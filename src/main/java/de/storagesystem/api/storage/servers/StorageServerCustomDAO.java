package de.storagesystem.api.storage.servers;

import java.util.Optional;

/**
 * @author Simon Brebeck
 */
public interface StorageServerCustomDAO {

    /**
     * Returns an optional containing the {@link StorageServer} by the ip and port, if it exists.
     * @param host The ip of the server.
     * @param port The port of the server.
     * @return an optional containing the server, if it exists.
     */
    Optional<StorageServer> findStorageServerByIp(String host, int port);
}
