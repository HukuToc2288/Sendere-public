package testificate;

import sun.misc.CRC16;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class SigningTest {
    public static byte[] sign(byte[] data, PrivateKey privateKey) throws Exception {
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(data);

        byte[] signature = privateSignature.sign();

        return signature;
    }
    public static boolean verify(byte[] data, byte[] signatureBytes, byte[] encodedPublicKey) throws Exception {
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(decodePublicKey(encodedPublicKey));

        publicSignature.update(data);

        return publicSignature.verify(signatureBytes);
    }

    public static PublicKey decodePublicKey(byte[] byteKey){
        try{
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(byteKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(keySpec);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static PrivateKey decodePrivateKey(byte[] byteKey){
        try{
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(byteKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(keySpec);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048, new SecureRandom());
        KeyPair pair = generator.generateKeyPair();

        return pair;
    }

    public static void main(String[] args) throws Exception {
        KeyPair aliceKeyPair = generateKeyPair();
        KeyPair bobKeyPair = generateKeyPair();

        byte[] alicePublicKeySignature = sign(aliceKeyPair.getPublic().getEncoded(), aliceKeyPair.getPrivate());

        // Send signature and encoded public key to Bob

        byte[] bobPublicKeySignature = sign(bobKeyPair.getPublic().getEncoded(), bobKeyPair.getPrivate());
        // Send signature and encoded public key to Bob

        // Alice verifies Bob's public key and XOR with her public

        boolean isBobSignatureCorrect = verify(bobKeyPair.getPublic().getEncoded(), bobPublicKeySignature, bobKeyPair.getPublic().getEncoded());
        if (!isBobSignatureCorrect){
            System.out.println("Bob's signature incorrect!");
            return;
        }
        byte[] aliceXorArray = byteArrayXor(bobKeyPair.getPublic().getEncoded(), aliceKeyPair.getPublic().getEncoded());

        // Bob verifies Alice's public key and XOR with his public

        boolean isAliceSignatureCorrect = verify(aliceKeyPair.getPublic().getEncoded(), alicePublicKeySignature, aliceKeyPair.getPublic().getEncoded());
        if (!isAliceSignatureCorrect){
            System.out.println("Alice's signature incorrect!");
            return;
        }
        byte[] bobXorArray = byteArrayXor(aliceKeyPair.getPublic().getEncoded(), bobKeyPair.getPublic().getEncoded());

        // Alice calculating CRC16 and prints
        CRC16 aliceCrc = new CRC16();
        for (byte b: aliceXorArray){
            aliceCrc.update(b);
        }
        System.out.println("Alice's security code: "+String.format("%05d",aliceCrc.value));

        // Bob calculating CRC16 and prints
        CRC16 bobCrc = new CRC16();
        for (byte b: bobXorArray){
            bobCrc.update(b);
        }
        System.out.println("Bob's security code: "+String.format("%05d",bobCrc.value));
    }

    public static byte[] byteArrayXor(byte[] arr1, byte[] arr2){
        if (arr1.length < arr2.length){
            byte[] tempArray = arr1;
            arr1 = arr2;
            arr2 = tempArray;
        }
        byte[] xorArray = new byte[arr1.length];
        System.arraycopy(arr1,0,xorArray,0,arr1.length);
        for (int i=0;i<arr2.length;i++){
            xorArray[i]^=arr2[i];
        }
        return xorArray;
    }
}
