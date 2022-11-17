package de.storagesystem.api.storage.buckets;

import de.storagesystem.api.users.User;

import java.util.Optional;

/**
 * @author Simon Brebeck
 */
public interface BucketCustomDAO {
    /**
     * Returns an optional containing the bucket with the given name and owner, if it exists.
     *
     * @param user The owner of the bucket.
     * @param name The name of the bucket.
     * @return An optional containing the bucket, if it exists.
     */
    Optional<Bucket> findByName(User user, String name);


    /**
     * Returns the bucket with the given name and owner, if it exists.
     *
     * @param user The owner of the bucket.
     * @param bucketName The name of the bucket.
     * @return The bucket, if it exists, else null.
     */
    Bucket getBucketByName(User user, String bucketName);
}
