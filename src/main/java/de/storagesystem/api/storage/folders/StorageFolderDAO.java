package de.storagesystem.api.storage.folders;

import de.storagesystem.api.storage.StorageItemDAO;
import org.springframework.stereotype.Repository;

public interface StorageFolderDAO extends StorageItemDAO<StorageFolder>, StorageFolderCustomDAO {

}