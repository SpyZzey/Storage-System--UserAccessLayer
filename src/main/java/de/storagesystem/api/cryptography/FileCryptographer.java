package de.storagesystem.api.cryptography;

import java.io.File;

public interface FileCryptographer {

    void encryptFile(String key, byte[] contentBytes);
    void decryptFile(String key);
}
