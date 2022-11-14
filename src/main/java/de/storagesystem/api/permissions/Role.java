package de.storagesystem.api.permissions;

import javax.persistence.*;
import java.util.List;

@Entity
public class Role {
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

    @ManyToMany
    private List<Privilege> privileges;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public List<Privilege> privileges() {
        return privileges;
    }

    public void setPrivileges(List<Privilege> privileges) {
        this.privileges = privileges;
    }

    public void addPrivilege(Privilege privilege) {
        privileges.add(privilege);
    }

    public void removePrivilege(Privilege privilege) {
        privileges.remove(privilege);
    }

}
