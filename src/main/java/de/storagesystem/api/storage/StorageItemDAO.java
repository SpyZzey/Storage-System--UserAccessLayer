package de.storagesystem.api.storage;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author Simon Brebeck
 */
@NoRepositoryBean
public interface StorageItemDAO<T extends StorageItem> extends CrudRepository<T, Long> {

}
