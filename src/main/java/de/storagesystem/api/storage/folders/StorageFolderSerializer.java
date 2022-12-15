package de.storagesystem.api.storage.folders;

import com.fasterxml.jackson.databind.JsonSerializer;
import org.springframework.boot.jackson.JsonComponent;

/**
 * @author Simon Brebeck on 15.12.2022
 */
@JsonComponent
public class StorageFolderSerializer extends JsonSerializer<StorageFolder> {
    @Override
    public void serialize(StorageFolder folder, com.fasterxml.jackson.core.JsonGenerator jsonGenerator, com.fasterxml.jackson.databind.SerializerProvider serializerProvider) throws java.io.IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", folder.getId());
        jsonGenerator.writeStringField("name", folder.getOriginalName());
        jsonGenerator.writeStringField("path", folder.getPath());
        jsonGenerator.writeNumberField("folders", folder.getFolders().size());
        jsonGenerator.writeNumberField("files", folder.getFiles().size());
        jsonGenerator.writeEndObject();
    }
}