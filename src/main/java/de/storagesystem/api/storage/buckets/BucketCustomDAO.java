package de.storagesystem.api.storage.buckets;

import de.storagesystem.api.users.User;

import java.util.Optional;
import java.util.stream.Stream;

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
     * Returns a stream of all buckets owned by the given user.
     * @param user The owner of the buckets.
     * @param page The page to return.
     * @param limit The limit of buckets per page.
     * @return A stream of all buckets under a given page and length owned by the given user.
     */
    Stream<Bucket> findPageByUser(User user, int page, int limit);

    /**
     * Returns the number of buckets owned by the given user.
     * @param user The owner of the buckets.
     * @return The number of buckets owned by the given user.
     */
    long countBucketsByUser(User user);

    /**
     * Returns the bucket with the given name and owner, if it exists.
     *
     * @param user The owner of the bucket.
     * @param bucketName The name of the bucket.
     * @return The bucket, if it exists, else null.
     */
    Bucket getBucketByName(User user, String bucketName);


}
