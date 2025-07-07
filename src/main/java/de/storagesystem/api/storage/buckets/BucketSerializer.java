package de.storagesystem.api.storage.buckets;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import de.storagesystem.api.storage.files.StorageFile;
import org.springframework.boot.jackson.JsonComponent;

/**
 * @author Simon Brebeck on 14.12.2022
 */
@JsonComponent
public class BucketSerializer extends JsonSerializer<Bucket> {

    /**
     * Serializes the given bucket to a json object.
     *
     * @param bucket the bucket to serialize
     * @param jsonGenerator the json generator
     * @param serializerProvider the serializer provider
     * @throws java.io.IOException if an error occurs
     */
    @Override
    public void serialize(
            Bucket bucket,
            JsonGenerator jsonGenerator,
            SerializerProvider serializerProvider) throws java.io.IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", bucket.getId());
        jsonGenerator.writeStringField("name", bucket.getName());
        jsonGenerator.writeNumberField("folders", bucket.getRootFolder().getFolders().size());
        jsonGenerator.writeNumberField("files", bucket.getRootFolder().getFiles().size());
        jsonGenerator.writeNumberField("users", bucket.getUsers().size());
        jsonGenerator.writeEndObject();
    }
}