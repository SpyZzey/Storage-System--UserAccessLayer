package de.storagesystem.api.storage.files;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * @author Simon Brebeck
 */
public interface StorageFileService {

    /**
     * Initializes the storage file service.
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
     * Deletes a file from a user bucket
     *
     * @param userId        The id of the user.
     * @param bucketName    The name of the bucket.
     * @param filePath      The path of the file.
     * @return true if the bucket was deleted successfully, false otherwise.
     */
    boolean deleteFile(Long userId, String bucketName, String filePath);

    /**
     * Loads all files from a user.
     *
     * @param userId    The id of the user.
     * @return Stream of paths to the files
     */
    Stream<Path> loadAll(Long userId);

    /**
     * Loads all files from a user under a given path.
     *
     * @param userId    The id of the user.
     * @param searchPath      The path to search for.
     * @return Stream of paths to the files
     */
    Stream<Path> loadAllByPath(Long userId, String searchPath);

    /**
     * Loads a file from a user.
     *
     * @param userId    The id of the user.
     * @param filePath  The Path of the file to load
     * @return the path to the file
     */
    Path loadbyPath(Long userId, String bucket, Path filePath);


    /**
     * Loads a file from a user as a resource.
     *
     * @param userId    The id of the user.
     * @param filePath  The Path of the file to load
     * @return the resource
     */
    ByteArrayResource loadAsResourcebyPath(Long userId, String bucket, Path filePath);
}
