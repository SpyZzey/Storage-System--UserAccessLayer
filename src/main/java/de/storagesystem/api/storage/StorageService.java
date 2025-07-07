package de.storagesystem.api.storage;

import de.storagesystem.api.exceptions.StorageEntityCreationException;
import de.storagesystem.api.storage.buckets.BucketDAO;
import de.storagesystem.api.storage.files.StorageFileDAO;
import de.storagesystem.api.storage.folders.StorageFolderDAO;
import de.storagesystem.api.servers.StorageServer;
import de.storagesystem.api.servers.StorageServerDAO;
import de.storagesystem.api.users.UserDAO;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Random;

/**
 * @author Simon Brebeck
 */
public class StorageService {
    /**
     * The {@link Logger} for this class
     */
    private static final Logger logger = LogManager.getLogger(StorageService.class);

    /**
     * The {@link Path} to the root directory of the storage
     */
    private static String ROOT;

    /**
     * The authentication token for the storage server
     */
    private static String serverAuth;


    /**
     * The max amount of subpartitions of users in a user-partition
     */
    private static final int maxSubpartitionsPerPartition = 2;

    /**
     * The max amount of user storage roots in a subpartition
     */
    private static final int maxUserRootsPerSubpartition = 2;

    /**
     * The max amount of folders in a folder
     */
    private static final int maxNumberOfPartitions = 1000;
    /**
     * The max amount of folders in a subfolder
     */
    private static final int maxNumberOfFilesInPartition = 10000;

    /**
     * The prefix of the server
     */
    private static String serverPrefix;

    /**
     * The {@link UserDAO} user repository
     */
    @Autowired
    protected final UserDAO userRepository;

    /**
     * The {@link BucketDAO} bucket repository
     */
    @Autowired
    protected final BucketDAO bucketRepository;

    /**
     * The {@link StorageFolderDAO} folder repository
     */
    @Autowired
    protected final StorageFolderDAO bucketFolderRepository;

    /**
     * The {@link StorageFileDAO} file repository
     */
    @Autowired
    protected final StorageFileDAO storageFileRepository;

    /**
     * The {@link StorageServerDAO} server repository
     */
    @Autowired
    protected final StorageServerDAO storageServerRepository;

    /**
     * The current {@link StorageServer}
     */
    private StorageServer storageServer;

    /**
     * Instantiates a new Storage service.
     * @param storageServerRepository the storage server repository
     * @param bucketFolderRepository the bucket folder repository
     * @param storageFileRepository the storage file repository
     * @param bucketRepository the bucket repository
     * @param userRepository the user repository
     */
    public StorageService(
            StorageServerDAO storageServerRepository,
            StorageFolderDAO bucketFolderRepository,
            StorageFileDAO storageFileRepository,
            BucketDAO bucketRepository,
            UserDAO userRepository) {
        this.storageServerRepository = storageServerRepository;
        this.bucketFolderRepository = bucketFolderRepository;
        this.storageFileRepository = storageFileRepository;
        this.bucketRepository = bucketRepository;
        this.userRepository = userRepository;
    }

    /**
     * Initializes the storage service
     */
    public void init() {
        Dotenv dotenv = Dotenv.load();
        // Load the storage root path from the .env file
        ROOT = dotenv.get("STORAGE_ROOT");
        // Load server prefix from the .env file
        serverPrefix = dotenv.get("SERVER_PREFIX");

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
     * Generate a path where a file can be stored for a user
     * @param userId The id of the user
     * @return The path where the file can be stored
     */
    public String getFileStoragePath(long userId) {
        Random random = new Random(System.currentTimeMillis());
        int partition = random.nextInt(maxNumberOfPartitions);
        int subpartition = random.nextInt(maxNumberOfPartitions);
        String path = getUserStoragePath(userId) + File.separator + partition + File.separator + subpartition;
        return createPartition(path);
    }


    /**
     * Generate the path to the user root directory
     * @param userId The id of the user
     * @return The path to the user root directory
     */
    public String getUserStoragePath(long userId) {
        int partition = (int) (userId / (maxUserRootsPerSubpartition * maxSubpartitionsPerPartition)) + 1;
        int subpartition = (int) (userId / maxUserRootsPerSubpartition) + 1;
        String path = ROOT + File.separator + "p" + partition + File.separator + "sub" + subpartition + File.separator + "u" + userId;
        return createPartition(path);
    }

    /**
     * Create a folder and all its parents if it does not exist
     * @param partitionPath The path to the folder to create
     * @return The path to the folder
     */
    public String createPartition(String partitionPath) {
        File file = new File(partitionPath);
        if(!file.isDirectory() && !file.mkdirs()) {
            throw new StorageEntityCreationException("Could not create partition " + partitionPath);
        }
        return partitionPath;
    }

    /**
     * Get the current {@link StorageServer} and create it if it does not exist
     * @return The current {@link StorageServer}
     */
    protected StorageServer getStorageServer() {
        if(storageServer == null) loadOrCreateStorageServer();
        return storageServer;
    }


    /**
     * Load the current {@link StorageServer} or create it if it does not exist
     */
    protected void loadOrCreateStorageServer() {
        // Load storage server from database or create a new one if it does not exist
        Dotenv dotenv = Dotenv.load();
        String servername = dotenv.get("SERVER_NAME");
        String host = dotenv.get("SERVER_HOST");
        int port = Integer.parseInt(dotenv.get("SERVER_PORT"));
        Optional<StorageServer> optStorageServer = storageServerRepository.findStorageServerByIp(host, port);
        if(optStorageServer.isPresent()) {
            storageServer = optStorageServer.get();
            logger.info("Loaded storage server " + servername + " from database");
        } else {
            File file = new File(root());
            long freeSpace = file.getUsableSpace();
            long totalSpace = file.getTotalSpace();
            storageServer = new StorageServer(servername, host, port, freeSpace, totalSpace);
            storageServerRepository.save(storageServer);
            logger.info("Created storage server " + servername + " in database");
        }
    }

    /**
     * Get the root path of the storage
     * @return The root path of the storage
     */
    public String root() {
        return ROOT;
    }

    /**
     * Get the server prefix
     * @return The server prefix
     */
    public static String serverPrefix() {
        return serverPrefix;
    }
}
