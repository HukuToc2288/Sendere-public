package testificate;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class RSATest {
    public void main(String[] args) throws Exception {
        KeyPairGenerator kpg;
        while (true) {
            try {
                kpg = KeyPairGenerator.getInstance("RSA");
                kpg.initialize(1024);
                KeyPair keyPair = kpg.generateKeyPair();
                if (keyPair.getPublic().getEncoded().length != 162)
                    return;
            } catch (NoSuchAlgorithmException e) {
                //go jump over the cliff if you cannot support RSA (using unencrypted connection)
                //28.03.2021 huku
                e.printStackTrace();
                return;
            }
        }
    }
}
