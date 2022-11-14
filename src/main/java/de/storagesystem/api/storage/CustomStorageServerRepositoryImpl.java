package de.storagesystem.api.storage;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

public class CustomStorageServerRepositoryImpl implements CustomStorageServerRepository {

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
