package de.storagesystem.api.cryptography;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

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
     */
    @Override
    public byte[] decryptFile(byte[] encryptedBytes) {
        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap(encryptedBytes);
            int ivLength = byteBuffer.getInt();
            byte[] iv = new byte[ivLength];
            byteBuffer.get(iv);
            byte[] encryptedData = new byte[byteBuffer.remaining()];
            byteBuffer.get(encryptedData);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
            return cipher.doFinal(encryptedData);
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public byte[] encryptFile(byte[] contentBytes) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] iv = cipher.getIV();
            byte[] encryptedData;
            encryptedData = cipher.doFinal(contentBytes);
            ByteBuffer byteBuffer = ByteBuffer.allocate(4 + iv.length + encryptedData.length);
            byteBuffer.putInt(iv.length);
            byteBuffer.put(iv);
            byteBuffer.put(encryptedData);
            return byteBuffer.array();
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }


}
