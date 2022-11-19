package de.storagesystem.api.storage;

import de.storagesystem.api.exceptions.InvalidTokenException;
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
     * @param auth           The authentication token of the user.
     * @param bucketName     The name of the bucket the file should be stored in.
     * @param directoryPath  The path to store the file in.
     * @param file           The file to store
     * @return true if the file was stored successfully, false otherwise
     */
    boolean storeFile(String auth, String bucketName, String directoryPath, MultipartFile file) throws InvalidTokenException;

    /**
     * Deletes a file from a user bucket
     *
     * @param auth          The authentication token of the user.
     * @param bucketName    The name of the bucket.
     * @param filePath      The path of the file.
     * @return true if the bucket was deleted successfully, false otherwise.
     */
    boolean deleteFile(String auth, String bucketName, String filePath);

    /**
     * Loads all files from a user.
     *
     * @param auth      The authentication token of the user.
     * @return          Stream of paths to the files
     */
    Stream<Path> loadAll(String auth);

    /**
     * Loads all files from a user under a given path.
     *
     * @param auth            The authentication token of the user.
     * @param searchPath      The path to search for.
     * @return Stream of paths to the files
     */
    Stream<Path> loadAllByPath(String auth, String searchPath);

    /**
     * Loads a file from a user.
     *
     * @param auth      The authentication token of the user.
     * @param folder    The folder the file is in.
     * @param fileName  The name of the file.
     * @return the path to the file
     */
    Path loadbyPath(String auth, String bucket, String folder, String fileName);


    /**
     * Loads a file from a user as a resource.
     *
     * @param auth      The authentication token of the user..
     *      * @param folder    The folder the file is in.
     *      * @param fileName  The name of the file.
     *      *
     * @return the resource
     */
    ByteArrayResource loadAsResourcebyPath(String auth, String bucket, String folder, String fileName) throws InvalidTokenException;
}
