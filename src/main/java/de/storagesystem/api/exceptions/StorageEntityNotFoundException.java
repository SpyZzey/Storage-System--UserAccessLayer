package de.storagesystem.api.exceptions;

public class StorageEntityNotFoundException extends RuntimeException {

    private String entityName;

    public StorageEntityNotFoundException(String message) {
        super(message);
    }
    public StorageEntityNotFoundException(String message, String entityName) {
        super(message);
        this.entityName = entityName;
    }

    public String entityName() {
        return entityName;
    }
}
