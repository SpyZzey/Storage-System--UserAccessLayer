package de.storagesystem.api.storage.servers;

import com.sun.istack.NotNull;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.Map;

/**
 * @author Simon Brebeck
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"host", "port"}))
public class StorageServer {

    /**
     * The id of the storage server
     */
    @Id
    @SequenceGenerator(
            name = "file_sequence",
            sequenceName = "file_sequence",
            allocationSize = 1)
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "file_sequence")
    private Long id;

    /**
     * The name of the storage server
     */
    @NotNull
    private String name;

    /**
     * The host of the storage server
     */
    @NotNull
    private String host;

    /**
     * The port of the storage server
     */
    @NotNull
    private int port;

    /**
     * The amount of free storage space on the storage server
     */
    @NotNull
    private Long freeStorage;

    /**
     * The amount of total amount of storage space on the storage server
     */
    @NotNull
    private Long totalStorage;

    /**
     * The online status of the storage server
     */
    @NotNull
    private boolean online;

    /**
     * Instantiates a new Storage server.
     */
    public StorageServer() {

    }

    /**
     * Instantiates a new Storage server.
     * @param name the name of the storage server
     * @param host the host of the storage server
     * @param port the port of the storage server
     * @param freeStorage the amount of free storage space on the storage server
     * @param totalStorage the amount of total amount of storage space on the storage server
     */
    public StorageServer(String name, String host, int port, Long freeStorage, Long totalStorage) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.freeStorage = freeStorage;
        this.totalStorage = totalStorage;
    }

    /**
     * Getter for the id of the storage server
     * @return the id of the storage server
     */
    public Long id() {
        return id;
    }

    /**
     * Setter for the id of the storage server
     * @param id the id of the storage server
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Getter for the name of the storage server
     * @return the name of the storage server
     */
    public String name() {
        return name;
    }

    /**
     * Setter for the name of the storage server
     * @param name the name of the storage server
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the host of the storage server
     * @return the host of the storage server
     */
    public String host() {
        return host;
    }

    /**
     * Setter for the host of the storage server
     * @param host the host of the storage server
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Getter for the port of the storage server
     * @return the port of the storage server
     */
    public int port() {
        return port;
    }

    /**
     * Setter for the port of the storage server
     * @param port the port of the storage server
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Getter for the amount of free storage space on the storage server
     * @return the amount of free storage space on the storage server
     */
    public Long freeStorage() {
        return freeStorage;
    }

    /**
     * Setter for the amount of free storage space on the storage server
     * @param freeStorage the amount of free storage space on the storage server
     */
    public void setFreeStorage(Long freeStorage) {
        this.freeStorage = freeStorage;
    }

    /**
     * Getter for the amount of total amount of storage space on the storage server
     * @return the amount of total amount of storage space on the storage server
     */
    public Long totalStorage() {
        return totalStorage;
    }

    /**
     * Setter for the amount of total amount of storage space on the storage server
     * @param totalStorage the amount of total amount of storage space on the storage server
     */
    public void setTotalStorage(Long totalStorage) {
        this.totalStorage = totalStorage;
    }

    public boolean online() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
