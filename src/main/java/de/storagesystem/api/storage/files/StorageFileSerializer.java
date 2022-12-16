package de.storagesystem.api.storage.files;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import de.storagesystem.api.storage.folders.StorageFolder;
import org.springframework.boot.jackson.JsonComponent;

/**
 * @author Simon Brebeck on 15.12.2022
 */
@JsonComponent
public class StorageFileSerializer extends JsonSerializer<StorageFile> {

    /**
     * Serializes the given file to a json object.
     *
     * @param file the file to serialize
     * @param jsonGenerator the json generator
     * @param serializerProvider the serializer provider
     * @throws java.io.IOException if an error occurs
     */
    @Override
    public void serialize(
            StorageFile file,
            JsonGenerator jsonGenerator,
            SerializerProvider serializerProvider) throws java.io.IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", file.getId());
        jsonGenerator.writeStringField("name", file.getOriginalName());
        jsonGenerator.writeStringField("path", file.getPath());
        jsonGenerator.writeStringField("file_type", file.getFileType());
        jsonGenerator.writeNumberField("size", file.getSize());
        jsonGenerator.writeEndObject();
    }

}