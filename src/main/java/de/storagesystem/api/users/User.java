package de.storagesystem.api.users;

import com.sun.istack.NotNull;

import javax.persistence.*;

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

    private String password;

    private boolean enabled;


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

    public void update(User user) {
        this.firstname = user.firstname();
        this.lastname = user.lastname();
        this.email = user.email();
    }

}
