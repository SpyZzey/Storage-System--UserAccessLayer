package de.storagesystem.api.storage.servers;

import de.storagesystem.api.exceptions.InvalidTokenException;
import de.storagesystem.api.exceptions.StorageEntityNotFoundException;
import de.storagesystem.api.exceptions.UserInputValidationException;
import de.storagesystem.api.exceptions.UserNotFoundException;
import de.storagesystem.api.storage.StorageFileService;
import de.storagesystem.api.storage.StorageInputValidation;
import de.storagesystem.api.storage.StorageInputValidationImpl;
import de.storagesystem.api.users.UserService;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Path;
import java.util.Map;

/**
 * @author Simon Brebeck on 19.11.2022
 */
@Controller
@RequestMapping("/api/internal/")
public class StorageServerController {
    /**
     * The {@link Logger} for this class
     */
    private static final Logger logger = LogManager.getLogger(StorageServerController.class);

    /**
     * The {@link StorageFileService} to access the storage files
     */
    private final StorageFileService storageService;

    /**
     * The {@link UserService} to access the user data
     */
    private final UserService userService;

    /**
     * Instantiates a new Storage server controller.
     */
    public StorageServerController(
            @Qualifier("storageServerFileService") StorageFileService storageService,
            UserService userService) {
        this.storageService = storageService;
        this.userService = userService;
    }


    @GetMapping("/files/{bucket}/{folder}/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            @PathVariable String bucket,
            @PathVariable String folder,
            @PathVariable String filename)
            throws
            StorageEntityNotFoundException,
            UserNotFoundException,
            UserInputValidationException,
            InvalidTokenException {
        StorageInputValidation inputValidation = new StorageInputValidationImpl();
        if(!inputValidation.validateBucketName(bucket))
            throw new UserInputValidationException("Invalid bucket name");
        if(!inputValidation.validateFolderPath(folder))
            throw new UserInputValidationException("Invalid folder path");
        if(!inputValidation.validateFileName(filename))
            throw new UserInputValidationException("Invalid file name");

        logger.info("Download file " + filename + " from bucket " + bucket + " in path " + folder);
        ByteArrayResource file = storageService.loadAsResourcebyPath(
                authentication,
                bucket,
                folder,
                filename);

        logger.info("Sending file " + filename + " from bucket " + bucket + " in path " + folder);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentLength(file.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }

    /**
     * Uploads a file to a folder inside a bucket for a user
     *
     * @param authentication the authentication token of the user
     * @param bucket the bucket name where the file should be uploaded
     * @param folder the folder name where the file should be uploaded
     * @param file the file to upload
     * @return the response as a {@link Map}
     * @throws MaxUploadSizeExceededException if the file is too big
     * @throws StorageEntityNotFoundException if the bucket or folder does not exist
     * @throws UserNotFoundException if the user does not exist
     * @throws UserInputValidationException if the bucket, folder name is invalid
     * @throws InvalidTokenException if the authentication token is invalid
     */
    @PostMapping(value = "/files/", params = {"bucket", "folder"})
    public ResponseEntity<Map<String, String>> handleFileUploadByName(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            @RequestParam("bucket") String bucket,
            @RequestParam(value = "folder", required = false) String folder,
            @RequestParam("file") MultipartFile file)
            throws
            MaxUploadSizeExceededException,
            StorageEntityNotFoundException,
            UserNotFoundException,
            UserInputValidationException,
            InvalidTokenException {
        StorageInputValidation inputValidation = new StorageInputValidationImpl();
        if(!inputValidation.validateBucketName(bucket))
            throw new UserInputValidationException("Invalid bucket name");
        if(!inputValidation.validateFolderPath(folder))
            throw new UserInputValidationException("Invalid folder path");

        logger.info("Storing file " + file.getOriginalFilename() + " to bucket " + bucket + " in path " + folder);
        boolean stored = storageService.storeFile(authentication, bucket, folder, file);

        if(stored) {
            logger.info("File " + file.getOriginalFilename() + " stored in bucket " + bucket + " in path " + folder);
            return ResponseEntity.ok(Map.of(
                    "status", "ok",
                    "message", "File stored"));
        } else {
            logger.error("File " + file.getOriginalFilename() + " could not be stored in bucket " + bucket + " in path " + folder);
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message","File already exists"));
        }
    }
}
