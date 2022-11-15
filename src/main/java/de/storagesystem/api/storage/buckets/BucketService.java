package de.storagesystem.api.storage.buckets;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface BucketService {

    /**
     * Initializes the storage.
     */
    void init();

    /**
     * Creates a bucket with a given name for a user.
     *
     * @param userId     The id of the user.
     * @param bucketName The name of the bucket.
     * @return true if the bucket was created successfully, false otherwise.
     */
    boolean createBucket(Long userId, String bucketName);

    /**
     * Deletes a bucket from a user.
     *
     * @param userId        The id of the user.
     * @param bucketName    The name of the bucket.
     * @return true if the bucket was deleted successfully, false otherwise.
     */
    boolean deleteBucket(Long userId, String bucketName);

    /**
     * Loads all buckets from a user.
     *
     * @param userId    The id of the user.
     * @return Stream of paths to the files
     */
    Stream<Bucket> loadAll(Long userId);

    /**
     * Loads a bucket from a user.
     *
     * @param userId    The id of the user.
     * @param bucket  The name of the bucket
     * @return the path to the file
     */
    Bucket loadByName(Long userId, String bucket);



}
