package de.storagesystem.api.storage.buckets;

import com.sun.istack.NotNull;
import de.storagesystem.api.storage.folders.StorageFolder;
import de.storagesystem.api.users.User;

import javax.persistence.*;
import java.util.*;

/**
 * @author Simon Brebeck
 */
@Entity
@Table(
        name = "buckets",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"creator_id", "name"})})
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
    @ManyToOne
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
     * The list of {@link User}s that can access the bucket.
     * Which can be added with {@link #addUser(User)} and removed with {@link #removeUser(User)}.
     *
     * @see User
     * @see Bucket#addUser(User)
     */
    @ManyToMany
    private List<User> users;

    @OneToOne(cascade = CascadeType.ALL)
    private StorageFolder rootFolder;

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
        this.rootFolder = new StorageFolder(name, creator);
        this.rootFolder.setBucket(this);
    }

    /**
     * Instantiates a new bucket.
     * @param name the name of the bucket
     * @param description the description of the bucket
     * @param creator the {@link User} that created the bucket
     * @see User
     */
    public Bucket(String name, String description, User creator) {
        this(name, creator);
        this.description = description;
    }

    /**
     * Instantiates a new bucket.
     * @param id the id of the bucket
     * @param name the name of the bucket
     * @param creator the {@link User} that created the bucket
     * @see User
     */
    public Bucket(Long id, String name, User creator) {
        this(id, name, null, creator);
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
        this(name, description, creator);
        this.id = id;
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
    public Long getId() {
        return id;
    }


    /**
     * Getter for the name of the bucket
     * @return String the name of the bucket
     */
    public String getName() {
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
    public String getDescription() {
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
    public User getCreator() {
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
     * Getter for the list of {@link User}s that can access the bucket.
     * @return List the list of {@link User}s that can access the bucket.
     * @see User
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * Setter for the list of {@link User}s that can access the bucket.
     * @param users the list of {@link User}s that can access the bucket.
     */
    public void setUsers(List<User> users) {
        this.users = users;
    }

    /**
     * Adds a {@link User} to the list of {@link User}s that can access the bucket.
     * @param user the {@link User} that should be added
     *                  to the list of {@link User}s that can access the bucket.
     * @see User
     */
    public void addUser(User user) {
        users.add(user);
    }

    /**
     * Removes a {@link User} from the list of {@link User}s that can access the bucket.
     * @param user the {@link User} that should be removed
     *                  from the list of {@link User}s that can access the bucket.
     * @see User
     */
    public void removeUser(User user) {
        users.remove(user);
    }

    public StorageFolder getRootFolder() {
        return rootFolder;
    }

    public void setRootFolder(StorageFolder rootFolder) {
        this.rootFolder = rootFolder;
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
