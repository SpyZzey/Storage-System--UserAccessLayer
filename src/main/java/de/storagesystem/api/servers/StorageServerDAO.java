package de.storagesystem.api.servers;

import org.springframework.data.repository.CrudRepository;

/**
 * @author Simon Brebeck
 */
public interface StorageServerDAO extends CrudRepository<StorageServer, Long>, StorageServerCustomDAO {
}
