package de.storagesystem.api.buckets.files;

import de.storagesystem.api.buckets.Bucket;
import de.storagesystem.api.buckets.BucketItem;
import de.storagesystem.api.buckets.directories.BucketDirectory;
import de.storagesystem.api.exceptions.StorageEntityNotFoundException;
import de.storagesystem.api.users.User;

import java.nio.file.Path;
import java.util.Optional;

public interface CustomBucketFileRepository {
    Optional<BucketFile> findByPath(Bucket bucket, Path path);

    boolean exists(Bucket bucket, String parentPath, String filename);

    BucketFile getBucketFileByPath(Bucket bucket, Path filePath);
}
