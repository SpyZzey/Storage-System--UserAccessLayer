package de.storagesystem.api.users;

import com.sun.istack.NotNull;
import de.storagesystem.api.buckets.users.BucketUser;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.persistence.*;
import java.security.NoSuchAlgorithmException;

@Entity
@Table
public class User {

    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1)
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence")
    private long id;

    @NotNull
    private String firstname;
    @NotNull
    private String lastname;
    @NotNull
    private String email;
    @NotNull
    private SecretKey secretKey;

    @Transient
    private BucketUser bucketUser;

    public User() throws NoSuchAlgorithmException {
        this.secretKey = KeyGenerator.getInstance("AES").generateKey();
    }

    public long id() {
        return id;
    }

    public String firstname() {
        return firstname;
    }

    public String lastname() {
        return lastname;
    }

    public String email() {
        return email;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setFirstName(String firstname) {
        this.firstname = firstname;
    }

    public void setLastName(String lastname) {
        this.lastname = lastname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public SecretKey secretKey() {
        return secretKey;
    }

    public void setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public BucketUser bucketUser() {
        if(bucketUser == null) {
            bucketUser = new BucketUser(firstname, lastname,  email);
        }
        return bucketUser;
    }

    public void update(User user) {
        this.firstname = user.firstname();
        this.lastname = user.lastname();
        this.email = user.email();
    }

}
