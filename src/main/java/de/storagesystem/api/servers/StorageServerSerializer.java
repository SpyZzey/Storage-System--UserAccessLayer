package de.storagesystem.api.servers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import de.storagesystem.api.storage.buckets.Bucket;
import org.springframework.boot.jackson.JsonComponent;

/**
 * @author Simon Brebeck on 16.12.2022
 */
@JsonComponent
public class StorageServerSerializer extends JsonSerializer<StorageServer> {

    /**
     * Serializes the given server to a json object.
     *
     * @param server the server to serialize
     * @param jsonGenerator the json generator
     * @param serializerProvider the serializer provider
     * @throws java.io.IOException if an error occurs
     */
    @Override
    public void serialize(
            StorageServer server,
            JsonGenerator jsonGenerator,
            SerializerProvider serializerProvider) throws java.io.IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", server.getId());
        jsonGenerator.writeStringField("name", server.getName());
        jsonGenerator.writeStringField("host", server.getHost());
        jsonGenerator.writeNumberField("port", server.getPort());
        jsonGenerator.writeNumberField("freeStorage", server.getFreeStorage());
        jsonGenerator.writeNumberField("totalStorage", server.getTotalStorage());
        jsonGenerator.writeEndObject();
    }
}