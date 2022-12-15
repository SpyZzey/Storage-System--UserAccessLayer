package de.storagesystem.api.cryptography;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Simon Brebeck
 */
public class FileAESCryptographer implements FileCryptographer {

    /**
     * An {@link SecretKey} instance used for encryption and decryption.
     */
    private final SecretKey secretKey;
    /**
     * An {@link Cipher} algorithm instance to encrypt and decrypt files.
     */
    private final Cipher cipher;

    public FileAESCryptographer(SecretKey secretKey, String transformation) throws NoSuchPaddingException, NoSuchAlgorithmException {
        this.secretKey = secretKey;
        this.cipher = Cipher.getInstance(transformation);
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public byte[] encryptFile(byte[] contentBytes) {
        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cos = new CipherOutputStream(outputStream, cipher)) {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] iv = cipher.getIV();

            // Write the encrypted bytes to the output stream.
            outputStream.write(iv);
            cos.write(contentBytes);
            return outputStream.toByteArray();
        } catch (InvalidKeyException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] decryptFile(byte[] encryptedContentBytes) {
        byte[] contentBytes;
        try(ByteArrayInputStream input = new ByteArrayInputStream(encryptedContentBytes);
            CipherInputStream cipherIn = new CipherInputStream(input, cipher);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] fileIv = new byte[16];
            input.read(fileIv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(fileIv));
            int length;
            byte[] buffer = new byte[4096];
            while((length = cipherIn.read(buffer, 0, buffer.length)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            contentBytes = outputStream.toByteArray();
        } catch (IOException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
        return contentBytes;
    }
}
