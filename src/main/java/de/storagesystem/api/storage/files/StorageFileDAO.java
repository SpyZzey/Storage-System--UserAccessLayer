package de.storagesystem.api.storage.files;

import de.storagesystem.api.storage.StorageItemDAO;
import org.springframework.stereotype.Repository;

/**
 * @author Simon Brebeck
 */
public interface StorageFileDAO extends StorageItemDAO<StorageFile>, StorageFileCustomDAO {
}