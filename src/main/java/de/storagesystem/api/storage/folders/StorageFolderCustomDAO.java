package de.storagesystem.api.storage.folders;

import de.storagesystem.api.storage.buckets.Bucket;

import java.nio.file.Path;
import java.util.Optional;

/**
 * @author Simon Brebeck
 */
public interface StorageFolderCustomDAO {

    /**
     * Returns an optional containing the {@link StorageFolder} with the name from a bucket and a path, if it exists.
     * @param bucket The bucket where the folder is located.
     * @param path The relative {@link Path} to the folder from the bucket.
     * @return an optional containing the {@link StorageFolder}, if it exists.
     * @see Bucket
     * @see StorageFolder
     */
    Optional<StorageFolder> findByPath(Bucket bucket, Path path);

    /**
     * Checks if a {@link StorageFolder} with the name folder exists in a bucket and a path.
     * @param bucket The bucket where the folder is located.
     * @param parentPath The relative path to the folder from the bucket.
     * @param folder The name of the folder.
     * @return true if the folder exists, false otherwise.
     * @see Bucket
     */
    boolean exists(Bucket bucket, String parentPath, String folder);

    /**
     * Returns the {@link StorageFolder} with the name from a bucket and a path, if it exists.
     *
     * @param bucket The bucket where the folder is located.
     * @param folderPath The relative path to the folder from the bucket.
     * @return the {@link StorageFolder}, if it exists.
     *
     * @see Bucket
     * @see StorageFolder
     */
    StorageFolder getFolderByPath(Bucket bucket, Path folderPath);

    /**
     * Get the {@link StorageFolder} with the name from a bucket and a path, if it exists.
     *
     * @param bucket    The bucket to search in
     * @param path      The path to search for
     * @return The {@link StorageFolder} if it exists, else null
     *
     * @see Bucket
     */
    StorageFolder getFolderIfNotBucketElseNull(Bucket bucket, Path path);
}
