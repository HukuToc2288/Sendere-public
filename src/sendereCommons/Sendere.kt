package sendereCommons

import sendereCommons.Settings.nickname
import sendereCommons.Settings.visibility
import sendereCommons.protopackets.*
import com.google.protobuf.Any
import com.google.protobuf.ByteString
import com.google.protobuf.InvalidProtocolBufferException
import com.google.protobuf.Message
import lombok.SneakyThrows
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.*
import java.nio.charset.StandardCharsets
import java.security.*
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import java.util.zip.CRC32
import java.util.zip.Deflater
import java.util.zip.Inflater
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import kotlin.experimental.and

abstract class Sendere {


    companion object {
        //port 1337 lol
        //20 ports must be enough...
        //21.08.2019
        const val START_PORT = 56990
        const val END_PORT = 56999
        const val DISCOVERY_PORT = 1338

        val SUID = ThreadLocalRandom.current().nextLong()

        /*
     Those constants defines the version of Sendere library

     Minor version shows little changes in library which don't affect commands structure
     and makes only internal changes
     It usually can be ignored in version check

     Middle version shows that some commands have been added or changed but Sendere still
     can work some way with clients having another middle version
     Middle version should be checked when sending commands and if remote client cannot handle
     some request user should be notified about it

     Major version shows that significant changes have been made since previous and their significance
     doesn't allow clients with different versions to normally work together
     This version should be checked when discovering clients in network and Sendere shouldn't allow any
     communication between users if it mismatch

     VERSION is a version code in 3-byte format that can be used for sending or something

     29.06.2020 huku
     */
        const val MAJOR_VERSION = 0
        const val MIDDLE_VERSION = 0
        const val MINOR_VERSION = 1
        const val VERSION = MAJOR_VERSION shl 16 + MIDDLE_VERSION shl 8 + MINOR_VERSION

        /**
         * Size in bytes of oncoming and incoming messages that should be used instead of
         * hardcoded value.
         */
        const val PACKET_LENGTH = 1024 * 33
        const val DEFAULT_FLAGS: Byte = 0
        @JvmStatic
        @Throws(InvalidKeyException::class, NoSuchPaddingException::class, NoSuchAlgorithmException::class, BadPaddingException::class, IllegalBlockSizeException::class)
        fun decrypt(data: ByteArray?, publicKey: PublicKey?): Long {
            val cipher = Cipher.getInstance("RSA")
            cipher.init(Cipher.DECRYPT_MODE, publicKey)
            return Converters.bytesToLong(cipher.doFinal(data))
        }

        @Throws(InvalidKeyException::class, NoSuchPaddingException::class, NoSuchAlgorithmException::class, BadPaddingException::class, IllegalBlockSizeException::class)
        fun encrypt(secret: Long, publicKey: PublicKey?): ByteArray {
            val cipher = Cipher.getInstance("RSA")
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            return cipher.doFinal(Converters.longToBytes(secret))
        }
    }

    var mainPort = 0
    private var discoveryPort = 0
    private var serverSocket: ServerSocket? = null
    var discoverySocket: MulticastSocket? = null
    private val receiverThread: Thread? = null
    private val allowReceiving = true
    private var userReady = true
    val remoteUsers: RemoteUserList = RemoteUserList()
    private val transmissionsIn = HashMap<Long, TransmissionIn>()
    private val transmissionsOut = HashMap<Long, TransmissionOut>()
    private val inRequests = LinkedList<InRequest>()
    private var currentInRequest: InRequest? = null
    private val addressesToPing = HashSet<ArpEntry>()
    var inflater = Inflater()
    var deflater = Deflater(9)
    private lateinit var rsaKeyPair: KeyPair
    private var encryptedSecret: ByteArray? = null

    private fun onReceive(sender: RemoteUser, flags: Byte, data: ByteArray) {
        // empty packet
        var data = data
        if (data.isEmpty()) return
        val compressedDataLength = data.size
        if (flags and 1 != 0.toByte()) {
            data = inflate(data)
        }
        val anyPacket: Any
        anyPacket = try {
            Any.parseFrom(data)
        } catch (e: InvalidProtocolBufferException) {
            sendErrorMessage(sender, 0.toByte(), RemoteErrorPacket.ErrorType.NOT_PROTOBUF)
            return
        }
        if (anyPacket.`is`(PingPacket::class.java)) {
            var packet: PingPacket = try {
                anyPacket.unpack(PingPacket::class.java)
            } catch (e: InvalidProtocolBufferException) {
                sendErrorMessage(sender, 0.toByte(), RemoteErrorPacket.ErrorType.INVALID_FORMAT, PingPacket::class.java.simpleName)
                return
            }
            val remoteSUID = packet.suid
            val remoteNick = packet.nickname
            // self-ping
            // 11.04.2021 huku
            if (remoteSUID == SUID && remoteNick == nickname) return
            val existingUser = remoteUsers.getByHash(remoteSUID)
            if (existingUser != null) {
                onRemoteUserUpdated(existingUser)
            } else {
                sender.identify(remoteNick, remoteSUID)
                remoteUsers.put(sender)
                onRemoteUserConnected(sender)
                if (visibility == 1) {
                    val byteSUID = Converters.longToBytes(SUID)
                    val byteNickname = nickname.toByteArray(StandardCharsets.UTF_8)
                    val nicknameLength = byteArrayOf(byteNickname.size.toByte())
                    sendMessage(sender, 0.toByte(), PongPacket.newBuilder().setSuid(SUID).setNickname(nickname).build())
                }
            }
        } else if (anyPacket.`is`(PongPacket::class.java)) {
            val packet: PongPacket
            packet = try {
                anyPacket.unpack(PongPacket::class.java)
            } catch (e: InvalidProtocolBufferException) {
                sendErrorMessage(sender, 0.toByte(), RemoteErrorPacket.ErrorType.INVALID_FORMAT, PongPacket::class.java.simpleName)
                return
            }
            val remoteSUID = packet.suid
            val remoteNick = packet.nickname
            // self-ping
            // 11.04.2021 huku
            // self-ping
            // 11.04.2021 huku
            if (remoteSUID == SUID && remoteNick == nickname) return
            val existingUser = remoteUsers.getByHash(remoteSUID)
            if (existingUser != null) {
                onRemoteUserUpdated(existingUser)
            } else {
                sender.identify(remoteNick, remoteSUID)
                remoteUsers.put(sender)
                onRemoteUserFound(sender)
            }
        } else if (anyPacket.`is`(RemoteErrorPacket::class.java)) {
            val packet: RemoteErrorPacket = try {
                anyPacket.unpack(RemoteErrorPacket::class.java)
            } catch (e: InvalidProtocolBufferException) {
                // TODO: 15.04.2021 we can face circular error sending if both clients will constantly throw exceptions
                // 15.04.2021 huku
                sendErrorMessage(sender, 0.toByte(), RemoteErrorPacket.ErrorType.INVALID_FORMAT, RemoteErrorPacket::class.java.simpleName)
                return
            }
            onRemoteErrorReceived(sender, packet.errorType, packet.extraMessage)
        } else if (anyPacket.`is`(TextPacket::class.java)) {
            val packet: TextPacket
            packet = try {
                anyPacket.unpack(TextPacket::class.java)
            } catch (e: InvalidProtocolBufferException) {
                sendErrorMessage(sender, 0.toByte(), RemoteErrorPacket.ErrorType.INVALID_FORMAT, TextPacket::class.java.simpleName)
                return
            }
            if (Settings.allowReceiving) {
                onTextMessageReceived(sender, packet.text)
            } else {
                sendErrorMessage(sender, 0.toByte(), RemoteErrorPacket.ErrorType.CHAT_NOT_ALLOWED)
            }
        } else if (anyPacket.`is`(SendRequestPacket::class.java)) {
            val packet: SendRequestPacket
            packet = try {
                anyPacket.unpack(SendRequestPacket::class.java)
            } catch (e: InvalidProtocolBufferException) {
                sendErrorMessage(sender, 0.toByte(), RemoteErrorPacket.ErrorType.INVALID_FORMAT, SendRequestPacket::class.java.simpleName)
                return
            }
            if (!Settings.allowReceiving) {
                // TODO: 21.06.2020 make special message if user don't allows to receive files
                /*
                 * Now requests process as it canceled by user, so sender unable to determine
                 * if request really was canceled by user or user don't allows to receive files at all
                 * 21.06.2020 huku
                 */
                processSendRequest(false, TransmissionIn.createDummyTransmission(sender, packet.transmissionId))
            } else {
                val request = InRequest(sender, packet.isDirectory, packet.transmissionId, packet.fileName)
                if (userReady) {
                    userReady = false
                    currentInRequest = request
                    onSendRequest(request)
                } else {
                    inRequests.add(request)
                }
            }
        } else if (anyPacket.`is`(SendResponsePacket::class.java)) {
            val packet: SendResponsePacket
            packet = try {
                anyPacket.unpack(SendResponsePacket::class.java)
            } catch (e: InvalidProtocolBufferException) {
                sendErrorMessage(sender, 0.toByte(), RemoteErrorPacket.ErrorType.INVALID_FORMAT, SendResponsePacket::class.java.simpleName)
                return
            }
            val transmission = transmissionsOut[packet.transmissionId]
            if (transmission != null) {
                if (packet.accepted) {
                    onSendResponse(true, transmission)
                    val transmissionThread = Thread { transmission.start() }
                    transmissionThread.start()
                } else {
                    onSendResponse(false, transmission)
                    transmissionsOut.remove(packet.transmissionId)
                }
            }
        } else if (anyPacket.`is`(CreateFilePacket::class.java)) {
            val packet: CreateFilePacket
            packet = try {
                anyPacket.unpack(CreateFilePacket::class.java)
            } catch (e: InvalidProtocolBufferException) {
                sendErrorMessage(sender, 0.toByte(), RemoteErrorPacket.ErrorType.INVALID_FORMAT, CreateFilePacket::class.java.simpleName)
                return
            }
            val transmission = transmissionsIn[packet.transmissionId]
            transmission?.createFile(packet.fileName)
        } else if (anyPacket.`is`(CreateDirectoryPacket::class.java)) {
            val packet: CreateDirectoryPacket
            packet = try {
                anyPacket.unpack(CreateDirectoryPacket::class.java)
            } catch (e: InvalidProtocolBufferException) {
                sendErrorMessage(sender, 0.toByte(), RemoteErrorPacket.ErrorType.INVALID_FORMAT, CreateDirectoryPacket::class.java.simpleName)
                return
            }
            val transmission = transmissionsIn[packet.transmissionId]
            transmission?.createDirectory(packet.fileName)
        } else if (anyPacket.`is`(RawDataPacket::class.java)) {
            val packet: RawDataPacket
            packet = try {
                anyPacket.unpack(RawDataPacket::class.java)
            } catch (e: InvalidProtocolBufferException) {
                sendErrorMessage(sender, 0.toByte(), RemoteErrorPacket.ErrorType.INVALID_FORMAT, RawDataPacket::class.java.simpleName)
                return
            }
            val transmission = transmissionsIn[packet.transmissionId]
            if (transmission != null) {
                transmission.realData += compressedDataLength.toLong()
                transmission.writeToFile(packet.data.toByteArray())
            }
        } else if (anyPacket.`is`(CloseFilePacket::class.java)) {
            val packet: CloseFilePacket
            packet = try {
                anyPacket.unpack(CloseFilePacket::class.java)
            } catch (e: InvalidProtocolBufferException) {
                sendErrorMessage(sender, 0.toByte(), RemoteErrorPacket.ErrorType.INVALID_FORMAT, CloseFilePacket::class.java.simpleName)
                return
            }
            val transmission = transmissionsIn[packet.transmissionId]
            transmission?.closeFile()
        } else if (anyPacket.`is`(TransmissionControlPacket::class.java)) {
            val packet: TransmissionControlPacket
            packet = try {
                anyPacket.unpack(TransmissionControlPacket::class.java)
            } catch (e: InvalidProtocolBufferException) {
                sendErrorMessage(sender, 0.toByte(), RemoteErrorPacket.ErrorType.INVALID_FORMAT, TransmissionControlPacket::class.java.simpleName)
                return
            }
            val transmission = transmissionsIn[packet.transmissionId]
            if (transmission != null) {
                val signal = packet.signal
                when (signal) {
                    TransmissionControlPacket.Signal.SENDING_CANCELED -> {
                    }
                    TransmissionControlPacket.Signal.RECEIVING_CANCELED -> {
                    }
                    TransmissionControlPacket.Signal.SENDING_COMPLETE -> transmission.onDone()
                    TransmissionControlPacket.Signal.RECEIVING_COMPLETE -> {
                    }
                }
            }
        } else if (anyPacket.`is`(AuthenticationStage1Packet::class.java)) {
            val packet: AuthenticationStage1Packet
            packet = try {
                anyPacket.unpack(AuthenticationStage1Packet::class.java)
            } catch (e: InvalidProtocolBufferException) {
                sendErrorMessage(sender, 0.toByte(), RemoteErrorPacket.ErrorType.INVALID_FORMAT, AuthenticationStage1Packet::class.java.simpleName)
                return
            }
            val remoteEncryptedSecret = packet.encryptedSecret.toByteArray()
            val authStage1Passed = sender.doAuthenticationStage1(packet.secret, remoteEncryptedSecret, packet.publicKey.toByteArray())
            if (authStage1Passed) {
                try {
                    val crc32 = CRC32()
                    for (b in encryptedSecret!!) {
                        crc32.update(b.toInt())
                    }
                    for (b in remoteEncryptedSecret) {
                        crc32.update(b.toInt())
                    }
                    sendMessage(sender, AuthenticationStage2Packet.newBuilder()
                            .setEncryptedSecret(ByteString.copyFrom(encryptedSecret))
                            .setPublicKey(ByteString.copyFrom(rsaKeyPair.public.encoded))
                            .build())
                    onAuthenticationRequestReceived(crc32.value)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            sendErrorMessage(sender, flags, RemoteErrorPacket.ErrorType.UNRECOGNIZED_PACKET, anyPacket.initializationErrorString)
        }
    }

    protected abstract fun onAuthenticationRequestReceived(secret: Long)
    @SneakyThrows
    private fun inflate(data: ByteArray): ByteArray {
        val inflaterBuffer = ByteArray(1024 * 1024)
        inflater.setInput(data)
        val length = inflater.inflate(inflaterBuffer)
        val inflated = ByteArray(length)
        System.arraycopy(inflaterBuffer, 0, inflated, 0, length)
        inflater.reset()
        return inflated
    }

    private fun deflate(data: ByteArray): ByteArray {
        val deflaterBuffer = ByteArray(1024 * 1024)
        deflater.reset()
        deflater.setInput(data)
        deflater.finish()
        var length = 0
        while (!deflater.finished()) {
            length += deflater.deflate(deflaterBuffer)
        }
        if (length == 0) {
            val a = 0
            deflater.reset()
            deflater.setInput(data)
            deflater.finish()
            length = deflater.deflate(deflaterBuffer)
        }
        val deflated = ByteArray(length)
        System.arraycopy(deflaterBuffer, 0, deflated, 0, length)
        return deflated
    }

    fun sendErrorMessage(user: RemoteUser, flags: Byte, type: RemoteErrorPacket.ErrorType?): Boolean {
        return sendMessage(user, flags, Any.pack(RemoteErrorPacket.newBuilder().setErrorType(type).build()).toByteArray())
    }

    fun sendErrorMessage(user: RemoteUser, flags: Byte, type: RemoteErrorPacket.ErrorType?, errorMessage: String?): Boolean {
        return sendMessage(user, flags, Any.pack(RemoteErrorPacket.newBuilder().setErrorType(type).setExtraMessage(errorMessage).build()).toByteArray())
    }

    protected abstract fun onRemoteErrorReceived(user: RemoteUser, errorType: RemoteErrorPacket.ErrorType?, extraMessage: String?)

    /**
     * Calls when updated some remote user info (e.g. add new IP address)
     *
     * @param user remote user whose info updated
     */
    abstract fun onRemoteUserUpdated(user: RemoteUser)

    /**
     * Calls when new remote user connected to local network and declared itself
     *
     * @param user connected user
     */
    abstract fun onRemoteUserConnected(user: RemoteUser)

    /**
     * Calls when already connected user was found by Sendere
     *
     * @param user found user
     */
    abstract fun onRemoteUserFound(user: RemoteUser)

    /**
     * Calls when some text data received
     *
     * @param who     remote user who send message
     * @param message message content
     */
    abstract fun onTextMessageReceived(who: RemoteUser, message: String?)

    /**
     * Calls when remote user trying to send file or directory to the client
     *
     * @param request class which contains request data
     */
    abstract fun onSendRequest(request: InRequest)
    abstract fun onSendResponse(allow: Boolean, transmission: TransmissionOut)
    abstract fun onUserDisconnected(remoteUser: RemoteUser)
    fun startReceiving() {
        val thread = Thread {
            while (allowReceiving) {
                try {
                    val unidentifiedUser: RemoteUser = object : RemoteUser(serverSocket!!.accept()) {
                        override fun onDisconnected() {
                            onUserDisconnected(this)
                            remoteUsers.removeByHash(hash)
                        }

                        override fun onReceived(flags: Byte, data: ByteArray) {
                            this@Sendere.onReceive(this, flags, data)
                        }
                    }
                } catch (e: IOException) {
                    /*e.printStackTrace();*/
                }
            }
        }
        thread.start()
    }

    var stopNeighbourRead = false

    private fun readNeighbourTable(): HashSet<ArpEntry> {
        val addressList = HashSet<ArpEntry>()
        var p: Process? = null
        try {
            println("Running ip neigh")
            p = Runtime.getRuntime().exec("ip neigh")
            //p.waitFor();
            val reader = BufferedReader(InputStreamReader(p.inputStream))
            var line = ""
            while (reader.readLine().also { line = it } != null) {
                // TODO: make invalid neighbour records detection more reliable and andd ipv6 support
                // 24.02.2021 huku
                if (!line.contains(":")) {
                    continue
                }
                val ipString = line.substring(0, line.indexOf(' '))
                if (ipString.contains(".")) {
                    // ipv4 address
                    val ipBytes = ByteArray(4)
                    val ipBytesString = ipString.split("\\.".toRegex()).toTypedArray()
                    if (ipBytesString.size != 4) continue
                    for (i in 0..3) {
                        ipBytes[i] = ipBytesString[i].toInt().toByte()
                    }
                    addressList.add(ArpEntry(ipBytes, START_PORT))
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressList
    }

    protected abstract fun onInternalError(code: Int, message: String?)
    private fun intToByteArray(ints: IntArray): ByteArray {
        val bytes = ByteArray(ints.size)
        for (i in ints.indices) {
            bytes[i] = ints[i].toByte()
        }
        return bytes
    }

    /*    public void addOrUpdateRemoteUser(String getNickname(), long hash, Socket socket) {
        boolean isNewUser = true;
        for (RemoteUser user : remoteUsers) {
            if (user.hash == hash && user.getNickname().equals(getNickname())) {
                user.addAddress(address);
                isNewUser = false;
                break;
            }
        }
        if (isNewUser)
            remoteUsers.add(new RemoteUser(getNickname(), hash, address, port));
    }*/
    fun sendMessage(remoteUser: RemoteUser, header: Byte, message: String): Boolean {
        return sendMessage(remoteUser, header, message.toByteArray(StandardCharsets.UTF_8), message.toByteArray().size)
    }

    fun sendMessage(remoteUser: RemoteUser, header: Byte, data: ByteArray?, length: Int): Boolean {
        return remoteUser.sendMessage(header, data, length)
    }

    fun sendMessage(remoteUser: RemoteUser, flags: Byte, packet: Message): Boolean {
        var flags = flags
        var anyBytes = Any.pack(packet).toByteArray()
        if (flags and 1 != 0.toByte()) {
            val deflated = deflate(anyBytes)
            if (deflated.size < anyBytes.size) {
                anyBytes = deflated
            } else {
                flags = 0
            }
        }
        return sendMessage(remoteUser, flags, anyBytes)
    }

    fun sendMessage(remoteUser: RemoteUser, message: Message): Boolean {
        return sendMessage(remoteUser, DEFAULT_FLAGS, message)
    }

    fun sendMessage(remoteUser: RemoteUser, flags: Byte, vararg data: ByteArray?): Boolean {
        return remoteUser.sendMessage(flags, *data)
    }

    /*    public RemoteUser findRemoteUserByAddress(String address) {
        for (RemoteUser user : remoteUsers) {
            for (String s : user.getAddresses()) {
                if (s.equals(address))
                    return user;
            }
        }
        return null;
    }*/
    fun processSendRequest(allow: Boolean, transmission: TransmissionIn) {
        if (allow) transmissionsIn[transmission.id] = transmission
        sendMessage(transmission.user, 0.toByte(), Any.pack(SendResponsePacket.newBuilder().setAccepted(allow)
                .setTransmissionId(transmission.id).build()).toByteArray())
        if (!inRequests.isEmpty()) {
            currentInRequest = inRequests.removeLast()
            onSendRequest(currentInRequest!!)
        } else {
            userReady = true
        }
    }

    fun createRemoteDirectory(relativePath: String?, transmission: TransmissionOut): Boolean {
        return sendMessage(transmission.user, 0.toByte(), CreateDirectoryPacket.newBuilder()
                .setFileName(relativePath)
                .setTransmissionId(transmission.id)
                .build())
    }

    fun createRemoteFile(relativePath: String?, transmission: TransmissionOut): Boolean {
        return sendMessage(transmission.user, 0.toByte(), CreateFilePacket.newBuilder()
                .setFileName(relativePath)
                .setTransmissionId(transmission.id)
                .build())
    }

    fun addTransmissionOut(transmission: TransmissionOut) {
        transmissionsOut[transmission.id] = transmission
    }

    fun sendTransmissionRequest(transmission: TransmissionOut): Boolean {
        return sendMessage(transmission.user, 0.toByte(), Any.pack(SendRequestPacket.newBuilder()
                .setFileName(transmission.filename)
                .setIsDirectory(transmission.isDirectory)
                .setTransmissionId(transmission.id)
                .build()).toByteArray())
    }

    fun sendTextMessage(user: RemoteUser, message: String?): Boolean {
        return sendMessage(user, 0.toByte(), Any.pack(TextPacket.newBuilder().setText(message).build()).toByteArray())
    }

    fun requestAuthentication(user: RemoteUser): Boolean {
        val cipher: Cipher
        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.ENCRYPT_MODE, rsaKeyPair.public)
            val packet = AuthenticationStage1Packet.newBuilder()
                    .setSecret(SUID)
                    .setPublicKey(ByteString.copyFrom(rsaKeyPair.public.encoded))
                    .setEncryptedSecret(ByteString.copyFrom(cipher.doFinal(Converters.longToBytes(SUID))))
                    .build()
            sendMessage(user, packet)
        } catch (e: Exception) {
            return false
        }
        return true
    }

    init {
        for (i in START_PORT..END_PORT) {
            try {
                serverSocket = ServerSocket(i)
                mainPort = i
                break
            } catch (e: IOException) {
                //e.printStackTrace();
            }
        }
        if (serverSocket == null) {
            throw RuntimeException("There are no free TCP ports in range " + START_PORT + "-" + END_PORT + ". Sendere need at least one of them to run.")
        }
        deflater.setStrategy(Deflater.HUFFMAN_ONLY)
        var keyPairGenerator: KeyPairGenerator? = null
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA")
            rsaKeyPair = keyPairGenerator.generateKeyPair()
            val cipher = Cipher.getInstance("RSA")
            cipher.init(Cipher.ENCRYPT_MODE, rsaKeyPair.getPrivate())
            encryptedSecret = cipher.doFinal(Converters.longToBytes(SUID))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        startReceiving()
        var clientDiscover = object : ClientDiscover(NetworkInterface.getNetworkInterfaces().toList(), mainPort){
            override fun onDiscoveryReceived(remoteSUID: Long, address: InetAddress, port: Int) {
                if(remoteUsers?.getByHash(remoteSUID)  == null){
                    var unidentifiedUser = object : RemoteUser(Socket(address, port)){
                        override fun onDisconnected() {
                            onUserDisconnected(this)
                            remoteUsers!!.removeByHash(hash)
                        }

                        override fun onReceived(flags: Byte, data: ByteArray?) {
                            this@Sendere.onReceive(this, flags, data!!)
                        }
                    }
                    sendMessage(unidentifiedUser, 0.toByte(), Any.pack(PingPacket.newBuilder().setSuid(SUID).setNickname(nickname).build()).toByteArray())
                }
            }
        }
    }
}