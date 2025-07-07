package de.storagesystem.api.servers;

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

    /**
     * Returns an optional containing a {@link StorageServer} that has free/usable storage capacity and is online, if it exists.
     * @param freeBytes The amount of free bytes the server should have.
     * @return an optional containing a server, if it exists.
     */
    Optional<StorageServer> findStorageServerByFreeCapacity(long freeBytes);

}
