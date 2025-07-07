package de.storagesystem.api.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.stream.Stream;

/**
 * @author Simon Brebeck on 12.12.2022
 */
public class ResponseBuilder {

    private final ObjectNode response;

    public ResponseBuilder() {
        ObjectMapper mapper = new ObjectMapper();
        response = mapper.createObjectNode();
    }

    public ObjectNode build(ResponseState state, String message) {
        response.put("status", state.getState());
        response.put("message", message);
        return response;
    }

    public ObjectNode build(ResponseState state) {
        response.put("status", state.getState());
        return response;
    }

    public ObjectNode build() {
        return response;
    }

    public ResponseBuilder setStatus(ResponseState state) {
        response.put("status", state.getState());
        return this;
    }

    public ResponseBuilder setMessage(String message) {
        response.put("message", message);
        return this;
    }

    public ResponseBuilder addArray(String key, Stream<?> stream) {
        ArrayNode array = response.putArray(key);
        stream.forEach(array::addPOJO);
        return this;
    }

    public ResponseBuilder addArray(String key, Iterable<?> elements) {
        ArrayNode array = response.putArray(key);
        elements.forEach(array::addPOJO);
        return this;
    }

    public ResponseBuilder addArray(String key, ArrayNode array) {
        response.set(key, array);
        return this;
    }

    public ResponseBuilder add(String key, Object value) {
        response.putPOJO(key, value);
        return this;
    }

    public ResponseBuilder add(String key, String value) {
        response.put(key, value);
        return this;
    }

    public ResponseBuilder add(String key, int value) {
        response.put(key, value);
        return this;
    }

    public ResponseBuilder add(String key, long value) {
        response.put(key, value);
        return this;
    }

    public ResponseBuilder add(String key, boolean value) {
        response.put(key, value);
        return this;
    }

    public ResponseBuilder add(String key, double value) {
        response.put(key, value);
        return this;
    }

    public ResponseBuilder add(String key, float value) {
        response.put(key, value);
        return this;
    }

    public ResponseBuilder add(String key, short value) {
        response.put(key, value);
        return this;
    }

    public ResponseBuilder add(String key, byte value) {
        response.put(key, value);
        return this;
    }

    public ResponseBuilder add(String key, char value) {
        response.put(key, value);
        return this;
    }

}
