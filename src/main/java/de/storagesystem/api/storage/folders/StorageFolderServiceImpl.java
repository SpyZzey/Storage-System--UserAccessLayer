package de.storagesystem.api.storage.folders;

import com.fasterxml.jackson.databind.node.ObjectNode;
import de.storagesystem.api.exceptions.StorageEntityAlreadyExistsException;
import de.storagesystem.api.exceptions.StorageEntityCreationException;
import de.storagesystem.api.exceptions.StorageEntityNotFoundException;
import de.storagesystem.api.exceptions.UserNotFoundException;
import de.storagesystem.api.storage.StorageItem;
import de.storagesystem.api.storage.buckets.Bucket;
import de.storagesystem.api.storage.buckets.BucketDAO;
import de.storagesystem.api.storage.buckets.BucketServiceImpl;
import de.storagesystem.api.storage.StorageService;
import de.storagesystem.api.storage.files.StorageFile;
import de.storagesystem.api.storage.files.StorageFileDAO;
import de.storagesystem.api.servers.StorageServerDAO;
import de.storagesystem.api.users.User;
import de.storagesystem.api.users.UserDAO;
import de.storagesystem.api.util.ResponseBuilder;
import de.storagesystem.api.util.ResponseState;
import de.storagesystem.api.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
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
    public ResponseEntity<ObjectNode> createFolder(Long userId, String bucketName, String parentFolderPath, String folderName)
            throws StorageEntityNotFoundException, StorageEntityCreationException, UserNotFoundException {
        User user = userRepository
                .findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Bucket bucketObj = bucketRepository
                .getBucketByName(user, bucketName);
        StorageFolder parentObj = bucketFolderRepository
                .getFolder(bucketObj, parentFolderPath);

        // Check if the folder already exists
        boolean existsInDatabase = bucketFolderRepository.exists(bucketObj, parentObj.getPath(), folderName);
        if(!existsInDatabase) {
            // Create the folder in the database if it does not exist
            StorageFolder folder = new StorageFolder(folderName, user);
            parentObj.addFolder(folder);

            bucketFolderRepository.save(folder);
            bucketFolderRepository.save(parentObj);
            logger.info("Created folder: " + parentObj.getPath() + "/" + folderName);

            ObjectNode response = new ResponseBuilder()
                    .setStatus(ResponseState.OK)
                    .setMessage("Folder successfully created.")
                    .add("name", folder.getOriginalName())
                    .add("path", folder.getPath())
                    .build();
            return ResponseEntity.ok(response);
        } else  {
            throw new StorageEntityAlreadyExistsException("Folder already exists");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<ObjectNode> deleteFolder(Long userId, String bucketName, String folderPath) {
        return null;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<ObjectNode> loadFolders(Long userId, String bucket, String pathToParent, int page, int limit)
            throws StorageEntityNotFoundException, StorageEntityCreationException, UserNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Bucket bucketObj = bucketRepository.findByName(user, bucket)
                .orElseThrow(() -> new StorageEntityNotFoundException("Bucket not found"));
        StorageFolder parentObj = bucketFolderRepository.getFolder(bucketObj, pathToParent);

        logger.info("Load folder page " + page + " with limit " + limit
                + " of bucket " + bucket + " for user " + user.getFirstname() + " " + user.getLastname());

        // Fetch all folders of a bucket
        List<StorageFolder> folders = parentObj.getFolders();
        ObjectNode response = Util.createStorageItemList(folders, page, limit);
        return ResponseEntity.ok(response);
    }

}
