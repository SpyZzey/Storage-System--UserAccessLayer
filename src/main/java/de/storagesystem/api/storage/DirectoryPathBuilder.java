package de.storagesystem.api.storage;

import de.storagesystem.api.exceptions.RootAlreadySetException;

import java.nio.file.Path;

public class DirectoryPathBuilder {

    private String root = null;
    private String path;

    public DirectoryPathBuilder() {

    }

    /**
     * Sets the root of the path.
     * @param path the root of the path
     * @return the builder
     * @throws RootAlreadySetException if the root is already set
     */
    public DirectoryPathBuilder setRoot(Path path) throws RootAlreadySetException {
        if(root != null) throw new RootAlreadySetException("Root already set");

        this.root = path.toAbsolutePath().toString();
        return this;
    }

    /**
     * Sets the root of the path.
     * @param name the name of the root
     * @return the builder
     */
    public DirectoryPathBuilder addDirectory(String name) {
        path += "/" + name;
        return this;
    }

    public Path build() {
        return Path.of(root + path);
    }

}
