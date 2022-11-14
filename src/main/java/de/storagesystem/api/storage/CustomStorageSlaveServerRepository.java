package de.storagesystem.api.storage;

import java.util.Optional;

public interface CustomStorageSlaveServerRepository {
    Optional<StorageSlaveServer> findStorageServerByIp(String host, int port);
}
