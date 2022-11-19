package de.storagesystem.api.storage.servers;

import de.storagesystem.api.cryptography.FileAESCryptographer;
import de.storagesystem.api.cryptography.FileCryptographer;
import de.storagesystem.api.exceptions.InvalidTokenException;
import de.storagesystem.api.exceptions.StorageEntityCreationException;
import de.storagesystem.api.exceptions.StorageEntityNotFoundException;
import de.storagesystem.api.exceptions.UserNotFoundException;
import de.storagesystem.api.storage.StorageFileService;
import de.storagesystem.api.storage.StorageService;
import de.storagesystem.api.storage.buckets.Bucket;
import de.storagesystem.api.storage.buckets.BucketDAO;
import de.storagesystem.api.storage.buckets.BucketServiceImpl;
import de.storagesystem.api.storage.files.StorageFile;
import de.storagesystem.api.storage.files.StorageFileDAO;
import de.storagesystem.api.storage.folders.StorageFolder;
import de.storagesystem.api.storage.folders.StorageFolderDAO;
import de.storagesystem.api.users.User;
import de.storagesystem.api.users.UserDAO;
import de.storagesystem.api.users.UserService;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * @author Simon Brebeck on 19.11.2022
 */
@Service("storageServerFileService")
public class StorageServerFileServiceImpl extends StorageService implements StorageFileService {

    /**
     * The {@link Logger} for this class
     */
    private static final Logger logger = LogManager.getLogger(BucketServiceImpl.class);

    /**
     * The {@link UserService} to access the user data
     */
    private final UserService userService;

    /**
     * Instantiates a new Storage file service.
     *
     * @param storageServerRepository the storage server repository
     * @param bucketFolderRepository  the bucket folder repository
     * @param storageFileRepository   the storage file repository
     * @param bucketRepository        the bucket repository
     * @param userRepository          the user repository
     * @param userService             the user service
     */
    public StorageServerFileServiceImpl(
            StorageServerDAO storageServerRepository,
            StorageFolderDAO bucketFolderRepository,
            StorageFileDAO storageFileRepository,
            BucketDAO bucketRepository,
            UserDAO userRepository, UserService userService) {
        super(storageServerRepository, bucketFolderRepository, storageFileRepository, bucketRepository, userRepository);
        this.userService = userService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PostConstruct
    public void init() {
        super.init();

        // Creates server storage folder if it does not exist
        File file = new File(root());
        if (file.exists()) return;

        if (file.mkdirs()) {
            logger.info("Created upload folder");
        } else {
            logger.error("Could not create upload folder");
            throw new StorageEntityCreationException("Could not create upload folder");
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean storeFile(
            String auth,
            String bucketName,
            String folderName,
            MultipartFile file) throws InvalidTokenException {
        long userId = userService.getUserId(auth);
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Bucket bucketObj = bucketRepository.getBucketByName(user, bucketName);

        // Get the path to the folder where the file should be stored
        Path parentPath = getFolderPathByString(folderName);
        StorageFolder parentObj = bucketFolderRepository.getFolderIfNotBucketElseNull(bucketObj, parentPath);

        // Check if the file already exists in the database
        if(storageFileRepository.exists(bucketObj, parentPath.toString(), file.getOriginalFilename())) {
            logger.info("File already exists: " + file.getOriginalFilename());
            return false;
        }

        try {
            // Create a new file entity on disk
            String uuidFilename = serverPrefix() + UUID.randomUUID();
            String storedPath = getFileStoragePath(user.id()) + File.separator + uuidFilename;
            storeEncryptedFileOnDisk(user, storedPath, file);

            // Create the new file in the database
            StorageFile fileObj = new StorageFile(
                    getStorageServer(),
                    bucketObj,
                    parentObj,
                    storedPath,
                    file.getOriginalFilename(),
                    uuidFilename,
                    file.getContentType(),
                    file.getSize(),
                    user.bucketUser());
            storageFileRepository.save(fileObj);
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
    public boolean deleteFile(String auth, String bucketName, String filePath) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<Path> loadAll(String auth) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<Path> loadAllByPath(String auth, String searchPath) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Path loadbyPath(String auth, String bucket, String folder, String fileName) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteArrayResource loadAsResourcebyPath(String auth, String bucketName, String folder, String fileName)
            throws StorageEntityNotFoundException, UserNotFoundException, InvalidTokenException {
        long userId = userService.getUserId(auth);
        logger.info("Fetching file from user " + userId + " in bucket " + bucketName + " with path " + folder + "/" + fileName);
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Bucket bucket = bucketRepository.getBucketByName(user, bucketName);
        StorageFile file = storageFileRepository.getBucketFileByPath(bucket, Path.of(folder, fileName));

        return getStorageFileResource(user, file);
    }

    /**
     * Decrypts the file represented by the {@link StorageFile} file of a user and returns it as a {@link ByteArrayResource}
     * @param user the user who owns the file
     * @param file the file to decrypt and return
     * @return the decrypted file as a {@link ByteArrayResource}
     */
    private ByteArrayResource getStorageFileResource(User user, StorageFile file) {
        FileAESCryptographer fileAESCryptographer;
        try {
            fileAESCryptographer = new FileAESCryptographer(user.secretKey(), "AES/CBC/PKCS5Padding");
        } catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
            logger.error("Error while decrypting file", e);
            throw new RuntimeException(e);
        }

        return new ByteArrayResource(fileAESCryptographer.decryptFile(file.storedPath()));
    }

    /**
     * Encrypts the {@link MultipartFile} file and stores it on disk at a given path
     * @param user the user who owns the file
     * @param path the path where the file should be stored
     * @param file the file to encrypt and store
     * @throws NoSuchAlgorithmException if the RSA algorithm is not available
     * @throws NoSuchPaddingException if the RSA algorithm is not available
     * @throws IOException if an I/O error occurs
     */
    private void storeEncryptedFileOnDisk(User user, String path, MultipartFile file)
            throws NoSuchPaddingException, NoSuchAlgorithmException, IOException {
        SecretKey secretkey = user.secretKey();
        FileCryptographer fc = new FileAESCryptographer(secretkey, "AES/CBC/PKCS5Padding");
        fc.encryptFile(path, file.getBytes());
    }
}
