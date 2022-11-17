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

@Service
public class StorageFolderServiceImpl extends StorageService implements StorageFolderService {
    private static final Logger logger = LogManager.getLogger(BucketServiceImpl.class);

    public StorageFolderServiceImpl(
            StorageServerDAO storageServerRepository,
            StorageFolderDAO bucketFolderRepository,
            StorageFileDAO storageFileRepository,
            BucketDAO bucketRepository,
            UserDAO userRepository) {
        super(storageServerRepository, bucketFolderRepository, storageFileRepository, bucketRepository, userRepository);
    }

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

    @Override
    public Stream<Path> loadAllFolders(Long userId) {
        return null;
    }

    @Override
    public Path loadFolderByPath(Long userId, String bucket, Path filePath) {
        return null;
    }
}
