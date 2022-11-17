package de.storagesystem.api.storage;

public class StorageInputValidation {

    public static boolean validateBucketName(String bucketName) {
        if(bucketName == null) return false;
        return bucketName.length() > 0 && bucketName.length() < 255;
    }

    public static boolean validateFolderPath(String parentFolderPath) {
        if(parentFolderPath == null) return true;
        if(parentFolderPath.isEmpty()) return true;
        if(parentFolderPath.startsWith("/")) return false;
        if(parentFolderPath.endsWith("/")) return false;

        return parentFolderPath.length() < 1024;
    }

    public static boolean validateFolderName(String folderName) {
        if(folderName == null) return false;
        if(folderName.isEmpty()) return false;
        if(folderName.contains("/")) return false;

        return folderName.length() < 1024;
    }

    public static boolean validateFileName(String filename) {
        if(filename == null) return false;
        if(filename.isEmpty()) return false;
        if(filename.contains("/")) return false;

        return filename.length() < 1024;
    }
}
