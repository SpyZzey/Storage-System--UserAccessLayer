package de.storagesystem.api.buckets;

import com.sun.istack.NotNull;
import de.storagesystem.api.buckets.directories.BucketDirectory;
import de.storagesystem.api.buckets.users.BucketUser;

import javax.persistence.*;
import java.io.File;
import java.nio.file.Path;

@MappedSuperclass
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"bucket_id", "bucketDirectory_id", "originalName"})
})
public class BucketItem {

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

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    private BucketUser creator;

    @ManyToOne(cascade = CascadeType.ALL)
    private BucketDirectory parent = null;

    @NotNull
    private String path = "/";

    @NotNull
    private String name;

    protected BucketItem() {

    }
    public BucketItem(Bucket bucket, String originalName, BucketUser creator) {
        this.parent = null;
        this.name = originalName;
        this.bucket = bucket;
        this.creator = creator;
        this.path = Path.of(originalName).toString();
    }
    public BucketItem(Bucket bucket, BucketDirectory parent, String originalName, BucketUser creator) {
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


    public Bucket bucket() {
        return bucket;
    }

    public void setBucket(Bucket bucket) {
        this.bucket = bucket;
    }

    public BucketDirectory parent() {
        return parent;
    }

    public void setParent(BucketDirectory parent) {
        this.path = Path.of((parent != null ? parent.path() : "")).resolve(name).toString();
        this.parent = parent;
    }
}
