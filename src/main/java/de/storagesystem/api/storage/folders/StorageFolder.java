package de.storagesystem.api.storage.folders;

import de.storagesystem.api.storage.StorageItem;
import de.storagesystem.api.storage.files.StorageFile;
import de.storagesystem.api.users.User;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.List;

/**
 * @author Simon Brebeck
 */
@Entity
@Table(
        name = "storage_folders",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"bucket_id", "path"})}
)
public class StorageFolder extends StorageItem {

    @OneToMany
    private List<StorageFolder> folders;

    @OneToMany
    private List<StorageFile> files;

    /**
     * Instantiates a new StorageFolder.
     */
    protected StorageFolder() {

    }

    /**
     * Instantiates a new StorageFolder.
     * @param originalName The original name of the folder.
     * @param creator The {@link User} who created the folder.
     */
    public StorageFolder(String originalName, User creator) {
        super(originalName, creator);
    }


    /**
     * Getter for the list of {@link StorageFile}s that are in the folder.
     * @return List the list of {@link StorageFile}s that are in the folder.
     */
    public List<StorageFile> getFiles() {
        return files;
    }

    /**
     * Getter for the list of {@link StorageFolder}s that are in the folder.
     * @return List the list of {@link StorageFolder}s that are in the folder.
     */
    public List<StorageFolder> getFolders() {
        return folders;
    }

    /**
     * Adds a {@link StorageFile} to the folder.
     * @param file The {@link StorageFile} to add.
     */
    public void addFile(StorageFile file) {
        files.add(file);
        file.setParent(this);
        file.setBucket(getBucket());
    }

    /**
     * Adds a {@link StorageFolder} to the folder.
     * @param folder The {@link StorageFolder} to add.
     */
    public void addFolder(StorageFolder folder) {
        folders.add(folder);
        folder.setParent(this);
        folder.setBucket(getBucket());
    }

    /**
     * Removes a {@link StorageFile} from the folder.
     * @param file The {@link StorageFile} to remove.
     */
    public void removeFile(StorageFile file) {
        files.remove(file);
        file.setParent(null);
        file.setBucket(null);
    }

    /**
     * Removes a {@link StorageFolder} from the folder.
     * @param folder The {@link StorageFolder} to remove.
     */
    public void removeFolder(StorageFolder folder) {
        folders.remove(folder);
        folder.setParent(null);
        folder.setBucket(null);
    }

    /**
     * Setter for the list of {@link StorageFile}s that are in the folder.
     * @param files The list of {@link StorageFile}s that are in the folder.
     */
    public void setFiles(List<StorageFile> files) {
        for(StorageFile file : this.files) {
            file.setParent(null);
            file.setBucket(null);
        }
        this.files = files;
        for (StorageFile file : files) {
            file.setParent(this);
            file.setBucket(getBucket());
        }
    }

    /**
     * Setter for the list of {@link StorageFolder}s that are in the folder.
     * @param folders The list of {@link StorageFolder}s that are in the folder.
     */
    public void setFolders(List<StorageFolder> folders) {
        for(StorageFolder folder : this.folders) {
            folder.setParent(null);
            folder.setBucket(null);
        }
        this.folders = folders;
        for (StorageFolder folder : folders) {
            folder.setParent(this);
            folder.setBucket(getBucket());
        }
    }

}
