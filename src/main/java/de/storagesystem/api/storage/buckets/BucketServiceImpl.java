
package de.storagesystem.api.storage.buckets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.storagesystem.api.exceptions.StorageEntityNotFoundException;
import de.storagesystem.api.storage.StorageItem;
import de.storagesystem.api.storage.StorageService;
import de.storagesystem.api.storage.files.StorageFile;
import de.storagesystem.api.storage.folders.StorageFolder;
import de.storagesystem.api.storage.folders.StorageFolderDAO;
import de.storagesystem.api.storage.files.StorageFileDAO;
import de.storagesystem.api.exceptions.UserNotFoundException;
import de.storagesystem.api.servers.StorageServerDAO;
import de.storagesystem.api.users.User;
import de.storagesystem.api.users.UserDAO;
import de.storagesystem.api.util.ResponseBuilder;
import de.storagesystem.api.util.ResponseState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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

    ObjectMapper mapper;

    /**
     * Instantiates a new Bucket service.
     *
     * @param storageServerRepository the storage server repository
     * @param bucketFolderRepository the bucket folder repository
     * @param storageFileRepository the storage file repository
     * @param bucketRepository the bucket repository
     * @param userRepository the user repository
     */
    @Autowired
    public BucketServiceImpl(
            StorageServerDAO storageServerRepository,
            StorageFolderDAO bucketFolderRepository,
            StorageFileDAO storageFileRepository,
            BucketDAO bucketRepository,
            UserDAO userRepository,
            ObjectMapper mapper) {
        super(storageServerRepository, bucketFolderRepository, storageFileRepository, bucketRepository, userRepository);
        this.mapper = mapper;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    @PostConstruct
    public void init() {
        super.init();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<ObjectNode> createBucket(Long userId, String bucketName) throws UserNotFoundException {
        // Fetch the user from the database, throw an exception if the user does not exist
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        // Check if the bucket already exists
        boolean existsInDatabase = bucketRepository.findByName(user, bucketName).isPresent();
        if(!existsInDatabase) {
            // Create the bucket in the database if it does not exist
            bucketRepository.save(new Bucket(bucketName, user));
            logger.info("Created bucket" + bucketName);

            ObjectNode response = new ResponseBuilder().build(ResponseState.OK, "Bucket successfully created.");
            return ResponseEntity.ok(response);
        } else {
            logger.info("Bucket already exists: " + bucketName);

            ObjectNode response = new ResponseBuilder().build(ResponseState.ERROR, "Bucket already exists.");
            return ResponseEntity.badRequest().body(response);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<ObjectNode> deleteBucket(Long userId, String bucketName) {
        // Fetch the user from the database, throw an exception if the user does not exist
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        Optional<Bucket> bucket = bucketRepository.findByName(user, bucketName);
        if(bucket.isPresent()) {
            // Delete the bucket from the database
            bucketRepository.delete(bucket.get());
            logger.info("Deleted bucket" + bucketName);

            ObjectNode response = new ResponseBuilder().build(ResponseState.OK, "Bucket successfully deleted.");
            return ResponseEntity.ok(response);
        } else {
            logger.info("Bucket does not exist: " + bucketName);

            ObjectNode response = new ResponseBuilder().build(ResponseState.ERROR, "Bucket does not exist.");
            return ResponseEntity.badRequest().body(response);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<ObjectNode> loadPage(Long userId, int page, int limit) {
        // Fetch the user from the database, throw an exception if the user does not exist
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        // Fetch all buckets from the database
        Stream<Bucket> buckets = bucketRepository.findPageByUser(user, page, limit);

        // Fetch the total amount of buckets
        long totalBucketsByUser = bucketRepository.countBucketsByUser(user);
        long totalPages = calculateTotalPages(totalBucketsByUser, limit);
        int bucketCount = calculateItemCount(totalPages, totalBucketsByUser, page, limit);

        // Create a new response object
        ObjectNode response = new ResponseBuilder()
                .setStatus(ResponseState.OK)
                .setMessage("Buckets successfully loaded.")
                .addArray("data", buckets)
                .add("count", bucketCount)
                .add("total", totalBucketsByUser)
                .add("page", page)
                .add("pages", totalPages)
                .build();

        logger.info("Loaded page " + page + " with " + bucketCount + " buckets for user " + user.getFirstname() + " " + user.getLastname());
        return ResponseEntity.ok(response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<ObjectNode> loadBucketInfo(Long userId, String bucketName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Bucket bucket = bucketRepository.findByName(user, bucketName)
                .orElseThrow(() -> new StorageEntityNotFoundException("Bucket not found"));

        ObjectNode response = new ResponseBuilder()
                .setStatus(ResponseState.OK)
                .setMessage("Bucket info successfully loaded.")
                .add("data", bucket)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<ObjectNode> loadBucketFolders(Long userId, String bucketName, int page, int limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Bucket bucket = bucketRepository.findByName(user, bucketName)
                .orElseThrow(() -> new StorageEntityNotFoundException("Bucket not found"));

        logger.info("Load folder page " + page + " with limit " + limit
                + " of bucket " + bucketName + " for user " + user.getFirstname() + " " + user.getLastname());

        // Fetch all folders of a bucket
        List<StorageFolder> folders = bucket.getRootFolder().getFolders();
        ObjectNode response = createBucketContentResponse(folders, page, limit);
        return ResponseEntity.ok(response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<ObjectNode> loadBucketFiles(Long userId, String bucketName, int page, int limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Bucket bucket = bucketRepository.findByName(user, bucketName)
                .orElseThrow(() -> new StorageEntityNotFoundException("Bucket not found"));

        logger.info("Load file page " + page + " with limit " + limit
                + " of bucket " + bucketName + " for user " + user.getFirstname() + " " + user.getLastname());

        // Fetch all files of a bucket
        List<StorageFile> files = bucket.getRootFolder().getFiles();
        ObjectNode response = createBucketContentResponse(files, page, limit);
        return ResponseEntity.ok(response);
    }

    private <T extends StorageItem> ObjectNode createBucketContentResponse(List<T> content, int page, int limit) {
        Stream<T> fileStream = content.stream()
                .sorted(Comparator.comparing(T::getOriginalName))
                .skip((long) page * limit)
                .limit(limit);

        long totalItems = content.size();
        long totalPages = calculateTotalPages(totalItems, limit);
        int itemCount = calculateItemCount(totalPages, totalItems, page, limit);

        return new ResponseBuilder()
                .setStatus(ResponseState.OK)
                .setMessage("Bucket folders successfully loaded.")
                .addArray("data", fileStream)
                .add("count", itemCount)
                .add("total", totalItems)
                .add("page", page)
                .add("pages", totalPages)
                .build();
    }

    private long calculateTotalPages(long totalItems, int limit) {
        return (long) Math.ceil((double) totalItems / limit);
    }

    private int calculateItemCount(long totalPages, long totalItems, int page, int limit) {
        if(totalItems == 0) return 0;
        if(page == totalPages - 1) return (int) (totalItems % limit);
        return limit;
    }

}
