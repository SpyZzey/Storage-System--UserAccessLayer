package de.storagesystem.api.exceptions;

public class RootAlreadySetException extends Exception {
    public RootAlreadySetException() {
        super("Root already set");
    }

    public RootAlreadySetException(String message) {
        super(message);
    }

}
