package de.storagesystem.api.storage.files;

import de.storagesystem.api.storage.buckets.Bucket;

import java.nio.file.Path;
import java.util.Optional;

public interface StorageFileCustomDAO {
    Optional<StorageFile> findByPath(Bucket bucket, Path path);

    boolean exists(Bucket bucket, String parentPath, String filename);

    StorageFile getBucketFileByPath(Bucket bucket, Path filePath);
}
