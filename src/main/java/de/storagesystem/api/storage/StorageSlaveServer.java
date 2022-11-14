package de.storagesystem.api.storage;

import com.sun.istack.NotNull;

import javax.persistence.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"host", "port"}))
public class StorageSlaveServer {
    @Id
    @SequenceGenerator(
            name = "file_sequence",
            sequenceName = "file_sequence",
            allocationSize = 1)
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "file_sequence")
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String host;

    @NotNull
    private int port;

    @NotNull
    private Long freeStorage;

    @NotNull
    private Long totalStorage;

    public StorageSlaveServer() {

    }
    public StorageSlaveServer(String name, String host, int port, Long freeStorage, Long totalStorage) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.freeStorage = freeStorage;
        this.totalStorage = totalStorage;
    }

    public Long id() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String host() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int port() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Long freeStorage() {
        return freeStorage;
    }

    public void setFreeStorage(Long freeStorage) {
        this.freeStorage = freeStorage;
    }

    public Long totalStorage() {
        return totalStorage;
    }

    public void setTotalStorage(Long totalStorage) {
        this.totalStorage = totalStorage;
    }

}
