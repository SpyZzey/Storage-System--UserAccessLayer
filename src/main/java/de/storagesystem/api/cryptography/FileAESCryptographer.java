package de.storagesystem.api.cryptography;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class FileAESCryptographer implements FileCryptographer {

    private SecretKey secretKey;
    private Cipher cipher;

    public FileAESCryptographer(SecretKey secretKey, String transformation) throws NoSuchPaddingException, NoSuchAlgorithmException {
        this.secretKey = secretKey;
        this.cipher = Cipher.getInstance(transformation);
    }

    /**
     * Encryptes a file and stores it at path
     * @param path
     * @param contentBytes
     */
    @Override
    public void encryptFile(String path, byte[] contentBytes) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] iv = cipher.getIV();

            FileOutputStream fs = new FileOutputStream(path);
            CipherOutputStream cos = new CipherOutputStream(fs, cipher);
            fs.write(iv);
            cos.write(contentBytes);
        } catch (InvalidKeyException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void decryptFile(String key) {

    }
}
