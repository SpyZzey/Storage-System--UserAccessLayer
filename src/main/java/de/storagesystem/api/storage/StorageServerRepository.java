package de.storagesystem.api.storage;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageServerRepository extends CrudRepository<StorageServer, Long>, CustomStorageServerRepository {
}
