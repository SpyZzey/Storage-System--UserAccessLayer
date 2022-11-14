package de.storagesystem.api.buckets.directories;

import de.storagesystem.api.buckets.Bucket;
import de.storagesystem.api.buckets.files.BucketFile;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public interface CustomBucketDirectoryRepository {
    Optional<BucketDirectory> findByPath(Bucket bucket, Path path);

    boolean exists(Bucket bucket, String parentPath, String directory);
    BucketDirectory getDirectoryByPath(Bucket bucket, Path dirPath);
    /**
     * Get the parent directory of a Path
     * @param bucket    The bucket to search in
     * @param path      The path to search for
     * @return The directory if path is a directory, else null
     */
    BucketDirectory getDirectoryIfNotBucketElseNull(Bucket bucket, Path path);
}
