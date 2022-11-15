package de.storagesystem.api.storage;

import com.sun.istack.NotNull;
import de.storagesystem.api.storage.buckets.Bucket;
import de.storagesystem.api.storage.folders.StorageFolder;
import de.storagesystem.api.users.BucketUser;

import javax.persistence.*;
import java.io.File;
import java.nio.file.Path;

@MappedSuperclass
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"bucket_id", "parent_id", "name"})
})
public class StorageItem {

    @Id
    @SequenceGenerator(
            name = "file_sequence",
            sequenceName = "file_sequence",
            allocationSize = 1)
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "file_sequence")
    private Long id;

    @NotNull
    @ManyToOne
    private Bucket bucket;

    @ManyToOne(cascade = CascadeType.ALL)
    private StorageFolder parent = null;

    @NotNull
    private String path = "/";

    @NotNull
    private String name;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    private BucketUser creator;

    protected StorageItem() {

    }
    public StorageItem(Bucket bucket, String originalName, BucketUser creator) {
        this.parent = null;
        this.name = originalName;
        this.bucket = bucket;
        this.creator = creator;
        this.path = Path.of(originalName).toString();
    }
    public StorageItem(Bucket bucket, StorageFolder parent, String originalName, BucketUser creator) {
        this.name = originalName;
        this.bucket = bucket;
        this.parent = parent;
        this.creator = creator;
        this.path = Path.of((parent != null ? parent.path() : File.separator)).resolve(originalName).toString();
    }

    public Long id() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String path() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String originalName() {
        return name;
    }

    public void setName(String originalName) {
        this.name = originalName;
    }

    public BucketUser creator() {
        return creator;
    }

    public void setCreator(BucketUser creator) {
        this.creator = creator;
    }

    public Bucket bucket() {
        return bucket;
    }

    public void setBucket(Bucket bucket) {
        this.bucket = bucket;
    }

    public StorageFolder parent() {
        return parent;
    }

    public void setParent(StorageFolder parent) {
        this.path = Path.of((parent != null ? parent.path() : "")).resolve(name).toString();
        this.parent = parent;
    }
}
