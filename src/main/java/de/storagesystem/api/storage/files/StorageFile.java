package de.storagesystem.api.storage.files;

import com.sun.istack.NotNull;
import de.storagesystem.api.storage.StorageItem;
import de.storagesystem.api.servers.StorageServer;
import de.storagesystem.api.users.User;

import javax.persistence.*;

/**
 * @author Simon Brebeck
 */
@Entity
@Table(name = "storage_files")
public class StorageFile extends StorageItem {

    /**
     * The Server where this file is phiysically stored.
     */
    @ManyToOne
    private StorageServer storageServer;

    /**
     * The type of the file.
     */
    @NotNull
    private String fileType;

    /**
     * The size of the file.
     */
    @NotNull
    private Long size;

    /**
     * The path to the physically stored file.
     */
    @NotNull
    private String storedPath;

    /**
     * The boolean whether the file has been deleted.
     */
    @NotNull
    private boolean deleted = false;

    /**
     * Instantiates a new Storage file.
     */
    protected StorageFile() {
    }


    /**
     * Instantiates a new Storage file.
     * @param storageServer the storage server where the file is stored physically.
     */
    protected StorageFile(StorageServer storageServer) {
        this.storageServer = storageServer;
    }

    /**
     * Instantiates a new Storage file.
     *
     * @param storageServer     the storage server where the file is stored physically.
     * @param storedPath        the stored path
     * @param originalName      the original name of the file
     * @param fileType          the file type of the file
     * @param size              the size of the file
     * @param creator             the owner of the file
     */
    public StorageFile(
            StorageServer storageServer,
            String storedPath,
            String originalName,
            String fileType,
            Long size,
            User creator) {
        super(originalName, creator);
        this.storageServer = storageServer;
        this.storedPath = storedPath;
        this.fileType = fileType;
        this.size = size;
    }

    /**
     * Getter for the type of the file.
     * @return the type of the file.
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * Setter for the type of the file.
     * @param fileType the type of the file.
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    /**
     * Getter for the size of the file.
     * @return the size of the file.
     */
    public Long getSize() {
        return size;
    }

    /**
     * Setter for the size of the file.
     * @param size the size of the file.
     */
    public void setSize(Long size) {
        this.size = size;
    }

    /**
     * Getter for the path of the physically stored file.
     * @return the path of the physically stored file.
     */
    public String getStoredPath() {
        return storedPath;
    }

    /**
     * Setter for the path of the physically stored file.
     * @param storedPath the path of the physically stored file.
     */
    public void setStoredPath(String storedPath) {
        this.storedPath = storedPath;
    }

    /**
     * Getter for the storage server where the file is stored physically.
     * @return the storage server where the file is stored physically.
     */
    public StorageServer getStorageServer() {
        return storageServer;
    }

    /**
     * Setter for the storage server where the file is stored physically.
     * @param storageServer the storage server where the file is stored physically.
     */
    public void setStorageServer(StorageServer storageServer) {
        this.storageServer = storageServer;
    }

    /**
     * Getter for state of the file, whether the file has been deleted or not.
     * @return true if the file has been deleted, false if not.
     */
    public boolean wasDeleted() {
        return deleted;
    }

    /**
     * Setter for the state of the file, whether the file has been deleted or not.
     * @param deleted true if the file has been deleted, false if not.
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
