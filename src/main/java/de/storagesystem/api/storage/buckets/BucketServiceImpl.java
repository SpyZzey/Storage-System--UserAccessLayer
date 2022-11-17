
package de.storagesystem.api.storage.buckets;

import de.storagesystem.api.storage.StorageService;
import de.storagesystem.api.storage.folders.StorageFolderDAO;
import de.storagesystem.api.storage.files.StorageFileDAO;
import de.storagesystem.api.exceptions.UserNotFoundException;
import de.storagesystem.api.storage.servers.StorageServerDAO;
import de.storagesystem.api.users.User;
import de.storagesystem.api.users.UserDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import java.util.stream.Stream;

/**
 * @author Simon Brebeck
 */
@Service
public class BucketServiceImpl extends StorageService implements BucketService {

    /**
     * The {@link Logger} for this class
     */
    private static final Logger logger = LogManager.getLogger(BucketServiceImpl.class);

    /**
     * Instantiates a new Bucket service.
     *
     * @param storageServerRepository the storage server repository
     * @param bucketFolderRepository the bucket folder repository
     * @param storageFileRepository the storage file repository
     * @param bucketRepository the bucket repository
     * @param userRepository the user repository
     */
    public BucketServiceImpl(
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
    public boolean createBucket(Long userId, String bucketName) throws UserNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        // Check if the bucket already exists
        boolean existsInDatabase = bucketRepository.findByName(user, bucketName).isPresent();
        if(!existsInDatabase) {
            // Create the bucket in the database if it does not exist
            bucketRepository.save(new Bucket(bucketName, user));
            logger.info("Created bucket" + bucketName);
            return true;
        } else {
            logger.info("Bucket already exists: " + bucketName);
            return false;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteBucket(Long userId, String bucketName) {
        return false;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<Bucket> loadAll(Long userId) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bucket loadByName(Long userId, String bucket) {
        return null;
    }

}
