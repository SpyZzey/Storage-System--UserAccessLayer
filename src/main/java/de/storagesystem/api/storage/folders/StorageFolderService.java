package de.storagesystem.api.storage.folders;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;

import java.nio.file.Path;

/**
 * @author Simon Brebeck
 */
public interface StorageFolderService {

    /**
     * Initializes the storage folder service.
     */
    void init();


    /**
     * Creates a directory with a given name in a bucket for a user.
     *
     * @param userId              The id of the user.
     * @param bucketName          The name of the bucket.
     * @param parentDirectoryPath The name of the parent directory.
     * @param bucketDirectoryName The name of the directory.
     * @return true if the directory was created successfully, false otherwise.
     */
    ResponseEntity<ObjectNode> createFolder(Long userId, String bucketName, String parentDirectoryPath, String bucketDirectoryName);

    /**
     * Deletes a directory from a user bucket
     * @param userId        The id of the user.
     * @param bucketName    The name of the bucket.
     * @param directoryPath The path of the directory.
     * @return true if the bucket was deleted successfully, false otherwise.
     */
    ResponseEntity<ObjectNode> deleteFolder(Long userId, String bucketName, String directoryPath);


    /**
     * Loads all folders from a user.
     * @param userId    The id of the user.
     * @return Stream of paths to the files
     */
    ResponseEntity<ObjectNode> loadAllFolders(Long userId);

    /**
     * Loads a folder from a user.
     * @param userId    The id of the user.
     * @param filePath  The Path of the file to load
     * @return the path to the file
     */
    ResponseEntity<ObjectNode> loadFolderByPath(Long userId, String bucket, Path filePath);

}
