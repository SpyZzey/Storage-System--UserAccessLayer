package de.storagesystem.api.storage.servers;

import java.util.Optional;

public interface StorageServerCustomDAO {
    Optional<StorageServer> findStorageServerByIp(String host, int port);
}
