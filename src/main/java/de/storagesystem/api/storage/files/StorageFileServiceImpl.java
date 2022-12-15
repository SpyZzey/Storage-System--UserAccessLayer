package de.storagesystem.api.storage.files;

import com.fasterxml.jackson.databind.node.ObjectNode;
import de.storagesystem.api.auth.Authentication;
import de.storagesystem.api.auth.RSAAuthentication;
import de.storagesystem.api.cryptography.FileAESCryptographer;
import de.storagesystem.api.cryptography.FileCryptographer;
import de.storagesystem.api.exceptions.InvalidTokenException;
import de.storagesystem.api.exceptions.StorageEntityCreationException;
import de.storagesystem.api.exceptions.StorageEntityNotFoundException;
import de.storagesystem.api.exceptions.UserNotFoundException;
import de.storagesystem.api.properties.StorageServerConfigProperty;
import de.storagesystem.api.storage.buckets.Bucket;
import de.storagesystem.api.storage.buckets.BucketDAO;
import de.storagesystem.api.storage.StorageService;
import de.storagesystem.api.storage.folders.StorageFolder;
import de.storagesystem.api.storage.folders.StorageFolderDAO;
import de.storagesystem.api.servers.StorageServer;
import de.storagesystem.api.servers.StorageServerDAO;
import de.storagesystem.api.users.User;
import de.storagesystem.api.users.UserDAO;

import de.storagesystem.api.util.ResponseBuilder;
import de.storagesystem.api.util.ResponseState;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
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
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.Objects;

/**
 * @author Simon Brebeck
 */
@Service("storageFileService")
public class StorageFileServiceImpl extends StorageService implements StorageFileService {

    /**
     * The {@link Logger} for this class
     */
    private static final Logger logger = LogManager.getLogger(StorageFileServiceImpl.class);

    private String protocol;

    private String storageAuth;

    private final StorageServerConfigProperty storageServerConfigProperties;
    private Authentication auth;

    /**
     * Instantiates a new Storage file service.
     *
     * @param storageServerRepository the storage server repository
     * @param bucketFolderRepository  the bucket folder repository
     * @param storageFileRepository   the storage file repository
     * @param bucketRepository        the bucket repository
     * @param userRepository          the user repository
     */
    public StorageFileServiceImpl(
            StorageServerConfigProperty storageServerConfigProperties,
            StorageServerDAO storageServerRepository,
            StorageFolderDAO bucketFolderRepository,
            StorageFileDAO storageFileRepository,
            BucketDAO bucketRepository,
            UserDAO userRepository) {
        super(storageServerRepository, bucketFolderRepository, storageFileRepository, bucketRepository, userRepository);
        this.storageServerConfigProperties = storageServerConfigProperties;
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
        storageAuth = dotenv.get("STORAGE_AUTH");
        String issuer = storageServerConfigProperties.getStorage().getIssuer();
        String publicKeyPath = storageServerConfigProperties.getStorage().getPublicKey();
        String privateKeyPath = storageServerConfigProperties.getStorage().getPrivateKey();
        try {
            this.auth = new RSAAuthentication(issuer, publicKeyPath, privateKeyPath);
        } catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException e) {
            logger.error("Could not create the Authentication", e);
            throw new RuntimeException(e);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<ObjectNode> storeFile(
            long userId,
            String bucketName,
            String folderName,
            MultipartFile file)
            throws
            HttpClientErrorException,
            StorageEntityNotFoundException,
            StorageEntityCreationException,
            UserNotFoundException {
        StorageServer server = storageServerRepository.findStorageServerByFreeCapacity(file.getSize())
                .orElseThrow(() -> new StorageEntityNotFoundException("No storage server with enough free capacity found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
        logger.info("Sending file to storage server: " + server.getName() + " (" + server.getHost() + ":" + server.getPort() + ")");

        ResponseEntity<ObjectNode> response = uploadFileToStorageServer(server, user, file);
        if(response.getStatusCode() == HttpStatus.OK && response.hasBody()) {
            String storedPath = Objects.requireNonNull(response.getBody()).get("path").asText();
            try {
                return insertFileIntoDatabase(server, user, bucketName, folderName, storedPath, file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new StorageEntityCreationException("Could not upload file to storage server");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<ObjectNode> deleteFile(long userId, String bucketName, String filePath) throws InvalidTokenException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
        Bucket bucket = bucketRepository.getBucketByName(user, bucketName);
        StorageFile file = storageFileRepository.getBucketFileByPath(bucket, filePath);
        StorageServer server = file.getStorageServer();

        logger.info("Deleting file from storage server: " + server.getName() + "(" + server.getHost() + ":" + server.getPort() + ")");
        ResponseEntity<ObjectNode> response = deleteFileFromStorageServer(server, file);
        if(response.getStatusCode() == HttpStatus.OK && response.hasBody()) {
            return deleteFileFromDatabase(file);
        } else {
            throw new StorageEntityCreationException("Could not delete file");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<ObjectNode> loadAllByPage(long userId, long page) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<ObjectNode> loadAllByPath(long userId, String searchPath) {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<Resource> loadFile(long userId, String bucketName, String filePath)
            throws StorageEntityNotFoundException, UserNotFoundException, InvalidTokenException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Bucket bucket = bucketRepository.getBucketByName(user, bucketName);
        StorageFile file = storageFileRepository.getBucketFileByPath(bucket, filePath);
        StorageServer server = file.getStorageServer();
        ResponseEntity<Resource> response = fetchFile(server, user, file);

        SecretKey secretKey = user.getSecretKey();
        try {
            FileAESCryptographer cryptographer = new FileAESCryptographer(secretKey, "AES/CBC/PKCS5Padding");

            byte[] encryptedData = Objects.requireNonNull(response.getBody()).getInputStream().readAllBytes();;
            byte[] decryptedData = cryptographer.decryptFile(encryptedData);
            Resource resource = new ByteArrayResource(decryptedData);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | IOException e) {
            logger.error("Error while creating cryptographer", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private ResponseEntity<ObjectNode> deleteFileFromDatabase(StorageFile file) {
        StorageFolder folder = file.getParent();
        folder.removeFile(file);

        storageFileRepository.delete(file);
        bucketFolderRepository.save(folder);
        ObjectNode response = new ResponseBuilder().build(ResponseState.OK, "File deleted");
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<ObjectNode> insertFileIntoDatabase(
            StorageServer server,
            User user,
            String bucketName,
            String folderName,
            String storedPath,
            MultipartFile file) throws IOException {
        Bucket bucket = bucketRepository.getBucketByName(user, bucketName);
        StorageFolder folder = bucketFolderRepository.getFolder(bucket, folderName);

        StorageFile storageFile = new StorageFile(
                server,
                storedPath,
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                user);

        folder.addFile(storageFile);

        storageFileRepository.save(storageFile);
        bucketFolderRepository.save(folder);

        ObjectNode response = new ResponseBuilder()
                .add("path", storageFile.getPath())
                .build(ResponseState.OK, "File stored.");

        return ResponseEntity.ok(response);
    }

    private ResponseEntity<ObjectNode> uploadFileToStorageServer(StorageServer server, User user, MultipartFile file) {
        RestTemplate restTemplate = new RestTemplate();
        String url = protocol + "://" + server.getHost() + ":" + server.getPort() + "/api/files/" + user.getId();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(storageAuth);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
        ContentDisposition contentDisposition = ContentDisposition
                .builder("form-data")
                .name("file")
                .filename(Objects.requireNonNull(file.getOriginalFilename()))
                .build();
        fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());

        HttpEntity<byte[]> fileEntity = new HttpEntity<>(encryptFile(user, file), fileMap);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileEntity);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ParameterizedTypeReference<ObjectNode> responseType = new ParameterizedTypeReference<>() {};
        return restTemplate.exchange(url, HttpMethod.POST, requestEntity, responseType);
    }


    private ResponseEntity<ObjectNode> deleteFileFromStorageServer(StorageServer server, StorageFile file) {
        RestTemplate restTemplate = new RestTemplate();
        String url = protocol + "://" + server.getHost() + ":" + server.getPort() +
                "/api/files/" + file.getCreator().getId() +
                "/" + URLEncoder.encode(file.getStoredPath().substring(1), StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(storageAuth);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ParameterizedTypeReference<ObjectNode> responseType = new ParameterizedTypeReference<>() {};
        return restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, responseType);
    }

    private ResponseEntity<Resource> fetchFile(StorageServer server, User user, StorageFile file) {
        RestTemplate restTemplate = new RestTemplate();
        String url = protocol + "://" + server.getHost() + ":" + server.getPort() +
                "/api/files/" + user.getId() +
                "/" + URLEncoder.encode(file.getStoredPath().substring(1), StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(storageAuth);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, request, Resource.class);
    }

    private byte[] encryptFile(User user, MultipartFile file) {
        try {
            FileCryptographer cryptographer = new FileAESCryptographer(user.getSecretKey(), "AES/CBC/PKCS5Padding");
            return cryptographer.encryptFile(file.getBytes());
        } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateServerToken() {
        return this.auth.createToken(Map.of("server_type", "storage-master-server"));
    }
}
