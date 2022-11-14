package de.storagesystem.api.exceptions;

public class StorageEntityAlreadyExistsException extends RuntimeException {
    public StorageEntityAlreadyExistsException(String message) {
        super(message);
    }
}
