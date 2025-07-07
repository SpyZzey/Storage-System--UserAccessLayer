package de.storagesystem.api.storage;

import com.sun.istack.NotNull;
import de.storagesystem.api.storage.buckets.Bucket;
import de.storagesystem.api.storage.folders.StorageFolder;
import de.storagesystem.api.users.User;

import javax.persistence.*;

/**
 * @author Simon Brebeck
 */
@MappedSuperclass
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"bucket_id", "parent_id", "name"})
})
public class StorageItem {

    /**
     * The id of the storage item
     */
    @Id
    @SequenceGenerator(
            name = "file_sequence",
            sequenceName = "file_sequence",
            allocationSize = 1)
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "file_sequence")
    private Long id;

    /**
     * The {@link Bucket} where the storage item is located
     */
    @NotNull
    @ManyToOne
    private Bucket bucket;

    /**
     * The {@link StorageFolder} where the storage item is located
     */
    @ManyToOne(cascade = CascadeType.ALL)
    private StorageFolder parent = null;

    /**
     * The path of the storage item
     */
    @NotNull
    private String path = "/";

    /**
     * The name of the storage item
     */
    @NotNull
    private String name;

    /**
     * The {@link User} who created the storage item
     */
    @NotNull
    @ManyToOne
    private User creator;


    /**
     * Instantiates a new Storage item.
     */
    protected StorageItem() {

    }

    /**
     * Instantiates a new Storage item.
     *
     * @param originalName the original name of the storage item
     * @param creator the {@link User} who created the storage item
     */
    public StorageItem(String originalName, User creator) {
        this.name = originalName;
        this.creator = creator;
        this.path = "/" + originalName;
    }

    /**
     * Getter for the id of the storage item
     * @return the id of the storage item
     */
    public Long getId() {
        return id;
    }

    /**
     * Setter for the id of the storage item
     * @param id the new id of the storage item
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Getter for the path of the storage item
     * @return the path of the storage item
     */
    public String getPath() {
        return path;
    }

    /**
     * Setter for the path of the storage item
     * @param path the new path of the storage item
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Getter for the originalName of the storage item
     * @return the originalName of the storage item
     */
    public String getOriginalName() {
        return name;
    }

    /**
     * Setter for the originalName of the storage item
     * @param originalName the new originalName of the storage item
     */
    public void setName(String originalName) {
        this.name = originalName;
    }

    /**
     * Getter for the {@link User} who created the storage item
     * @return the {@link User} who created the storage item
     */
    public User getCreator() {
        return creator;
    }


    /**
     * Setter for the {@link User} who created the storage item
     * @param creator the new {@link User} who created the storage item
     */
    public void setCreator(User creator) {
        this.creator = creator;
    }

    /**
     * Getter for the {@link Bucket} where the storage item is located
     * @return the {@link Bucket} where the storage item is located
     */
    public Bucket getBucket() {
        return bucket;
    }

    /**
     * Setter for the {@link Bucket} where the storage item is located
     * @param bucket the new {@link Bucket} where the storage item is located
     */
    public void setBucket(Bucket bucket) {
        this.bucket = bucket;
    }

    /**
     * Getter for the {@link StorageFolder} where the storage item is located
     * @return the {@link StorageFolder} where the storage item is located
     */
    public StorageFolder getParent() {
        return parent;
    }

    /**
     * Setter for the {@link StorageFolder} where the storage item is located
     * @param parent the new {@link StorageFolder} where the storage item is located
     */
    public void setParent(StorageFolder parent) {
        this.path = (parent != null ? parent.getPath() : "") + "/" + name;
        this.parent = parent;
    }
}
