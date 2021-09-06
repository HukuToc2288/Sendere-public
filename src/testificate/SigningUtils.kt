package testificate

import java.io.File
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*


fun main(args: Array<String>) {
    SigningUtils()
}

class SigningUtils {

    init {
        if (!File("/raid/sendere/private.txt").exists() || !File("/raid/sendere/public.txt").exists()) {
            generateKeys()
        }

        val keyFactory = KeyFactory.getInstance("DSA")
        val privateKey = keyFactory.generatePrivate(PKCS8EncodedKeySpec(Base64.getDecoder().decode(File("/raid/sendere/private.txt").readBytes())))
        val publicKey = keyFactory.generatePublic(X509EncodedKeySpec(Base64.getDecoder().decode(File("/raid/sendere/public.txt").readBytes())))
        val signingSignature = Signature.getInstance("SHA256WithDSA").apply { initSign(privateKey) }
        val verificationSignature = Signature.getInstance("SHA256WithDSA").apply { initVerify(publicKey) }
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


}