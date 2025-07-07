package de.storagesystem.api.users;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

/**
 * @author Simon Brebeck on 15.12.2022
 */
@JsonComponent
public class UserSerializer extends JsonSerializer<User> {

    /**
     * Serializes the given user to a json object.
     *
     * @param user the user to serialize
     * @param jsonGenerator the json generator
     * @param serializerProvider the serializer provider
     * @throws java.io.IOException if an error occurs
     */
    @Override
    public void serialize(
            User user,
            JsonGenerator jsonGenerator,
            SerializerProvider serializerProvider) throws java.io.IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", user.getId());
        jsonGenerator.writeStringField("firstname", user.getFirstname());
        jsonGenerator.writeStringField("lastname", user.getLastname());
        jsonGenerator.writePOJOField("email", user.getEmail());
        jsonGenerator.writeEndObject();
    }
}