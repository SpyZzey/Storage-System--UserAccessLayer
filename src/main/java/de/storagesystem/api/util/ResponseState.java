package de.storagesystem.api.util;

/**
 * @author Simon Brebeck on 12.12.2022
 */
public enum ResponseState {
    OK("ok"),
    ERROR("error");

    private final String state;

    ResponseState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}
