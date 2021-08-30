package sendereCommons

import com.google.protobuf.Any
import com.google.protobuf.ByteString
import com.google.protobuf.InvalidProtocolBufferException
import com.google.protobuf.Message
import lombok.SneakyThrows
import sendereCommons.Settings.nickname
import sendereCommons.Settings.visibility
import sendereCommons.protopackets.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.*
import java.nio.charset.StandardCharsets
import java.security.*
import java.security.spec.X509EncodedKeySpec
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import java.util.zip.Deflater
import java.util.zip.Inflater
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.interfaces.DHPublicKey
import kotlin.experimental.and
import kotlin.reflect.KClass

object Sendere {

    //port 1337 lol
    //20 ports must be enough...
    //21.08.2019
    const val START_PORT = 56990
    const val END_PORT = 56999
    private const val DISCOVERY_PORT = 1338

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

    const val DEFAULT_FLAGS: Byte = 0

    @JvmStatic
    @Throws(
        InvalidKeyException::class,
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class
    )
    fun decrypt(data: ByteArray?, publicKey: PublicKey?): Long {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, publicKey)
        return Converters.bytesToLong(cipher.doFinal(data))
    }

    @Throws(
        InvalidKeyException::class,
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class
    )
    fun encrypt(secret: Long, publicKey: PublicKey?): ByteArray {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinal(Converters.longToBytes(secret))
    }


    var mainPort = 0
    private var serverSocket: ServerSocket? = null
    private val allowReceiving = true
    private var userReady = true
    val remoteUsers: RemoteUserList = RemoteUserList()
    private val transmissionsIn = HashMap<Long, TransmissionIn>()
    private val transmissionsOut = HashMap<Long, TransmissionOut>()
    private val inRequests = LinkedList<InRequest>()
    private lateinit var currentInRequest: InRequest
    var inflater = Inflater()
    var deflater = Deflater(9)
    private lateinit var rsaKeyPair: KeyPair
    private var encryptedSecret: ByteArray? = null
    private var eventListeners = ArrayList<EventListener>()

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
            val packetType = PingPacket::class.java
            val packet = anyPacket.unpackOrNull(packetType) ?: run {
                sendInvalidFormatErrorMessage(sender, packetType)
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
            val packetType = PongPacket::class.java
            val packet = anyPacket.unpackOrNull(packetType) ?: run {
                sendInvalidFormatErrorMessage(sender, packetType)
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
                startDiffieHellman(sender)
            }
        } else if (anyPacket.`is`(RemoteErrorPacket::class.java)) {
            val packetType = RemoteErrorPacket::class.java
            val packet = anyPacket.unpackOrNull(packetType) ?: run {
                // TODO: 15.04.2021 we can face circular error sending if both clients will constantly throw exceptions
                sendInvalidFormatErrorMessage(sender, packetType)
                return
            }
            onRemoteErrorReceived(sender, packet.errorType, packet.extraMessage)
        } else if (anyPacket.`is`(TextPacket::class.java)) {
            val packetType = TextPacket::class.java
            val packet = anyPacket.unpackOrNull(packetType) ?: run {
                sendInvalidFormatErrorMessage(sender, packetType)
                return
            }
            if (Settings.allowReceiving) {
                onTextMessageReceived(sender, packet.text)
            } else {
                sendErrorMessage(sender, 0.toByte(), RemoteErrorPacket.ErrorType.CHAT_NOT_ALLOWED)
            }
        } else if (anyPacket.`is`(SendRequestPacket::class.java)) {
            val packetType = SendRequestPacket::class.java
            val packet = anyPacket.unpackOrNull(packetType) ?: run {
                sendInvalidFormatErrorMessage(sender, packetType)
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
                    onSendRequestReceived(sender, request)
                } else {
                    inRequests.add(request)
                }
            }
        } else if (anyPacket.`is`(SendResponsePacket::class.java)) {
            val packetType = SendResponsePacket::class.java
            val packet = anyPacket.unpackOrNull(packetType) ?: run {
                sendInvalidFormatErrorMessage(sender, packetType)
                return
            }
            val transmission = transmissionsOut.remove(packet.transmissionId)
            if (transmission != null) {
                onSendResponseReceived(sender, transmission,packet.accepted)
            }
        } else if (anyPacket.`is`(CreateFilePacket::class.java)) {
            val packetType = CreateFilePacket::class.java
            val packet = anyPacket.unpackOrNull(packetType) ?: run {
                sendInvalidFormatErrorMessage(sender, packetType)
                return
            }
            val transmission = transmissionsIn[packet.transmissionId]
            transmission?.createFile(packet.fileName)
        } else if (anyPacket.`is`(CreateDirectoryPacket::class.java)) {
            val packetType = CreateDirectoryPacket::class.java
            val packet = anyPacket.unpackOrNull(packetType) ?: run {
                sendInvalidFormatErrorMessage(sender, packetType)
                return
            }
            val transmission = transmissionsIn[packet.transmissionId]
            transmission?.createDirectory(packet.fileName)
        } else if (anyPacket.`is`(RawDataPacket::class.java)) {
            val packetType = RawDataPacket::class.java
            val packet = anyPacket.unpackOrNull(packetType) ?: run {
                sendInvalidFormatErrorMessage(sender, packetType)
                return
            }
            val transmission = transmissionsIn[packet.transmissionId]
            if (transmission != null) {
                transmission.realData += compressedDataLength.toLong()
                transmission.writeToFile(packet.data.toByteArray())
            }
        } else if (anyPacket.`is`(CloseFilePacket::class.java)) {
            val packetType = CloseFilePacket::class.java
            val packet = anyPacket.unpackOrNull(packetType) ?: run {
                sendInvalidFormatErrorMessage(sender, packetType)
                return
            }
            val transmission = transmissionsIn[packet.transmissionId]
            transmission?.closeFile()
        } else if (anyPacket.`is`(TransmissionControlPacket::class.java)) {
            val packetType = TransmissionControlPacket::class.java
            val packet = anyPacket.unpackOrNull(packetType) ?: run {
                sendInvalidFormatErrorMessage(sender, packetType)
                return
            }
            val transmission = transmissionsIn[packet.transmissionId]
            if (transmission != null) {
                when (packet.signal) {
                    TransmissionControlPacket.Signal.SENDING_CANCELED -> {
                    }
                    TransmissionControlPacket.Signal.RECEIVING_CANCELED -> {
                    }
                    TransmissionControlPacket.Signal.SENDING_COMPLETE -> transmission.onDone()
                    TransmissionControlPacket.Signal.RECEIVING_COMPLETE -> {
                    }
                    else -> {
                        // TODO: 26.06.2021 huku unknown signal
                    }
                }
            }
        } else if (anyPacket.`is`(DiffieHellmanPacket::class.java)) {
            val packetType = DiffieHellmanPacket::class.java
            val packet = anyPacket.unpackOrNull(packetType) ?: run {
                sendInvalidFormatErrorMessage(sender, packetType)
                return
            }

            val encodedKey = packet.encodedPublicKey.toByteArray()
            val senderGeneratedSecret = packet.senderGeneratedSecret

            if (!senderGeneratedSecret) {

                // отправка публичного ключа Алисы Бобу

                /*
                 * Let's turn over to Bob. Bob has received Alice's public key
                 * in encoded format.
                 * He instantiates a DH public key from the encoded key material.
                 */
                val bobKeyFac = KeyFactory.getInstance("DH")
                val x509KeySpec = X509EncodedKeySpec(encodedKey)

                val alicePubKey = bobKeyFac.generatePublic(x509KeySpec)
                val ivBytes = ByteArray(16)
                ThreadLocalRandom.current().nextBytes(ivBytes)
                sender.createKeyAgreementFromReceivedKey(alicePubKey as DHPublicKey, ivBytes)
                val bobPubKeyEnc = sender.keyPair.public.encoded

                val diffieHellmanPacket = DiffieHellmanPacket.newBuilder()
                    .setEncodedPublicKey(ByteString.copyFrom(bobPubKeyEnc))
                    .setSenderGeneratedSecret(true)
                    .setIv(ByteString.copyFrom(ivBytes))
                    .build()
                sendMessage(sender, diffieHellmanPacket)
            } else {
                val aliceKeyFac = KeyFactory.getInstance("DH")
                val x509KeySpec = X509EncodedKeySpec(encodedKey)

                val bobPubKey = aliceKeyFac.generatePublic(x509KeySpec)
                sender.updateKeyAgreementWithReceivedKey(bobPubKey as DHPublicKey, packet.iv.toByteArray())
                val cleartext = ByteArray(28)
                ThreadLocalRandom.current().nextBytes(cleartext)
                val encrypted = sender.encrypt(cleartext)

                val encryptionCheckPacket = EncryptionCheckPacket.newBuilder()
                    .setCleartext(ByteString.copyFrom(cleartext))
                    .setEncrypted(ByteString.copyFrom(encrypted))
                    .setSenderTrusts(false)
                    .build()
                sendMessage(sender, encryptionCheckPacket)

            }
        } else if (anyPacket.`is`(EncryptionCheckPacket::class.java)) {

            val packetType = EncryptionCheckPacket::class.java
            val packet = anyPacket.unpackOrNull(packetType) ?: run {
                sendInvalidFormatErrorMessage(sender, packetType)
                return
            }

            val receivedCleartext = packet.cleartext.toByteArray()
            if (receivedCleartext.contentEquals(sender.decrypt(packet.encrypted.toByteArray()))) {
                sender.encryptionDone = true
                if (!packet.senderTrusts) {
                    val cleartextToSend = ByteArray(28)
                    ThreadLocalRandom.current().nextBytes(cleartextToSend)
                    val encrypted = sender.encrypt(cleartextToSend)
                    val encryptionCheckPacket = EncryptionCheckPacket.newBuilder()
                        .setCleartext(ByteString.copyFrom(cleartextToSend))
                        .setEncrypted(ByteString.copyFrom(encrypted))
                        .setSenderTrusts(true)
                        .build()
                    sendMessage(sender, encryptionCheckPacket)
                }
                println("Holy cow, it works!");
            } else {
                println("As expected, it doesn't work")
            }
        } else {
            sendErrorMessage(
                sender,
                flags,
                RemoteErrorPacket.ErrorType.UNRECOGNIZED_PACKET,
                anyPacket.initializationErrorString
            )
        }
    }

    private fun <T : Message> Any.unpackOrNull(packetType: Class<T>): T? {
        return try {
            unpack(packetType) as T
        } catch (e: InvalidProtocolBufferException) {
            null
        }
    }

    private fun <T : Message> sendInvalidFormatErrorMessage(remoteUser: RemoteUser, packetType: Class<T>): Boolean {
        return sendErrorMessage(
            remoteUser,
            DEFAULT_FLAGS,
            RemoteErrorPacket.ErrorType.INVALID_FORMAT,
            packetType.simpleName
        )
    }

    public fun addEventListener(listener: EventListener) {
        eventListeners.add(listener)
    }

    public fun removeEventListener(listener: EventListener) {
        eventListeners.remove(listener)
    }

    public fun removeAllEventListeners() {
        eventListeners.clear()
    }

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
        val deflateBuffer = ByteArray(1024 * 1024)
        deflater.reset()
        deflater.setInput(data)
        deflater.finish()
        var length = 0
        while (!deflater.finished()) {
            length += deflater.deflate(deflateBuffer)
        }
        if (length == 0) {
            val a = 0
            deflater.reset()
            deflater.setInput(data)
            deflater.finish()
            length = deflater.deflate(deflateBuffer)
        }
        val deflated = ByteArray(length)
        System.arraycopy(deflateBuffer, 0, deflated, 0, length)
        return deflated
    }

    fun sendErrorMessage(user: RemoteUser, flags: Byte, type: RemoteErrorPacket.ErrorType): Boolean {
        return sendErrorMessage(user, flags, type, null)
    }

    fun sendErrorMessage(user: RemoteUser, flags: Byte, type: RemoteErrorPacket.ErrorType, errorMessage: String?): Boolean {
        return sendMessage(user, flags, Any.pack(RemoteErrorPacket.newBuilder().setErrorType(type)
            .also {
                if (errorMessage != null)
                    it.extraMessage = errorMessage
            }.build()).toByteArray())
    }

    private fun onRemoteErrorReceived(remoteUser: RemoteUser, errorType: RemoteErrorPacket.ErrorType, extraMessage: String?) {
        val iterator = eventListeners.iterator()
        while (iterator.hasNext()) {
            iterator.next().onRemoteErrorReceived(remoteUser, errorType, extraMessage)
        }
    }

    /**
     * Calls when updated some remote user info (e.g. add new IP address)
     *
     * @param user remote user whose info updated
     */
    private fun onRemoteUserUpdated(remoteUser: RemoteUser) {
        val iterator = eventListeners.iterator()
        while (iterator.hasNext()) {
            iterator.next().onRemoteUserUpdated(remoteUser)
        }
    }

    /**
     * Calls when new remote user connected to local network and declared itself
     *
     * @param user connected user
     */
    private fun onRemoteUserConnected(remoteUser: RemoteUser) {
        val iterator = eventListeners.iterator()
        while (iterator.hasNext()) {
            iterator.next().onRemoteUserConnected(remoteUser)
        }
    }

    /**
     * Calls when already connected user was found by Sendere
     *
     * @param user found user
     */
    private fun onRemoteUserFound(remoteUser: RemoteUser) {
        val iterator = eventListeners.iterator()
        while (iterator.hasNext()) {
            iterator.next().onRemoteUserFound(remoteUser)
        }
    }

    /**
     * Calls when some text data received
     *
     * @param user     remote user who send message
     * @param message message content
     */
    private fun onTextMessageReceived(remoteUser: RemoteUser, message: String) {
        val iterator = eventListeners.iterator()
        while (iterator.hasNext()) {
            iterator.next().onTextMessageReceived(remoteUser, message)
        }
    }

    /**
     * Calls when remote user trying to send file or directory to the client
     *
     * @param request class which contains request data
     */
    private fun onSendRequestReceived(remoteUser: RemoteUser, request: InRequest) {
        val iterator = eventListeners.iterator()
        while (iterator.hasNext()) {
            iterator.next().onSendRequestReceived(remoteUser, request)
        }
    }

    private fun onSendResponseReceived(remoteUser: RemoteUser, transmissionOut: TransmissionOut, allow: Boolean) {
        val iterator = eventListeners.iterator()
        while (iterator.hasNext()) {
            iterator.next().onSendResponseReceived(remoteUser,transmissionOut, allow)
        }
    }

    private fun onRemoteUserDisconnected(remoteUser: RemoteUser) {
        val iterator = eventListeners.iterator()
        while (iterator.hasNext()) {
            iterator.next().onRemoteUserDisconnected(remoteUser)
        }
    }

    fun startReceiving() {
        val thread = Thread {
            while (allowReceiving) {
                try {
                    val unidentifiedUser: RemoteUser = object : RemoteUser(serverSocket!!.accept()) {
                        override fun onDisconnected() {
                            onRemoteUserDisconnected(this)
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

    private fun startDiffieHellman(remoteUser: RemoteUser) {
        remoteUser.createKeyAgreement()
        val alicePubKeyEnc = remoteUser.keyPair.public.encoded
        val diffieHellmanPacket = DiffieHellmanPacket.newBuilder()
            .setEncodedPublicKey(ByteString.copyFrom(alicePubKeyEnc))
            .setSenderGeneratedSecret(false)
            .build()
        sendMessage(remoteUser, diffieHellmanPacket)
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

    fun sendMessage(remoteUser: RemoteUser, header: Byte, data: ByteArray): Boolean {
        return remoteUser.sendMessage(header, data)
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
        return remoteUser.sendMessage(flags, anyBytes)
    }

    fun sendMessage(remoteUser: RemoteUser, message: Message): Boolean {
        return sendMessage(remoteUser, DEFAULT_FLAGS, message)
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
        sendMessage(
            transmission.user, 0.toByte(), SendResponsePacket.newBuilder().setAccepted(allow)
                .setTransmissionId(transmission.id).build()
        )
        if (!inRequests.isEmpty()) {
            currentInRequest = inRequests.removeLast()!!
            onSendRequestReceived(currentInRequest!!.who, currentInRequest!!)
        } else {
            userReady = true
        }
    }

    fun createRemoteDirectory(relativePath: String?, transmission: TransmissionOut): Boolean {
        return sendMessage(
            transmission.user, 0.toByte(), CreateDirectoryPacket.newBuilder()
                .setFileName(relativePath)
                .setTransmissionId(transmission.id)
                .build()
        )
    }

    fun createRemoteFile(relativePath: String?, transmission: TransmissionOut): Boolean {
        return sendMessage(
            transmission.user, 0.toByte(), CreateFilePacket.newBuilder()
                .setFileName(relativePath)
                .setTransmissionId(transmission.id)
                .build()
        )
    }

    fun addTransmissionOut(transmission: TransmissionOut) {
        transmissionsOut[transmission.id] = transmission
    }

    fun sendTransmissionRequest(transmission: TransmissionOut): Boolean {
        return sendMessage(
            transmission.user, 0.toByte(), SendRequestPacket.newBuilder()
                .setFileName(transmission.filename)
                .setIsDirectory(transmission.isDirectory)
                .setTransmissionId(transmission.id)
                .build()
        )
    }

    fun sendTextMessage(user: RemoteUser, message: String?): Boolean {
        return sendMessage(user, 0.toByte(), TextPacket.newBuilder().setText(message).build())
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
            cipher.init(Cipher.ENCRYPT_MODE, rsaKeyPair.private)
            encryptedSecret = cipher.doFinal(Converters.longToBytes(SUID))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        startReceiving()
        val clientDiscover = object : ClientDiscover(NetworkInterface.getNetworkInterfaces().toList(), mainPort) {
            override fun onDiscoveryReceived(remoteSUID: Long, address: InetAddress, port: Int) {
                if (remoteUsers.getByHash(remoteSUID) == null) {
                    val unidentifiedUser = object : RemoteUser(Socket(address, port)) {
                        override fun onDisconnected() {
                            onRemoteUserDisconnected(this)
                            remoteUsers.removeByHash(hash)
                        }

                        override fun onReceived(flags: Byte, data: ByteArray) {
                            this@Sendere.onReceive(this, flags, data)
                        }
                    }
                    sendMessage(
                        unidentifiedUser,
                        0.toByte(),
                        PingPacket.newBuilder().setSuid(SUID).setNickname(nickname).build()
                    )
                }
            }
        }
        clientDiscover.start()
    }

    private fun getProtobufPacketType(anyPacket: Any): Class<Message>? {
        // TODO: huku 30.08.2021 stub
        return null
    }
}