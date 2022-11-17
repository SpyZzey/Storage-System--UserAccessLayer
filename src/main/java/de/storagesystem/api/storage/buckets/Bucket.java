package de.storagesystem.api.storage.buckets;

import com.sun.istack.NotNull;
import de.storagesystem.api.users.BucketUser;
import de.storagesystem.api.users.User;

import javax.persistence.*;
import java.util.*;

/**
 * @author Simon Brebeck
 */
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"creator_id", "name"})
})
public class Bucket {

    /**
     * The id of the bucket
     */
    @Id
    @SequenceGenerator(
            name = "bucket_sequence",
            sequenceName = "bucket_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "bucket_sequence"
    )
    private Long id;

    /**
     * The {@link User} that created the bucket
     * @see User
     */
    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    private User creator;

    /**
     * The name of the bucket
     */
    @NotNull
    private String name;

    /**
     * The description of the bucket
     */
    private String description;


    /**
     * The list of {@link BucketUser}s that can access the bucket.
     * Which can be added with {@link #addBucketUser(BucketUser)} and removed with {@link #removeBucketUser(BucketUser)}.
     *
     * @see BucketUser
     * @see Bucket#addBucketUser(BucketUser)
     */
    @OneToMany
    private List<BucketUser> users;

    /**
     * The default constructor of the bucket to create a new bucket
     */
    protected Bucket() {

    }

    /**
     * Instantiates a new bucket.
     * @param name the name of the bucket
     * @param creator the {@link User} that created the bucket
     * @see User
     */
    public Bucket(String name, User creator) {
        this.name = name;
        this.creator = creator;
        this.users = new ArrayList<>();
    }

    /**
     * Instantiates a new bucket.
     * @param name the name of the bucket
     * @param description the description of the bucket
     * @param creator the {@link User} that created the bucket
     * @see User
     */
    public Bucket(String name, String description, User creator) {
        this.name = name;
        this.description = description;
        this.creator = creator;
        users = new ArrayList<>();
    }

    /**
     * Instantiates a new bucket.
     * @param id the id of the bucket
     * @param name the name of the bucket
     * @param creator the {@link User} that created the bucket
     * @see User
     */
    public Bucket(Long id, String name, User creator) {
        this.id = id;
        this.name = name;
        this.creator = creator;
        this.users = new ArrayList<>();
    }

    /**
     * Instantiates a new bucket.
     * @param id the id of the bucket
     * @param name the name of the bucket
     * @param description the description of the bucket
     * @param creator the {@link User} that created the bucket
     * @see User
     */
    public Bucket(Long id, String name, String description, User creator) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.creator = creator;
        users = new ArrayList<>();
    }

    /**
     * Setter for the id of the bucket
     * @param id the id of the bucket
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Getter for the id of the bucket
     * @return Long the id of the bucket
     */
    public Long id() {
        return id;
    }


    /**
     * Getter for the name of the bucket
     * @return String the name of the bucket
     */
    public String name() {
        return name;
    }

    /**
     * Setter for the name of the bucket
     * @param name the name of the bucket
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the description of the bucket
     * @return String the description of the bucket
     */
    public String description() {
        return description;
    }

    /**
     * Setter for the description of the bucket
     * @param description the description of the bucket
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter for the {@link User} that created the bucket
     * @return User the {@link User} that created the bucket
     * @see User
     */
    public User creator() {
        return creator;
    }

    /**
     * Setter for the {@link User} that created the bucket
     * @param creator the {@link User} that created the bucket
     * @see User
     */
    public void setCreator(User creator) {
        this.creator = creator;
    }

    /**
     * Getter for the list of {@link BucketUser}s that can access the bucket.
     * @return List the list of {@link BucketUser}s that can access the bucket.
     * @see BucketUser
     */
    public List<BucketUser> users() {
        return users;
    }

    /**
     * Setter for the list of {@link BucketUser}s that can access the bucket.
     * @param users the list of {@link BucketUser}s that can access the bucket.
     */
    public void setBucketUsers(List<BucketUser> users) {
        this.users = users;
    }

    /**
     * Adds a {@link BucketUser} to the list of {@link BucketUser}s that can access the bucket.
     * @param bucketUser the {@link BucketUser} that should be added
     *                  to the list of {@link BucketUser}s that can access the bucket.
     * @see BucketUser
     */
    public void addBucketUser(BucketUser bucketUser) {
        users.add(bucketUser);
    }

    /**
     * Removes a {@link BucketUser} from the list of {@link BucketUser}s that can access the bucket.
     * @param bucketUser the {@link BucketUser} that should be removed
     *                  from the list of {@link BucketUser}s that can access the bucket.
     * @see BucketUser
     */
    public void removeBucketUser(BucketUser bucketUser) {
        users.remove(bucketUser);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bucket buckets = (Bucket) o;
        return id.equals(buckets.id) && name.equals(buckets.name) && Objects.equals(description, buckets.description) && creator.equals(buckets.creator) && Objects.equals(users, buckets.users);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, creator, users);
    }
}
