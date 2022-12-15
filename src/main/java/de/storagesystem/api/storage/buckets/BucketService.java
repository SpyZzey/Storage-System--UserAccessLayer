package de.storagesystem.api.storage.buckets;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;

/**
 * @author Simon Brebeck
 */
public interface BucketService {

    /**
     * Initializes the storage bucket service.
     */
    void init();

    /**
     * Creates a bucket with a given name for a user.
     *
     * @param userId     The id of the user.
     * @param bucketName The name of the bucket.
     * @return true if the bucket was created successfully, false otherwise.
     */
    ResponseEntity<ObjectNode> createBucket(Long userId, String bucketName);

    /**
     * Deletes a bucket from a user.
     *
     * @param userId        The id of the user.
     * @param bucketName    The name of the bucket.
     * @return true if the bucket was deleted successfully, false otherwise.
     */
    ResponseEntity<ObjectNode> deleteBucket(Long userId, String bucketName);

    /**
     * Loads all buckets from a user.
     *
     * @param userId The id of the user.
     * @param page  The page to load.
     * @param limit The limit of buckets to load per page.
     * @return Stream of paths to the files
     */
    ResponseEntity<ObjectNode> loadPage(Long userId, int page, int limit);

    /**
     * Loads a bucket from a user.
     *
     * @param userId The id of the user.
     * @param bucket The name of the bucket
     * @return the path to the file
     */
    ResponseEntity<ObjectNode> loadBucketInfo(Long userId, String bucket);

    /**
     * Load the folders of a bucket from a user.
     * @param userId The id of the user.
     * @param bucketName The name of the bucket.
     * @param page The page to load.
     * @param limit The limit of folders to load per page.
     */
    ResponseEntity<ObjectNode> loadBucketFolders(Long userId, String bucketName, int page, int limit);

    /**
     * Load the files of a bucket from a user.
     * @param userId The id of the user.
     * @param bucketName The name of the bucket.
     * @param page The page to load.
     * @param limit The limit of folders to load per page.
     */
    ResponseEntity<ObjectNode> loadBucketFiles(Long userId, String bucketName, int page, int limit);

}
