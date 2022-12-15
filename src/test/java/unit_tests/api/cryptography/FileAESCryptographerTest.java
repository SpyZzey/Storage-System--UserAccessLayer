package unit_tests.api.cryptography;

import de.storagesystem.api.cryptography.FileAESCryptographer;
import org.junit.jupiter.api.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileAESCryptographerTest {


    /**
     * Tests if the encryption and decryption of a file works
     * @throws NoSuchAlgorithmException if the algorithm is not supported
     * @throws NoSuchPaddingException if the padding is not supported
     */
    @Test
    public void encryptStringAndDecryptToStringTest() throws NoSuchAlgorithmException, NoSuchPaddingException {
        String content = "Hello World";
        String decryptedContent = null;

        SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();
        FileAESCryptographer cryptographer = new FileAESCryptographer(secretKey, "AES/CBC/PKCS5Padding");
        byte[] encryptedData = cryptographer.encryptFile(content.getBytes());

        decryptedContent = new String(cryptographer.decryptFile(encryptedData));
        assertEquals(content, decryptedContent);
    }

}
