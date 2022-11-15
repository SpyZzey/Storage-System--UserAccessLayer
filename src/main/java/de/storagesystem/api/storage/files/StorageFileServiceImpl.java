package de.storagesystem.api.storage.files;

import de.storagesystem.api.cryptography.FileAESCryptographer;
import de.storagesystem.api.cryptography.FileCryptographer;
import de.storagesystem.api.exceptions.StorageEntityCreationException;
import de.storagesystem.api.exceptions.StorageEntityNotFoundException;
import de.storagesystem.api.exceptions.UserNotFoundException;
import de.storagesystem.api.storage.buckets.Bucket;
import de.storagesystem.api.storage.buckets.BucketDAO;
import de.storagesystem.api.storage.buckets.BucketServiceImpl;
import de.storagesystem.api.storage.buckets.StorageService;
import de.storagesystem.api.storage.folders.StorageFolder;
import de.storagesystem.api.storage.folders.StorageFolderDAO;
import de.storagesystem.api.storage.servers.StorageServerDAO;
import de.storagesystem.api.users.User;
import de.storagesystem.api.users.UserDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.File;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class StorageFileServiceImpl extends StorageService implements StorageFileService {
    private static final Logger logger = LogManager.getLogger(BucketServiceImpl.class);

    public StorageFileServiceImpl(
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
            Long userId,
            String bucketName,
            String folderName,
            MultipartFile file) {
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
            String uuidFilename = serverPrefix + UUID.randomUUID();
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
    public boolean deleteFile(Long userId, String bucketName, String filePath) {
        return false;
    }

    @Override
    public Stream<Path> loadAll(Long userId) {
        return null;
    }

    @Override
    public Stream<Path> loadAllByPath(Long userId, String searchPath) {
        return null;
    }

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
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Bucket bucket = bucketRepository.getBucketByName(user, bucketName);
        StorageFile file = storageFileRepository.getBucketFileByPath(bucket, filePath);

        return getStorageFileResource(user, file);
    }

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

    private void storeEncryptedFileOnDisk(User user, String path, MultipartFile file) throws Exception {
        SecretKey secretkey = user.secretKey();
        FileCryptographer fc = new FileAESCryptographer(secretkey, "AES/CBC/PKCS5Padding");
        fc.encryptFile(path, file.getBytes());
    }
}
