package de.storagesystem.api.permissions;


import com.sun.istack.NotNull;

import javax.persistence.*;

/**
 * @author Simon Brebeck
 */
@Entity
public class Privilege {

    /**
     * The id of the privilege
     */
    @Id
    @SequenceGenerator(
            name = "privilege_sequence",
            sequenceName = "privilege_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "privilege_sequence"
    )
    private Long id;

    /**
     * The name of the privilege
     */
    @NotNull
    private String name;

    /**
     * The description of the privilege
     */
    private String description;

    /**
     * Setter for the id of the privilege
     * @param id the id of the privilege
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Getter for the id of the privilege
     * @return the id of the privilege
     */
    public Long id() {
        return id;
    }

    /**
     * Setter for the name of the privilege
     * @param name the name of the privilege
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the name of the privilege
     * @return the name of the privilege
     */
    public String name() {
        return name;
    }

    /**
     * Setter for the description of the privilege
     * @param description the description of the privilege
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter for the description of the privilege
     * @return the description of the privilege
     */
    public String description() {
        return description;
    }

}
