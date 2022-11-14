package de.storagesystem.api.storage;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

public class CustomStorageSlaveServerRepositoryImpl implements CustomStorageSlaveServerRepository {

    @PersistenceContext
    EntityManager em;

    @Override
    public Optional<StorageSlaveServer> findStorageServerByIp(String host, int port) {
        return em.createQuery("SELECT s FROM StorageSlaveServer s WHERE s.host = :host AND s.port = :port", StorageSlaveServer.class)
                .setParameter("host", host)
                .setParameter("port", port)
                .getResultStream()
                .findFirst();
    }

}
