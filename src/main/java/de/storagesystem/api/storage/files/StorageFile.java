package de.storagesystem.api.storage.files;

import com.sun.istack.NotNull;
import de.storagesystem.api.storage.buckets.Bucket;
import de.storagesystem.api.storage.StorageItem;
import de.storagesystem.api.storage.folders.StorageFolder;
import de.storagesystem.api.users.BucketUser;
import de.storagesystem.api.storage.servers.StorageServer;

import javax.persistence.*;

@Entity
public class StorageFile extends StorageItem {

    @ManyToOne
    private StorageServer storageServer;

    @NotNull
    private String fileType;
    @NotNull
    private Long size;

    @NotNull
    private String storedName;

    @NotNull
    private String storedPath;

    @NotNull
    private boolean deleted = false;

    protected StorageFile() {
    }

    protected StorageFile(StorageServer storageServer) {
        this.storageServer = storageServer;
    }

    public StorageFile(
            StorageServer storageServer,
            Bucket bucket,
            StorageFolder parentDirectory,
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

    public boolean wasDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
