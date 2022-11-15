package de.storagesystem.api.users;

import com.sun.istack.NotNull;
import de.storagesystem.api.storage.buckets.Bucket;
import de.storagesystem.api.permissions.Privilege;
import de.storagesystem.api.permissions.Role;

import javax.persistence.*;
import java.util.List;

@Entity
public class BucketUser {
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

    @NotNull
    @ManyToOne
    private Bucket bucket;

    @NotNull
    private String firstname;

    @NotNull
    private String lastname;

    @NotNull
    private String email;

    @ManyToMany
    private List<Role> roles;

    @ManyToMany
    private List<Privilege> privileges;

    protected BucketUser() {

    }

    public BucketUser(String firstname, String lastname, String email) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

    public BucketUser(String firstname, String lastname, String email, List<Role> roles, List<Privilege> privileges) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.roles = roles;
        this.privileges = privileges;
    }

    public BucketUser(Long id, String firstname, String lastname, String email) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

    public BucketUser(Long id, String firstname, String lastname, String email, List<Role> roles, List<Privilege> privileges) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.roles = roles;
        this.privileges = privileges;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long id() {
        return id;
    }

    public void setFirstname(String name) {
        this.firstname = name;
    }

    public String firstname() {
        return firstname;
    }

    public String lastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String email() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Bucket bucket() {
        return bucket;
    }

    public void setBucket(Bucket bucket) {
        this.bucket = bucket;
    }

    public void setPrivileges(List<Privilege> privileges) {
        this.privileges = privileges;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<Role> roles() {
        return roles;
    }

    public List<Privilege> privileges() {
        return privileges;
    }

    public void addPrivilege(Privilege privilege) {
        privileges.add(privilege);
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public void removePrivilege(Privilege privilege) {
        privileges.remove(privilege);
    }

    public void removeRole(Role role) {
        roles.remove(role);
    }

}
