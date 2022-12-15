package de.storagesystem.api.users;

import com.fasterxml.jackson.databind.JsonSerializer;
import org.springframework.boot.jackson.JsonComponent;

/**
 * @author Simon Brebeck on 15.12.2022
 */
@JsonComponent
public class UserSerializer extends JsonSerializer<User> {
    @Override
    public void serialize(User user, com.fasterxml.jackson.core.JsonGenerator jsonGenerator, com.fasterxml.jackson.databind.SerializerProvider serializerProvider) throws java.io.IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", user.getId());
        jsonGenerator.writeStringField("firstname", user.getFirstname());
        jsonGenerator.writeStringField("lastname", user.getLastname());
        jsonGenerator.writePOJOField("email", user.getEmail());
        jsonGenerator.writeEndObject();
    }
}