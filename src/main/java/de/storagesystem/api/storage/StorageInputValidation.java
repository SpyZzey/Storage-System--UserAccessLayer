package de.storagesystem.api.storage;

/**
 * @author Simon Brebeck
 */
public interface StorageInputValidation {

    /**
     * Checks if the given string is a valid name for a bucket.
     *
     * @param bucketName The name of the bucket to validate.
     * @return true if the name is valid, false otherwise.
     */
    boolean validateBucketName(String bucketName);

    /**
     * Checks if the given string is a valid path for a folder.
     *
     * @param folderPath The path of the folder to validate.
     * @return true if the folder path is valid, false otherwise.
     */
    boolean validateFolderPath(String folderPath);


    /**
     * Checks if the given string is a valid name for a folder.
     *
     * @param folderName The name of the folder to validate.
     * @return true if the folder name is valid, false otherwise.
     */
    boolean validateFolderName(String folderName);

    /**
     * Checks if the given string is a valid name for a file.
     * @param filename The name of the file to validate.
     * @return true if the file name is valid, false otherwise.
     */
    boolean validateFileName(String filename);

    /**
     * Checks if the given string is a valid path for a file.
     *
     * @param filePath      The path of the file to validate.
     * @return true if the name is valid, false otherwise.
     */
    boolean validateFilePath(String filePath);
}
