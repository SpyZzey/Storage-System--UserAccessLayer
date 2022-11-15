package de.storagesystem.api.storage.folders;

import de.storagesystem.api.storage.buckets.Bucket;

import java.nio.file.Path;
import java.util.Optional;

public interface StorageFolderCustomDAO {
    Optional<StorageFolder> findByPath(Bucket bucket, Path path);

    boolean exists(Bucket bucket, String parentPath, String directory);
    StorageFolder getDirectoryByPath(Bucket bucket, Path dirPath);
    /**
     * Get the parent directory of a Path
     * @param bucket    The bucket to search in
     * @param path      The path to search for
     * @return The directory if path is a directory, else null
     */
    StorageFolder getFolderIfNotBucketElseNull(Bucket bucket, Path path);
}
