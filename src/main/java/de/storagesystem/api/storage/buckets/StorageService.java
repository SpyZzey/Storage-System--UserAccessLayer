package de.storagesystem.api.storage.buckets;

import de.storagesystem.api.exceptions.StorageEntityCreationException;
import de.storagesystem.api.storage.files.StorageFileDAO;
import de.storagesystem.api.storage.folders.StorageFolderDAO;
import de.storagesystem.api.storage.servers.StorageServer;
import de.storagesystem.api.storage.servers.StorageServerDAO;
import de.storagesystem.api.users.UserDAO;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Random;

public class StorageService {

    private static Path ROOT;
    private static final int maxSubpartitionsPerPartition = 2;
    private static final int maxUserRootsPerSubpartition = 2;

    private static final int maxNumberOfPartitions = 1000;
    private static final int maxNumberOfFilesInPartition = 10000;

    protected String serverPrefix;
    @Autowired
    protected final UserDAO userRepository;
    @Autowired
    protected final BucketDAO bucketRepository;
    @Autowired
    protected final StorageFolderDAO bucketFolderRepository;
    @Autowired
    protected final StorageFileDAO storageFileRepository;
    @Autowired
    protected final StorageServerDAO storageServerRepository;
    private StorageServer storageServer;

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

    public void init() {
        Dotenv dotenv = Dotenv.load();
        // Load the storage root path from the .env file
        ROOT = Path.of(dotenv.get("STORAGE_ROOT"));
        // Load server prefix from the .env file
        serverPrefix = dotenv.get("SERVER_PREFIX");
    }

    /**
     * Get the path of a folder by its path string
     * @param pathString The path to search for
     */
    protected Path getFolderPathByString(String pathString) {
        Path path = Path.of(File.separator);
        if(pathString != null && !pathString.isEmpty()) {
            path = path.resolve(pathString);
        }
        return path;
    }

    public String getFileStoragePath(long userId) {
        Random random = new Random(System.currentTimeMillis());
        int partition = random.nextInt(maxNumberOfPartitions);
        int subpartition = random.nextInt(maxNumberOfPartitions);
        String path = getUserStoragePath(userId) + File.separator + partition + File.separator + subpartition;
        return createPartition(path);
    }


    public String getUserStoragePath(long userId) {
        int partition = (int) (userId / (maxUserRootsPerSubpartition * maxSubpartitionsPerPartition)) + 1;
        int subpartition = (int) (userId / maxUserRootsPerSubpartition) + 1;
        String path = ROOT + File.separator + "p" + partition + File.separator + "sub" + subpartition + File.separator + "u" + userId;
        return createPartition(path);
    }

    public String createPartition(String partitionPath) {
        File file = new File(partitionPath);
        if(!file.isDirectory() && !file.mkdirs()) {
            throw new StorageEntityCreationException("Could not create partition " + partitionPath);
        }
        return partitionPath;
    }

    protected StorageServer getStorageServer() {
        if(storageServer == null) loadOrCreateStorageServer();
        return storageServer;
    }


    private void loadOrCreateStorageServer() {
        // Load storage server from database or create a new one if it does not exist
        Dotenv dotenv = Dotenv.load();
        String servername = dotenv.get("SERVER_NAME");
        String host = dotenv.get("SERVER_HOST");
        int port = Integer.parseInt(dotenv.get("SERVER_PORT"));
        Optional<StorageServer> optStorageServer = storageServerRepository.findStorageServerByIp(host, port);
        if(optStorageServer.isPresent()) {
            storageServer = optStorageServer.get();
        } else {
            File file = new File(root());
            long freeSpace = file.getUsableSpace();
            long totalSpace = file.getTotalSpace();
            storageServer = new StorageServer(servername, host, port, freeSpace, totalSpace);
            storageServerRepository.save(storageServer);
        }
    }

    public String root() {
        return ROOT.toString();
    }

}
