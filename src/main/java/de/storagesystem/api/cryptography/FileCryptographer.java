package de.storagesystem.api.cryptography;

/**
 * @author Simon Brebeck
 */
public interface FileCryptographer {

    /**
     * Encryptes a file and stores it at path
     *
     * @param contentBytes The bytes to encrypt
     * @return The encrypted bytes
     */
    byte[] encryptFile(byte[] contentBytes);

    /**
     * Decryptes a file and returns the decrypted bytes
     *
     * @param encryptedContentBytes The bytes to decrypt
     * @return The decrypted bytes
     */
    byte[] decryptFile(byte[] encryptedContentBytes);
}
