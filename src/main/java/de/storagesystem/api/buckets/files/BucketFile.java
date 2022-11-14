package de.storagesystem.api.buckets.files;

import com.sun.istack.NotNull;
import de.storagesystem.api.buckets.Bucket;
import de.storagesystem.api.buckets.BucketItem;
import de.storagesystem.api.buckets.directories.BucketDirectory;
import de.storagesystem.api.buckets.users.BucketUser;
import de.storagesystem.api.storage.StorageSlaveServer;

import javax.persistence.*;

@Entity
public class BucketFile extends BucketItem {

    @ManyToOne
    private StorageSlaveServer storageServer;

    @NotNull
    private String fileType;
    @NotNull
    private Long size;

    @NotNull
    private String storedName;

    @NotNull
    private String storedPath;

    protected BucketFile() {
    }

    protected BucketFile(StorageSlaveServer storageServer) {
        this.storageServer = storageServer;
    }

    public BucketFile(
            StorageSlaveServer storageServer,
            Bucket bucket,
            BucketDirectory parentDirectory,
            String storedPath,
            String originalName,
            String storedName,
            String fileType,
            Long size,
            BucketUser creator) {
        super(bucket, parentDirectory, originalName, creator);
        this.storageServer = storageServer;
        this.storedPath = storedPath;
        this.storedName = storedName;
        this.fileType = fileType;
        this.size = size;
    }

    public String fileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long size() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String storedPath() {
        return storedPath;
    }

    public void setStoredPath(String storedPath) {
        this.storedPath = storedPath;
    }

    public String storedName() {
        return storedName;
    }

    public void setStoredName(String storedName) {
        this.storedName = storedName;
    }


}
