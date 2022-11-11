package de.storagesystem.api.files;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    /**
     * Initializes the storage.
     */
    void init();

    /**
     * Stores a given key and file pair in the storage.
     * @param key the key to store the file under
     * @param file the file to store
     */
    void store(String key, MultipartFile file);

    /**
     * Loads all files from a user.
     * @return Stream of paths to the files
     */
    Stream<Path> loadAll();

    /**
     * Loads a file from a user.
     * @param filename the name of the file to load
     * @return the path to the file
     */
    Path load(String filename);

    /**
     * Loads a file from a user as a resource.
     * @param filename the name of the file to load
     * @return the resource
     */
    Resource loadAsResource(String filename);

}
