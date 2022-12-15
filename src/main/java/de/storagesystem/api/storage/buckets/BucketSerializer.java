package de.storagesystem.api.storage.buckets;

import com.fasterxml.jackson.databind.JsonSerializer;
import org.springframework.boot.jackson.JsonComponent;

/**
 * @author Simon Brebeck on 14.12.2022
 */
@JsonComponent
public class BucketSerializer extends JsonSerializer<Bucket> {
    @Override
    public void serialize(Bucket bucket, com.fasterxml.jackson.core.JsonGenerator jsonGenerator, com.fasterxml.jackson.databind.SerializerProvider serializerProvider) throws java.io.IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", bucket.getId());
        jsonGenerator.writeStringField("name", bucket.getName());
        jsonGenerator.writeNumberField("folders", bucket.getRootFolder().getFolders().size());
        jsonGenerator.writeNumberField("files", bucket.getRootFolder().getFiles().size());
        jsonGenerator.writeNumberField("users", bucket.getUsers().size());
        jsonGenerator.writeEndObject();
    }
}