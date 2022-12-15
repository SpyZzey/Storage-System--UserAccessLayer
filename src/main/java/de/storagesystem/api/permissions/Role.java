package de.storagesystem.api.permissions;

import javax.persistence.*;
import java.util.List;

/**
 * @author Simon Brebeck
 */
@Entity
@Table(name = "roles")
public class Role {

    /**
     * The id of the role
     */
    @Id
    @SequenceGenerator(
            name = "role_sequence",
            sequenceName = "role_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "role_sequence"
    )
    private Long id;

    /**
     * The list of {@link Privilege}s that are assigned to this role.
     * Which can be added with {@link #addPrivilege(Privilege)} and removed with {@link #removePrivilege(Privilege)}.
     *
     * @see Privilege
     * @see Role#addPrivilege(Privilege)
     */
    @ManyToMany
    private List<Privilege> privileges;

    /**
     * Setter for the id of the role
     * @param id the id of the role
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Getter for the id of the role
     * @return the id of the role
     */
    public Long getId() {
        return id;
    }

    /**
     * Getter for the list of privileges that are assigned to this role.
     * @return the list of privileges that are assigned to this role.
     */
    public List<Privilege> getPrivileges() {
        return privileges;
    }

    /**
     * Setter for the list of privileges that are assigned to this role.
     * @param privileges the list of privileges that are assigned to this role.
     */
    public void setPrivileges(List<Privilege> privileges) {
        this.privileges = privileges;
    }

    /**
     * Adds a privilege to the list of privileges that are assigned to this role.
     * @param privilege the privilege to add to the list of privileges that are assigned to this role.
     */
    public void addPrivilege(Privilege privilege) {
        privileges.add(privilege);
    }

    /**
     * Removes a privilege from the list of privileges that are assigned to this role.
     * @param privilege the privilege to remove from the list of privileges that are assigned to this role.
     */
    public void removePrivilege(Privilege privilege) {
        privileges.remove(privilege);
    }

}
