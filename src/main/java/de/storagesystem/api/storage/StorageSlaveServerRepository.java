package de.storagesystem.api.storage;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageSlaveServerRepository extends CrudRepository<StorageSlaveServer, Long>, CustomStorageSlaveServerRepository {
}
