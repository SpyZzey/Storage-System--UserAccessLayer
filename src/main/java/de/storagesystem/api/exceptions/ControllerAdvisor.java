package de.storagesystem.api.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(StorageEntityNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageEntityNotFoundException exc) {
        logger.info("Storage Entity was not found");
        return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "Entity was not found: " + exc.entityName()));
    }

    @ExceptionHandler(StorageEntityAlreadyExistsException.class)
    public ResponseEntity<?> handleStorageFileAlreadyExists(StorageEntityAlreadyExistsException exc) {
        logger.info("Bucket already exists");
        return ResponseEntity.badRequest().body("Bucket already exists");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleFileSizeLimitExceeded(MaxUploadSizeExceededException exc) {
        logger.info("File size limit exceeded. Max: " + exc.getMaxUploadSize());
        return ResponseEntity.badRequest().body("File size limit exceeded. Max: " + exc.getMaxUploadSize());
    }
}
