package de.storagesystem.api.storage.files;

import com.fasterxml.jackson.databind.node.ObjectNode;
import de.storagesystem.api.exceptions.InvalidTokenException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

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
     * @param userId The user id of the user the file belongs to.
     * @param bucketName The name of the bucket the file should be stored in.
     * @param directoryPath The path to store the file in.
     * @param file The file to store
     * @return true if the file was stored successfully, false otherwise
     */
    ResponseEntity<ObjectNode> storeFile(long userId, String bucketName, String directoryPath, MultipartFile file) throws InvalidTokenException;

    /**
     * Deletes a file from a user bucket
     *
     * @param userId The user id of the user the file belongs to.
     * @param bucketName The name of the bucket.
     * @param filePath   The path of the file.
     * @return true if the bucket was deleted successfully, false otherwise.
     */
    ResponseEntity<ObjectNode> deleteFile(long userId, String bucketName, String filePath) throws InvalidTokenException;

    /**
     * Loads all files from a user.
     *
     * @param userId The user id of the user the file belongs to.
     * @param page The page to load.
     * @return Stream of paths to the files
     */
    ResponseEntity<ObjectNode> loadAllByPage(long userId, long page);

    /**
     * Loads all files from a user under a given path.
     *
     * @param userId The user id of the user the files belong to.
     * @param searchPath The path to search for.
     * @return Stream of paths to the files
     */
    ResponseEntity<ObjectNode> loadAllByPath(long userId, String searchPath);

    /**
     * Loads a file from a user as a resource.
     *
     * @param userId The user id of the user the file belongs to.
     * @param bucket The name of the bucket the file is stored in.
     * @param filePath The path to the file.
     *
     * @return the resource
     */
    ResponseEntity<Resource> loadFile(long userId, String bucket, String filePath) throws InvalidTokenException;
}
