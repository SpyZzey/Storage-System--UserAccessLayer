package de.storagesystem.api.cryptography;

import java.io.File;

public interface FileCryptographer {

    void encryptFile(String path, byte[] contentBytes);
    byte[] decryptFile(String path);
}
