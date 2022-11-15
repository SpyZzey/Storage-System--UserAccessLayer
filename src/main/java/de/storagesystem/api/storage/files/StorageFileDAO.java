package de.storagesystem.api.storage.files;

import de.storagesystem.api.storage.StorageItemDAO;
import org.springframework.stereotype.Repository;

public interface StorageFileDAO extends StorageItemDAO<StorageFile>, StorageFileCustomDAO {
}