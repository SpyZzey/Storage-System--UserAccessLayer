
package de.storagesystem.api.buckets;

import de.storagesystem.api.buckets.directories.BucketDirectory;
import de.storagesystem.api.buckets.directories.BucketDirectoryRepository;
import de.storagesystem.api.buckets.files.BucketFile;
import de.storagesystem.api.buckets.files.BucketFileRepository;
import de.storagesystem.api.cryptography.FileAESCryptographer;
import de.storagesystem.api.cryptography.FileCryptographer;
import de.storagesystem.api.exceptions.StorageEntityCreationException;
import de.storagesystem.api.exceptions.StorageEntityNotFoundException;
import de.storagesystem.api.exceptions.UserNotFoundException;
import de.storagesystem.api.storage.StorageSlaveServer;
import de.storagesystem.api.storage.StorageSlaveServerRepository;
import de.storagesystem.api.users.User;
import de.storagesystem.api.users.UserRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.*;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Component
public class BucketService extends StorageFileSystem implements StorageService {

    private static final Logger logger = LogManager.getLogger(BucketService.class);

    private String serverPrefix;
    private StorageSlaveServer storageServer;
    private final StorageSlaveServerRepository storageSlaveServerRepository;
    private final BucketDirectoryRepository bucketDirectoryRepository;
    private final BucketFileRepository bucketFileRepository;
    private final BucketRepository bucketRepository;
    private final UserRepository userRepository;

    @Autowired
    public BucketService(
            StorageSlaveServerRepository storageSlaveServerRepository,
            BucketDirectoryRepository bucketDirectoryRepository,
            BucketFileRepository bucketFileRepository,
            BucketRepository bucketRepository,
            UserRepository userRepository) {
        this.storageSlaveServerRepository = storageSlaveServerRepository;
        this.bucketDirectoryRepository = bucketDirectoryRepository;
        this.bucketFileRepository = bucketFileRepository;
        this.bucketRepository = bucketRepository;
        this.userRepository = userRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        super.init();

        // Load server prefix from .env file
        Dotenv dotenv = Dotenv.load();
        serverPrefix = dotenv.get("SERVER_PREFIX");

        // Creates server storage directory if it does not exist
        File file = new File(root());
        if (file.exists()) return;

        if (file.mkdirs()) {
            logger.info("Created upload directory");
        } else {
            logger.error("Could not create upload directory");
            throw new StorageEntityCreationException("Could not create upload directory");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean storeFile(
            Long userId,
            String bucketName,
            String directoryPath,
            MultipartFile file) {
        User user = getUserById(userId);
        Bucket bucket = bucketRepository.getBucketByName(user, bucketName);

        // Get the path to the directory where the file should be stored
        Path parentPath = getDirectoryPathByString(directoryPath);
        BucketDirectory parent = bucketDirectoryRepository.getDirectoryIfNotBucketElseNull(bucket, parentPath);

        // Check if the file already exists in the database
        if(bucketFileRepository.exists(bucket, parentPath.toString(), file.getOriginalFilename())) {
            logger.info("File already exists: " + file.getOriginalFilename());
            return false;
        }

        try {
            // Create a new file entity on disk
            String uuidFilename = serverPrefix + UUID.randomUUID();
            String storedPath = getFileStoragePath(user.id()) + File.separator + uuidFilename;
            storeEncryptedFileOnDisk(user, storedPath, file);

            // Create the new file in the database
            BucketFile fileModel = new BucketFile(
                    getStorageServer(),
                    bucket,
                    parent,
                    storedPath,
                    file.getOriginalFilename(),
                    uuidFilename,
                    file.getContentType(),
                    file.getSize(),
                    user.bucketUser());
            bucketFileRepository.save(fileModel);
            logger.info("File stored: " + file.getOriginalFilename());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createBucket(Long userId, String bucketName) throws UserNotFoundException {
        User user = getUserById(userId);

        // Check if the bucket already exists
        boolean existsInDatabase = bucketRepository.findByName(user, bucketName).isPresent();
        if(!existsInDatabase) {
            // Create the bucket in the database if it does not exist
            bucketRepository.save(new Bucket(bucketName, user));
            logger.info("Created bucket directory" + bucketName);
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
    public boolean createDirectory(Long userId, String bucketName, String parentDirectoryPath, String bucketDirectoryName)
            throws UserNotFoundException {
        User user = getUserById(userId);
        Bucket bucket = bucketRepository.getBucketByName(user, bucketName);
        Path parentPath = getDirectoryPathByString(parentDirectoryPath);
        BucketDirectory parent = bucketDirectoryRepository.getDirectoryIfNotBucketElseNull(bucket, parentPath);

        // Check if the directory already exists
        boolean existsInDatabase = bucketDirectoryRepository.exists( bucket, parentPath.toString(), bucketDirectoryName);
        if(!existsInDatabase) {
            // Create the directory in the database if it does not exist
            bucketDirectoryRepository.save(new BucketDirectory(
                    bucket,
                    parent,
                    bucketDirectoryName,
                    user.bucketUser()));
            logger.info("Created directory: " + parentPath + File.separator + bucketDirectoryName);
            return true;
        } else  {
            logger.info("Directory already exists: " + parentPath + File.separator + bucketDirectoryName);
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
    public boolean deleteDirectory(Long userId, String bucketName, String directoryPath) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteFile(Long userId, String bucketName, String filePath) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<Path> loadAll(Long userId) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Path loadbyPath(Long userId, String bucket, Path filePath) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteArrayResource loadAsResourcebyPath(Long userId, String bucketName, Path filePath)
            throws StorageEntityNotFoundException, UserNotFoundException {
        logger.info("Fetching file from user " + userId + " in bucket " + bucketName + " with path " + filePath);
        User user = getUserById(userId);
        Bucket bucket = bucketRepository.getBucketByName(user, bucketName);
        BucketFile file = bucketFileRepository.getBucketFileByPath(bucket, filePath);

        return getFileResource(user, file);
    }

    private ByteArrayResource getFileResource(User user, BucketFile file) {
        FileAESCryptographer fileAESCryptographer;
        try {
            fileAESCryptographer = new FileAESCryptographer(user.secretKey(), "AES/CBC/PKCS5Padding");
        } catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
            logger.error("Error while decrypting file", e);
            throw new RuntimeException(e);
        }

        return new ByteArrayResource(fileAESCryptographer.decryptFile(file.storedPath()));
    }

    private StorageSlaveServer getStorageServer() {
        if(storageServer == null) loadOrCreateStorageServer();
        return storageServer;
    }

    private void loadOrCreateStorageServer() {
        // Load storage server from database or create a new one if it does not exist
        Dotenv dotenv = Dotenv.load();
        String servername = dotenv.get("SERVER_NAME");
        String host = dotenv.get("SERVER_HOST");
        int port = Integer.parseInt(dotenv.get("SERVER_PORT"));
        Optional<StorageSlaveServer> optStorageServer = storageSlaveServerRepository.findStorageServerByIp(host, port);
        if(optStorageServer.isPresent()) {
            storageServer = optStorageServer.get();
        } else {
            File file = new File(root());
            long freeSpace = file.getUsableSpace();
            long totalSpace = file.getTotalSpace();
            storageServer = new StorageSlaveServer(servername, host, port, freeSpace, totalSpace);
            storageSlaveServerRepository.save(storageServer);
        }
    }

    private void storeEncryptedFileOnDisk(User user, String path, MultipartFile file) throws Exception {
        SecretKey secretkey = user.secretKey();
        FileCryptographer fc = new FileAESCryptographer(secretkey, "AES/CBC/PKCS5Padding");
        fc.encryptFile(path, file.getBytes());
    }

    /**
     * Get the path of a directory by its name
     * @param pathString The path to search for
     */
    private Path getDirectoryPathByString(String pathString) {
        Path path = Path.of(File.separator);
        if(pathString != null && !pathString.isEmpty()) {
            path = path.resolve(pathString);
        }
        return path;
    }

    private User getUserById(Long userId) throws UserNotFoundException {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

}
