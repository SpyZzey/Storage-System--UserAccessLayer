package de.storagesystem.api.servers;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

/**
 * @author Simon Brebeck
 */
@Repository
public class StorageServerCustomDAOImpl implements StorageServerCustomDAO {

    /**
     * The {@link EntityManager} used to access the database.
     */
    @PersistenceContext
    EntityManager em;

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<StorageServer> findStorageServerByIp(String host, int port) {
        return em.createQuery("SELECT s FROM StorageServer s WHERE s.host = :host AND s.port = :port", StorageServer.class)
                .setParameter("host", host)
                .setParameter("port", port)
                .getResultStream()
                .findFirst();
    }

    /**
     * {@inheritDoc}
     *
     * @param freeBytes The amount of free bytes the server should have.
     * @return an optional containing the {@link StorageServer} with the least
     * amount of storage available that fulfills the requirements, if it exists.
     */
    @Override
    public Optional<StorageServer> findStorageServerByFreeCapacity(long freeBytes) {
        return em.createQuery("SELECT s FROM StorageServer s WHERE s.freeStorage >= :freeBytes AND s.online = true ORDER BY s.freeStorage DESC", StorageServer.class)
                .setParameter("freeBytes", freeBytes)
                .getResultStream()
                .findFirst();
    }


}
