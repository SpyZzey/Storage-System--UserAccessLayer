package de.storagesystem.api.storage.folders;

import de.storagesystem.api.exceptions.UserNotFoundException;
import de.storagesystem.api.storage.StorageInputValidation;
import de.storagesystem.api.storage.buckets.Bucket;
import de.storagesystem.api.storage.buckets.BucketDAO;
import de.storagesystem.api.storage.buckets.BucketServiceImpl;
import de.storagesystem.api.storage.StorageService;
import de.storagesystem.api.storage.files.StorageFileDAO;
import de.storagesystem.api.storage.servers.StorageServerDAO;
import de.storagesystem.api.users.User;
import de.storagesystem.api.users.UserDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * @author Simon Brebeck
 */
@Service
public class StorageFolderServiceImpl extends StorageService implements StorageFolderService {

    /**
     * The {@link Logger} for this class
     */
    private static final Logger logger = LogManager.getLogger(BucketServiceImpl.class);


    /**
     * Instantiates a new Storage folder service.
     * @param storageServerRepository the storage server repository
     * @param bucketFolderRepository the bucket folder repository
     * @param storageFileRepository the storage file repository
     * @param bucketRepository the bucket repository
     * @param userRepository the user repository
     */
    public StorageFolderServiceImpl(
            StorageServerDAO storageServerRepository,
            StorageFolderDAO bucketFolderRepository,
            StorageFileDAO storageFileRepository,
            BucketDAO bucketRepository,
            UserDAO userRepository) {
        super(storageServerRepository, bucketFolderRepository, storageFileRepository, bucketRepository, userRepository);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        super.init();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createFolder(Long userId, String bucketName, String parentFolderPath, String folderName)
            throws UserNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Bucket bucketObj = bucketRepository.getBucketByName(user, bucketName);
        Path parentPath = getFolderPathByString(parentFolderPath);
        StorageFolder parentObj = bucketFolderRepository.getFolderIfNotBucketElseNull(bucketObj, parentPath);

        // Check if the folder already exists
        boolean existsInDatabase = bucketFolderRepository.exists(bucketObj, parentPath.toString(), folderName);
        if(!existsInDatabase) {
            // Create the folder in the database if it does not exist
            bucketFolderRepository.save(new StorageFolder(
                    bucketObj,
                    parentObj,
                    folderName,
                    user.bucketUser()));
            logger.info("Created folder: " + parentPath + File.separator + folderName);
            return true;
        } else  {
            logger.info("Folder already exists: " + parentPath + File.separator + folderName);
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteFolder(Long userId, String bucketName, String folderPath) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<Path> loadAllFolders(Long userId) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Path loadFolderByPath(Long userId, String bucket, Path filePath) {
        return null;
    }
}
