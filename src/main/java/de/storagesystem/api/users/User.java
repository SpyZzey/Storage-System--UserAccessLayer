package de.storagesystem.api.users;

import com.sun.istack.NotNull;
import de.storagesystem.api.storage.buckets.Bucket;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.persistence.*;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;

/**
 * @author Simon Brebeck
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * The id of the user
     */
    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1)
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence")
    private long id;

    /**
     * The first name of the user
     */
    @NotNull
    private String firstname;

    /**
     * The last name of the user
     */
    @NotNull
    private String lastname;

    /**
     * The email of the user
     */
    @NotNull
    private String email;

    /**
     * The secret key of the user, used to encrypt the data
     */
    @NotNull
    private SecretKey secretKey;

    /**
     * The buckets of the user
     */
    @ManyToMany(mappedBy = "users", fetch = FetchType.LAZY)
    private List<Bucket> buckets;


    /**
     * Instantiates a new User.
     * @throws NoSuchAlgorithmException if AES is not supported to generate the secret key
     */
    public User() throws NoSuchAlgorithmException {
        this.secretKey = KeyGenerator.getInstance("AES").generateKey();
    }

    /**
     * Instantiates a new User.
     * @param firstname the first name of the user
     * @param lastname the last name of the user
     * @param email the email of the user
     * @throws NoSuchAlgorithmException if AES is not supported to generate the secret key
     */
    public User(String firstname, String lastname, String email) throws NoSuchAlgorithmException {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.secretKey = KeyGenerator.getInstance("AES").generateKey();
    }

    /**
     * Getter for the id of the user
     * @return the id of the user
     */
    public long getId() {
        return id;
    }

    /**
     * Getter for the first name of the user
     * @return the first name of the user
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * Getter for the last name of the user
     * @return the last name of the user
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * Getter for the email of the user
     * @return the email of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter for the id of the user
     * @param id the new id of the user
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Setter for the first name of the user
     * @param firstname the new first name of the user
     */
    public void setFirstName(String firstname) {
        this.firstname = firstname;
    }

    /**
     * Setter for the last name of the user
     * @param lastname the new last name of the user
     */
    public void setLastName(String lastname) {
        this.lastname = lastname;
    }

    /**
     * Setter for the email of the user
     * @param email the new email of the user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Getter for the secret key of the user
     * @return the secret key of the user
     */
    public SecretKey getSecretKey() {
        return secretKey;
    }

    /**
     * Setter for the secret key of the user
     * @param secretKey the new secret key of the user
     */
    public void setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * Updaes the user with the data of the given {@link User}
     * @param user the user to update the data from
     */
    public void update(User user) {
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.email = user.getEmail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && firstname.equals(user.firstname) && lastname.equals(user.lastname) && email.equals(user.email) && secretKey.equals(user.secretKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, firstname, lastname, email, secretKey);
    }
}
