package testificate

import com.google.protobuf.Any
import com.google.protobuf.ByteString
import com.google.protobuf.Message
import sendereCommons.protopackets.PingPacket
import sendereCommons.protopackets.SignedPacket
import java.io.File
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*


fun main(args: Array<String>) {
    SigningUtils
}

object SigningUtils {
    val signingSignature: Signature
    val verificationSignature: Signature
    val saltGenerator = SecureRandom()

    init {
        if (!File("/raid/sendere/private.txt").exists() || !File("/raid/sendere/public.txt").exists()) {
            generateKeys()
        }

        val keyFactory = KeyFactory.getInstance("DSA")
        val privateKey = keyFactory.generatePrivate(PKCS8EncodedKeySpec(Base64.getDecoder()
            .decode(File("/raid/sendere/private.txt").readBytes())))
        val publicKey = keyFactory.generatePublic(X509EncodedKeySpec(Base64.getDecoder()
            .decode(File("/raid/sendere/public.txt").readBytes())))
        signingSignature = Signature.getInstance("SHA256WithDSA").apply { initSign(privateKey) }
        verificationSignature = Signature.getInstance("SHA256WithDSA").apply { initVerify(publicKey) }
    }

    private fun generateKeys() {
        val signature = Signature.getInstance("SHA256WithDSA")
        val secureRandom = SecureRandom()
        val keyPairGenerator = KeyPairGenerator.getInstance("DSA")
        val keyPair = keyPairGenerator.generateKeyPair()
        signature.initSign(keyPair.private, secureRandom)
        File("/raid/sendere/private.txt").writeBytes(Base64.getEncoder().encode(keyPair.private.encoded))
        File("/raid/sendere/public.txt").writeBytes(Base64.getEncoder().encode(keyPair.public.encoded))
    }

    private fun generateSignature(vararg data: ByteArray): ByteArray {
        for (d in data)
            signingSignature.update(d)
        return signingSignature.sign()
    }

    fun makeSignedPacket(packet: Message): SignedPacket {
        val salt = ByteArray(8).apply { saltGenerator.nextBytes(this) }
        val packetBytes = Any.pack(packet).toByteArray()
        return SignedPacket.newBuilder()
            .setNestedPacketBytes(ByteString.copyFrom(packetBytes))
            .setSalt(ByteString.copyFrom(salt))
            .setSignature(ByteString.copyFrom(generateSignature(packetBytes, salt)))
            .build()
    }

    fun verifyData(signature: ByteArray, vararg data: ByteArray): Boolean {
        for (d in data)
            verificationSignature.update(d)
        return (verificationSignature.verify(signature))
    }
}