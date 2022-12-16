package de.storagesystem.api.storage.folders;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import de.storagesystem.api.users.User;
import org.springframework.boot.jackson.JsonComponent;

/**
 * @author Simon Brebeck on 15.12.2022
 */
@JsonComponent
public class StorageFolderSerializer extends JsonSerializer<StorageFolder> {

    /**
     * Serializes the given folder to a json object.
     *
     * @param folder the folder to serialize
     * @param jsonGenerator the json generator
     * @param serializerProvider the serializer provider
     * @throws java.io.IOException if an error occurs
     */
    @Override
    public void serialize(
            StorageFolder folder,
            JsonGenerator jsonGenerator,
            SerializerProvider serializerProvider) throws java.io.IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", folder.getId());
        jsonGenerator.writeStringField("name", folder.getOriginalName());
        jsonGenerator.writeStringField("path", folder.getPath());
        jsonGenerator.writeNumberField("folders", folder.getFolders().size());
        jsonGenerator.writeNumberField("files", folder.getFiles().size());
        jsonGenerator.writeEndObject();
    }
}