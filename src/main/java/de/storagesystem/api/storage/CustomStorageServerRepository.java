package de.storagesystem.api.storage;

import java.util.Optional;

public interface CustomStorageServerRepository {
    Optional<StorageServer> findStorageServerByIp(String host, int port);
}
