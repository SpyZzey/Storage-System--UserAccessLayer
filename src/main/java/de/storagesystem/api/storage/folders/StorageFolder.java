package de.storagesystem.api.storage.folders;

import de.storagesystem.api.storage.buckets.Bucket;
import de.storagesystem.api.storage.StorageItem;
import de.storagesystem.api.users.BucketUser;

import javax.persistence.Entity;

/**
 * @author Simon Brebeck
 */
@Entity
public class StorageFolder extends StorageItem {

    /**
     * Instantiates a new StorageFolder.
     */
    protected StorageFolder() {

    }

    /**
     * Instantiates a new StorageFolder.
     * @param bucket The bucket where the folder is located.
     * @param bucketDirectory The directory where the folder is located.
     * @param originalName The original name of the folder.
     * @param creator The {@link BucketUser} who created the folder.
     */
    public StorageFolder(Bucket bucket, StorageFolder bucketDirectory, String originalName, BucketUser creator) {
        super(bucket, bucketDirectory, originalName, creator);
    }

    /**
     * Instantiates a new StorageFolder.
     * @param bucket The bucket where the folder is located.
     * @param originalName The original name of the folder.
     * @param creator The {@link BucketUser} who created the folder.
     */
    public StorageFolder(Bucket bucket, String originalName, BucketUser creator) {
        super(bucket, originalName, creator);
    }

}
