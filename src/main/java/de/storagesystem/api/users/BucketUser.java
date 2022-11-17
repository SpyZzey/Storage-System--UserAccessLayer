package de.storagesystem.api.users;

import com.sun.istack.NotNull;
import de.storagesystem.api.storage.buckets.Bucket;
import de.storagesystem.api.permissions.Privilege;
import de.storagesystem.api.permissions.Role;

import javax.persistence.*;
import java.util.List;

/**
 * @author Simon Brebeck
 */
@Entity
public class BucketUser {

    /**
     * The id of the user
     */
    @Id
    @SequenceGenerator(
            name = "bucket_user_sequence",
            sequenceName = "bucket_user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "bucket_user_sequence"
    )
    private Long id;

    /**
     * The {@link Bucket} where the user can operate on
     */
    @NotNull
    @ManyToOne
    private Bucket bucket;

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
     * The list of {@link Role}s of the user
     */
    @ManyToMany
    private List<Role> roles;

    /**
     * The list of {@link Privilege}s of the user
     */
    @ManyToMany
    private List<Privilege> privileges;

    /**
     * Instantiates a new {@link BucketUser}
     */
    protected BucketUser() {

    }

    /**
     * Instantiates a new {@link BucketUser}
     * @param firstname the first name of the user
     * @param lastname the last name of the user
     * @param email the email of the user
     */
    public BucketUser(String firstname, String lastname, String email) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }


    /**
     * Instantiates a new {@link BucketUser}
     * @param firstname the first name of the user
     * @param lastname the last name of the user
     * @param email the email of the user
     * @param roles the list of {@link Role}s of the user
     * @param privileges the list of {@link Privilege}s of the user
     */
    public BucketUser(String firstname, String lastname, String email, List<Role> roles, List<Privilege> privileges) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.roles = roles;
        this.privileges = privileges;
    }


    /**
     * Instantiates a new {@link BucketUser}
     * @param id the id of the user
     * @param firstname the first name of the user
     * @param lastname the last name of the user
     * @param email the email of the user
     */
    public BucketUser(Long id, String firstname, String lastname, String email) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

    /**
     * Instantiates a new {@link BucketUser}
     * @param id the id of the user
     * @param firstname the first name of the user
     * @param lastname the last name of the user
     * @param email the email of the user
     * @param roles the list of {@link Role}s of the user
     * @param privileges the list of {@link Privilege}s of the user
     */
    public BucketUser(Long id, String firstname, String lastname, String email, List<Role> roles, List<Privilege> privileges) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.roles = roles;
        this.privileges = privileges;
    }

    /**
     * Setter for the id of the user
     * @param id the id of the user
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Getter for the id of the user
     * @return the id of the user
     */
    public Long id() {
        return id;
    }

    /**
     * Setter for the first name of the user
     * @param firstname the first name of the user
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * Getter for the first name of the user
     * @return the first name of the user
     */
    public String firstname() {
        return firstname;
    }


    /**
     * Getter for the last name of the user
     * @return the last name of the user
     */
    public String lastname() {
        return lastname;
    }

    /**
     * Setter for the last name of the user
     * @param lastname the last name of the user
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     * Getter for the email of the user
     * @return the email of the user
     */
    public String email() {
        return email;
    }

    /**
     * Setter for the email of the user
     * @param email the email of the user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Getter for the {@link Bucket} where the user can operate on
     * @return the {@link Bucket} where the user can operate on
     */
    public Bucket bucket() {
        return bucket;
    }

    /**
     * Setter for the {@link Bucket} where the user can operate on
     * @param bucket the {@link Bucket} where the user can operate on
     */
    public void setBucket(Bucket bucket) {
        this.bucket = bucket;
    }

    /**
     * Setter for the list of {@link Privilege}s of the user
     * @param privileges the list of {@link Privilege}s of the user
     */
    public void setPrivileges(List<Privilege> privileges) {
        this.privileges = privileges;
    }


    /**
     * Setter for the list of {@link Role}s of the user
     * @param roles the list of {@link Role}s of the user
     */
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    /**
     * Getter for the list of {@link Role}s of the user
     * @return the list of {@link Role}s of the user
     */
    public List<Role> roles() {
        return roles;
    }

    /**
     * Getter for the list of {@link Privilege}s of the user
     * @return the list of {@link Privilege}s of the user
     */
    public List<Privilege> privileges() {
        return privileges;
    }

    /**
     * Adds a {@link Privilege} to the list of {@link Privilege}s of the user
     * @param privilege the {@link Privilege} to add
     */
    public void addPrivilege(Privilege privilege) {
        privileges.add(privilege);
    }

    /**
     * Adds a {@link Role} to the list of {@link Role}s of the user
     * @param role the {@link Role} to add
     */
    public void addRole(Role role) {
        roles.add(role);
    }

    /**
     * Removes a {@link Privilege} from the list of {@link Privilege}s of the user
     * @param privilege the {@link Privilege} to remove
     */
    public void removePrivilege(Privilege privilege) {
        privileges.remove(privilege);
    }

    /**
     * Removes a {@link Role} from the list of {@link Role}s of the user
     * @param role the {@link Role} to remove
     */
    public void removeRole(Role role) {
        roles.remove(role);
    }

}
