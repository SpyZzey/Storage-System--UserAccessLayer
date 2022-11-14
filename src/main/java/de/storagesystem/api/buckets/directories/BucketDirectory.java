package de.storagesystem.api.buckets.directories;

import de.storagesystem.api.buckets.Bucket;
import de.storagesystem.api.buckets.BucketItem;
import de.storagesystem.api.buckets.users.BucketUser;

import javax.persistence.Entity;
import java.nio.file.Path;

@Entity
public class BucketDirectory extends BucketItem {
    protected BucketDirectory() {

    }

    public BucketDirectory(Bucket bucket, BucketDirectory bucketDirectory, String originalName, BucketUser creator) {
        super(bucket, bucketDirectory, originalName, creator);
    }
    public BucketDirectory(Bucket bucket, String originalName, BucketUser creator) {
        super(bucket, originalName, creator);
    }

}
