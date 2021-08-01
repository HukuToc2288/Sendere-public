package sendereCommons

import lombok.Data
import lombok.Getter
import lombok.Setter
import lombok.SneakyThrows
import sendereCommons.Sendere.Companion.decrypt
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.net.SocketException
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import javax.crypto.Cipher
import javax.crypto.KeyAgreement
import javax.crypto.interfaces.DHPublicKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

abstract class RemoteUser {
    var nickname: String? = null
    var hash: Long = 0
    private val port = 0
    private var isIdentified = false

    private var disconnected = false
    private var lastAliveTime: Long = 0

    private lateinit var inputStream: InputStream
    private lateinit var outputStream: OutputStream
    private lateinit var socket: Socket

    lateinit var keyPair: KeyPair
    private lateinit var keyAgreement: KeyAgreement
    private lateinit var sharedSecret: ByteArray
    private lateinit var encryptionCipher: Cipher
    private lateinit var decryptionCipher: Cipher

    var encryptionDone = false
    fun createKeyAgreement(): Boolean {
        return try {
            val aliceKpairGen = KeyPairGenerator.getInstance("DH")
            aliceKpairGen.initialize(2048)
            keyPair = aliceKpairGen.generateKeyPair()
            keyAgreement = KeyAgreement.getInstance("DH")
            keyAgreement.init(keyPair.private)
            println("Stage 1 done")
            true
        } catch (e: Exception) {
            // algorithm not supported
            println("Stage 1 failed")
            false
        }
    }

    fun createKeyAgreementFromReceivedKey(publicKey: DHPublicKey, ivBytes: ByteArray?): Boolean {
        return try {
            val bobKpairGen = KeyPairGenerator.getInstance("DH")
            bobKpairGen.initialize(publicKey.params)
            keyPair = bobKpairGen.generateKeyPair()
            keyAgreement = KeyAgreement.getInstance("DH")
            keyAgreement.init(keyPair.private)
            keyAgreement.doPhase(publicKey, true)
            sharedSecret = keyAgreement.generateSecret()
            val bobAesKey = SecretKeySpec(sharedSecret, 0, 16, "AES")
            encryptionCipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            encryptionCipher.init(Cipher.ENCRYPT_MODE, bobAesKey, IvParameterSpec(ivBytes))
            decryptionCipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            decryptionCipher.init(Cipher.DECRYPT_MODE, bobAesKey, IvParameterSpec(ivBytes))
            println("Stage 2 done")
            true
        } catch (e: Exception) {
            // algorithm not supported
            e.printStackTrace()
            println("Stage 2 failed")
            false
        }
    }

    @SneakyThrows
    fun encrypt(cleartext: ByteArray): ByteArray {
        return encryptionCipher.doFinal(cleartext)
    }

    @SneakyThrows
    fun decrypt(encrypted: ByteArray): ByteArray {
        return decryptionCipher.doFinal(encrypted)
    }

    fun updateKeyAgreementWithReceivedKey(publicKey: DHPublicKey?, ivBytes: ByteArray): Boolean {
        return try {
            keyAgreement.doPhase(publicKey, true)
            sharedSecret = keyAgreement.generateSecret()
            val aliceAesKey = SecretKeySpec(sharedSecret, 0, 16, "AES")
            encryptionCipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            encryptionCipher.init(Cipher.ENCRYPT_MODE, aliceAesKey, IvParameterSpec(ivBytes))
            decryptionCipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            decryptionCipher.init(Cipher.DECRYPT_MODE, aliceAesKey, IvParameterSpec(ivBytes))
            println("Stage 3 done")
            true
        } catch (e: Exception) {
            println("Stage 3 failed")
            false
        }
    }

    constructor(nickname: String?, hash: Long, socket: Socket) {
        identify(nickname, hash)
        commonInitialization(socket)
    }

    constructor(socket: Socket) {
        commonInitialization(socket)
        val identifyTimer = Timer()
        identifyTimer.schedule(object : TimerTask() {
            override fun run() {
                if (!isIdentified) onDisconnectInternal()
            }
        }, 5000)
    }

    fun getPort(): Int {
        return socket.port
    }

    @Throws(IOException::class)
    private fun commonInitialization(socket: Socket) {
        this.socket = socket
        inputStream = socket.getInputStream()
        outputStream = socket.getOutputStream()
        lastAliveTime = System.currentTimeMillis()
        doReceiving()
        doAlive()
    }

    private fun doAlive() {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                sendAlive()
            }
        }, 0, 2000)
    }

    private fun doReceiving() {
        val receiverThread = Thread {
            while (!disconnected) {
                var read: Int
                val packetLength = ByteArray(4)
                var flags: Byte
                var length: Int
                try {
//                        while (inputStream.available() < 4 && !disconnected) {
////                            if (System.currentTimeMillis() > lastAliveTime + 5000)
////                                RemoteUser.this.onDisconnectInternal();
//                            System.out.println("waiting...");
//                            Thread.sleep(2000);
//                        }
                    var headerRead = 0
                    while (headerRead < 4 && !disconnected) {
                        headerRead += inputStream.read(packetLength, headerRead, 4 - headerRead)
                    }
                    length = (packetLength[0].toUByte().toInt() shl 16) +
                            + (packetLength[1].toUByte().toInt() shl 8) +
                            + (packetLength[2].toUByte().toInt())
                   // length = (packetLength[0] + if (packetLength[0] >= 0) 0 else 256 shl 16) + (packetLength[1] + if (packetLength[1] >= 0) 0 else 256 shl 8) + packetLength[2] + if (packetLength[2] >= 0) 0 else 256
                    //47 is '/' symbol's code
                    //18.09.2019
//                    if(packetLength[3]!=47)
//                        continue;
                    // flags = (byte) inputStream.read();
                    read = 0
                    val data = ByteArray(length)
                    while (read < length && !disconnected) read += inputStream.read(data, read, length - read)
                    lastAliveTime = System.currentTimeMillis()
                    onReceived(packetLength[3], data)
                } catch (e: SocketException) {
                    onDisconnectInternal()
                } catch (e: IOException) {
                    //e.printStackTrace();
                }
            }
            try {
                inputStream.close()
                outputStream.close()
            } catch (e: IOException) {
                //Okay
                //10.09.2019
                e.printStackTrace()
            }
        }
        receiverThread.start()
    }

    private fun onDisconnectInternal() {
        if (disconnected) return
        disconnected = true
        if (isIdentified) onDisconnected()
    }

    protected abstract fun onDisconnected()
    fun sendAlive(): Boolean {
        // Send headless packet to keep connection
        // 11.04.2021 huku

//        if (disconnected)
//            return false;
//        try {
//            // TODO: 11.04.2021 send flags: stub
//            sendingQueue.put(new SendingQueueElement(0, (byte) 0, null));
//            return true;
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        sendMessage(0.toByte(), byteArrayOf())
        return false
    }

    fun sendMessage(flags: Byte, data: ByteArray): Boolean {
        if (disconnected) return false
        var length = data.size
        // Don't pay attention on "Synchronization on a non-final field" warning
        // output stream never changing even if javac can't understand it
        // 19.09.2019 huku
        synchronized(outputStream) {
            try {
                outputStream.write(byteArrayOf((length and 0x00FF0000 shr 16).toByte(), (length and 0x0000FF00 shr 8).toByte(), (length and 0x000000FF).toByte()))
                outputStream.write(flags.toInt())
                if (length == 0) return true
                outputStream.write(data)
                return true
            } catch (e: IOException) {
                onDisconnectInternal()
            }
        }
        return false
    }

    override fun toString(): String {
        return nickname!!
    }

    fun identify(nickname: String?, hash: Long) {
        this.nickname = nickname
        this.hash = hash
        isIdentified = true
    }

    abstract fun onReceived(flags: Byte, data: ByteArray)
    val address: String
        get() = socket.inetAddress.hostAddress

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as RemoteUser
        return hash == that.hash && port == that.port && isIdentified == that.isIdentified && nickname == that.nickname && socket == that.socket
    }

    override fun hashCode(): Int {
        return Objects.hash(nickname, hash)
    }

    @Data
    private class SendingQueueElement(private val length: Int, private val flags: Byte, private val datas: Array<ByteArray>)
}