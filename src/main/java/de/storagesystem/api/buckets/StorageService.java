package de.storagesystem.api.buckets;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    /**
     * Initializes the storage.
     */
    void init();

    /**
     * Stores a given file in the storage under a given bucket and directory.
     *
     * @param userId     The id of the user.
     * @param bucketName The name of the bucket the file should be stored in.
     * @param directoryPath       The path to store the file in.
     * @param file       the file to store
     * @return true if the file was stored successfully, false otherwise
     */
    boolean storeFile(Long userId, String bucketName, String directoryPath, MultipartFile file);

    /**
     * Creates a bucket with a given name for a user.
     *
     * @param userId     The id of the user.
     * @param bucketName The name of the bucket.
     * @return true if the bucket was created successfully, false otherwise.
     */
    boolean createBucket(Long userId, String bucketName);

    /**
     * Creates a directory with a given name in a bucket for a user.
     *
     * @param userId              The id of the user.
     * @param bucketName          The name of the bucket.
     * @param parentDirectoryPath The name of the parent directory.
     * @param bucketDirectoryName The name of the directory.
     * @return true if the directory was created successfully, false otherwise.
     */
    boolean createDirectory(Long userId, String bucketName, String parentDirectoryPath, String bucketDirectoryName);

    /**
     * Deletes a bucket from a user.
     * @param userId        The id of the user.
     * @param bucketName    The name of the bucket.
     * @return true if the bucket was deleted successfully, false otherwise.
     */
    boolean deleteBucket(Long userId, String bucketName);

    /**
     * Deletes a directory from a user bucket
     * @param userId        The id of the user.
     * @param bucketName    The name of the bucket.
     * @param directoryPath The path of the directory.
     * @return true if the bucket was deleted successfully, false otherwise.
     */
    boolean deleteDirectory(Long userId, String bucketName, String directoryPath);

    /**
     * Deletes a file from a user bucket
     * @param userId        The id of the user.
     * @param bucketName    The name of the bucket.
     * @param filePath      The path of the file.
     * @return true if the bucket was deleted successfully, false otherwise.
     */
    boolean deleteFile(Long userId, String bucketName, String filePath);

    /**
     * Loads all files from a user.
     * @param userId    The id of the user.
     * @return Stream of paths to the files
     */
    Stream<Path> loadAll(Long userId);

    /**
     * Loads a file from a user.
     * @param userId    The id of the user.
     * @param filePath  The Path of the file to load
     * @return the path to the file
     */
    Path loadbyPath(Long userId, String bucket, Path filePath);


    /**
     * Loads a file from a user as a resource.
     * @param userId    The id of the user.
     * @param filePath  The Path of the file to load
     * @return the resource
     */
    ByteArrayResource loadAsResourcebyPath(Long userId, String bucket, Path filePath);

}
