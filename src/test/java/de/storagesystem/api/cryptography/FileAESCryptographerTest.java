package de.storagesystem.api.cryptography;

import org.junit.jupiter.api.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileAESCryptographerTest {

    @Test
    public void encryptStringAndDecryptToStringTest() {
        String content = "Hello World";
        String decryptedContent = null;

        try {
            SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();
            FileAESCryptographer cryptographer = new FileAESCryptographer(secretKey, "AES/CBC/PKCS5Padding");
            cryptographer.encryptFile("TestEncryptionDecryption.file", content.getBytes());

            decryptedContent = new String(cryptographer.decryptFile("TestEncryptionDecryption.file"));
            assertTrue(new File("TestEncryptionDecryption.file").delete());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        assertEquals(content, decryptedContent);
    }

}
