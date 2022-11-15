package de.storagesystem.api.storage.servers;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class StorageServerCustomDAOImpl implements StorageServerCustomDAO {

    @PersistenceContext
    EntityManager em;

    @Override
    public Optional<StorageServer> findStorageServerByIp(String host, int port) {
        return em.createQuery("SELECT s FROM StorageServer s WHERE s.host = :host AND s.port = :port", StorageServer.class)
                .setParameter("host", host)
                .setParameter("port", port)
                .getResultStream()
                .findFirst();
    }

}
