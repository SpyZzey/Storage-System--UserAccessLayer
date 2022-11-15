package de.storagesystem.api.storage.servers;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface StorageServerDAO extends CrudRepository<StorageServer, Long>, StorageServerCustomDAO {
}
