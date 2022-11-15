package de.storagesystem.api.storage;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface StorageItemDAO<T extends StorageItem> extends CrudRepository<T, Long> {

}
