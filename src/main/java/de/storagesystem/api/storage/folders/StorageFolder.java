package de.storagesystem.api.storage.folders;

import de.storagesystem.api.storage.buckets.Bucket;
import de.storagesystem.api.storage.StorageItem;
import de.storagesystem.api.users.BucketUser;

import javax.persistence.Entity;

@Entity
public class StorageFolder extends StorageItem {
    protected StorageFolder() {

    }

    public StorageFolder(Bucket bucket, StorageFolder bucketDirectory, String originalName, BucketUser creator) {
        super(bucket, bucketDirectory, originalName, creator);
    }
    public StorageFolder(Bucket bucket, String originalName, BucketUser creator) {
        super(bucket, originalName, creator);
    }

}
