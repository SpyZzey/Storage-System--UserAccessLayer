package de.storagesystem.api.storage;

/**
 * @author Simon Brebeck on 17.11.2022
 */
public class StorageInputValidationImpl implements StorageInputValidation {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validateBucketName(String bucketName) {
        if(bucketName == null) return false;
        return bucketName.length() > 0 && bucketName.length() < 255;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validateFolderPath(String folderPath) {
        if(folderPath == null) return true;
        if(folderPath.isEmpty()) return true;
        if(folderPath.startsWith("/")) return false;
        if(folderPath.endsWith("/")) return false;

        return folderPath.length() < 1024;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validateFolderName(String folderName) {
        if(folderName == null) return false;
        if(folderName.isEmpty()) return false;
        if(folderName.contains("/")) return false;

        return folderName.length() < 1024;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validateFileName(String filename) {
        if(filename == null) return false;
        if(filename.isEmpty()) return false;
        if(filename.contains("/")) return false;

        return filename.length() < 1024;
    }
}
