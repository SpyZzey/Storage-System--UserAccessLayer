package de.storagesystem.api.storage.servers;

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

}
