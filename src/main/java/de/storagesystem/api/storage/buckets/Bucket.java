package de.storagesystem.api.storage.buckets;

import com.sun.istack.NotNull;
import de.storagesystem.api.users.BucketUser;
import de.storagesystem.api.users.User;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"creator_id", "name"})
})
public class Bucket {
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

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    private User creator;

    @NotNull
    private String name;

    private String description;


    @OneToMany
    private List<BucketUser> users;

    protected Bucket() {

    }

    public Bucket(String name, User creator) {
        this.name = name;
        this.creator = creator;
        this.users = new ArrayList<>();
    }

    public Bucket(String name, String description, User creator) {
        this.name = name;
        this.description = description;
        this.creator = creator;
        users = new ArrayList<>();
    }

    public Bucket(Long id, String name, User creator) {
        this.id = id;
        this.name = name;
        this.creator = creator;
        this.users = new ArrayList<>();
    }

    public Bucket(Long id, String name, String description, User creator) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.creator = creator;
        users = new ArrayList<>();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String description() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User creator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public List<BucketUser> users() {
        return users;
    }

    public void setUsers(List<BucketUser> users) {
        this.users = users;
    }

    public void addUser(BucketUser bucketUser) {
        users.add(bucketUser);
    }

    public void removeUser(BucketUser bucketUser) {
        users.remove(bucketUser);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bucket buckets = (Bucket) o;
        return id.equals(buckets.id) && name.equals(buckets.name) && Objects.equals(description, buckets.description) && creator.equals(buckets.creator) && Objects.equals(users, buckets.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, creator, users);
    }
}
