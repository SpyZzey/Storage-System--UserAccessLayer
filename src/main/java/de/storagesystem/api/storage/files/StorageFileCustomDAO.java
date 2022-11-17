package de.storagesystem.api.storage.files;

import de.storagesystem.api.storage.buckets.Bucket;

import java.nio.file.Path;
import java.util.Optional;

/**
 * @author Simon Brebeck
 */
public interface StorageFileCustomDAO {

    /**
     * Loads an optional containing the file from a bucket and a path, if it exists.
     * @param bucket The bucket where the file is located.
     * @param path The path to the file.
     * @return an optional containing the file, if it exists.
     */
    Optional<StorageFile> findByPath(Bucket bucket, Path path);

    /**
     * Checks if a file with the name filename exists in a bucket and a path.
     * @param bucket The bucket where the file is located.
     * @param parentPath The path to the file.
     * @param filename The name of the file.
     * @return true if the file exists, false otherwise.
     */
    boolean exists(Bucket bucket, String parentPath, String filename);

    /**
     * Returns the file with the name from a bucket and a path, if it exists.
     * @param bucket The bucket where the file is located.
     * @param filePath The path to the file.
     * @return the file, if it exists, else null.
     */
    StorageFile getBucketFileByPath(Bucket bucket, Path filePath);
}
