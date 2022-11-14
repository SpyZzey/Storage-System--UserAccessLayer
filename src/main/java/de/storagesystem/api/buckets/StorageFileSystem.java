package de.storagesystem.api.buckets;

import de.storagesystem.api.exceptions.StorageEntityCreationException;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;
import java.nio.file.Path;
import java.util.Random;

public class StorageFileSystem {

    private static Path ROOT;
    private static final int maxSubpartitionsPerPartition = 2;
    private static final int maxUserRootsPerSubpartition = 2;

    private static final int maxNumberOfPartitions = 1000;
    private static final int maxNumberOfFilesInPartition = 10000;

    public void init() {
        Dotenv dotenv = Dotenv.load();
        ROOT = Path.of(dotenv.get("STORAGE_ROOT"));
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

    public String root() {
        return ROOT.toString();
    }

}
