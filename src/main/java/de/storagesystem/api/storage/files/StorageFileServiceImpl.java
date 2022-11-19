package de.storagesystem.api.storage.files;

import de.storagesystem.api.cryptography.FileAESCryptographer;
import de.storagesystem.api.cryptography.FileCryptographer;
import de.storagesystem.api.exceptions.InvalidTokenException;
import de.storagesystem.api.exceptions.StorageEntityCreationException;
import de.storagesystem.api.exceptions.StorageEntityNotFoundException;
import de.storagesystem.api.exceptions.UserNotFoundException;
import de.storagesystem.api.storage.StorageFileService;
import de.storagesystem.api.storage.buckets.Bucket;
import de.storagesystem.api.storage.buckets.BucketDAO;
import de.storagesystem.api.storage.buckets.BucketServiceImpl;
import de.storagesystem.api.storage.StorageService;
import de.storagesystem.api.storage.folders.StorageFolderDAO;
import de.storagesystem.api.storage.servers.StorageServer;
import de.storagesystem.api.storage.servers.StorageServerDAO;
import de.storagesystem.api.users.User;
import de.storagesystem.api.users.UserDAO;
import de.storagesystem.api.users.UserService;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Simon Brebeck
 */
@Service("storageFileService")
public class StorageFileServiceImpl extends StorageService implements StorageFileService {

    /**
     * The {@link Logger} for this class
     */
    private static final Logger logger = LogManager.getLogger(BucketServiceImpl.class);

    /**
     * The {@link UserService} to access the user data
     */
    private final UserService userService;

    private String protocol;

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
    public StorageFileServiceImpl(
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
        Dotenv dotenv = Dotenv.load();
        protocol = dotenv.get("TRANSFER_PROTOCOL");
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean storeFile(
            String auth,
            String bucketName,
            String folderName,
            MultipartFile file)
            throws
            HttpClientErrorException,
            StorageEntityNotFoundException,
            StorageEntityCreationException,
            UserNotFoundException {
        // Load storage server from database or create a new one if it does not exist
        loadOrCreateStorageServer();

        StorageServer server = storageServerRepository.findStorageServerByFreeCapacity(file.getSize())
                .orElseThrow(() -> new StorageEntityNotFoundException("No storage server with enough free capacity found"));

        logger.info("Found storage server with enough free capacity, uploading file to " + server.name() + "(" + server.host() + ":" + server.port() + ")");

        RestTemplate restTemplate = new RestTemplate();
        String url = protocol + "://" + server.host() + ":" + server.port() +
                "/api/internal/files/?bucket=" + bucketName + "&folder=" + folderName;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", auth);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        return response.getStatusCode().is2xxSuccessful();
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
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Bucket bucket = bucketRepository.getBucketByName(user, bucketName);
        StorageFile file = storageFileRepository.getBucketFileByPath(bucket, Path.of(folder));
        StorageServer server = file.storageServer();

        RestTemplate restTemplate = new RestTemplate();
        String url = protocol + "://" + server.host() + ":" + server.port() +
                "/api/internal/files/" + bucketName + "/" + folder + "/" + fileName;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(auth);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<ByteArrayResource> response = restTemplate.exchange(url, HttpMethod.GET, request, ByteArrayResource.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new StorageEntityNotFoundException("File not found");
        }
    }

}
