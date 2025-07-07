package de.storagesystem.api.storage.folders;

import com.fasterxml.jackson.databind.node.ObjectNode;
import de.storagesystem.api.exceptions.*;
import de.storagesystem.api.storage.StorageInputValidation;
import de.storagesystem.api.storage.StorageInputValidationImpl;
import de.storagesystem.api.users.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Map;

/**
 * @author Simon Brebeck
 */
@Controller
@RequestMapping("/api/folders/")
public class StorageFolderController {

    /**
     * The {@link Logger} for this class
     */
    private static final Logger logger = LogManager.getLogger(StorageFolderController.class);

    /**
     * The {@link StorageFolderService} to accesess the storage folders
     */
    private final StorageFolderService storageService;

    /**
     * The {@link UserService} to access the users
     */
    private final UserService userService;

    /**
     * Instantiates a new Storage folder controller.
     * @param storageFolderService the storage service to access the storage folders
     * @param userService the user service to access the users
     */
    @Autowired
    public StorageFolderController(StorageFolderService storageFolderService, UserService userService) {
        this.storageService = storageFolderService;
        this.userService = userService;
    }

    /**
     * Creates a new folder in the given bucket under a given parent folder
     *
     * @param authentication the authentication token
     * @param bucket the bucket to create the folder in
     * @param relativePath the path to the parent folder to create the folder in
     * @param folder the name of the folder to create
     * @return the response as a {@link ResponseEntity<ObjectNode>}
     * @throws MaxUploadSizeExceededException if the upload size exceeds the maximum upload size
     * @throws UserInputValidationException if the folder path or bucket-/foldername is invalid
     * @throws InvalidTokenException if the token is invalid
     */
    @PostMapping(value = "/{bucket}/{relativePath}")
    public ResponseEntity<ObjectNode> handleDirectoryCreation(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            @PathVariable("bucket") String bucket,
            @PathVariable(value = "relativePath", required = false) String relativePath,
            @RequestParam("folder") String folder)
            throws
            StorageEntityNotFoundException,
            StorageEntityCreationException,
            UserNotFoundException,
            UserInputValidationException,
            InvalidTokenException {

        StorageInputValidation inputValidation = new StorageInputValidationImpl();
        if(!inputValidation.validateBucketName(bucket))
            throw new UserInputValidationException("Invalid bucket name: " + bucket);
        if(!inputValidation.validateFolderPath(relativePath))
            throw new UserInputValidationException("Invalid folder path: " + relativePath);
        if(!inputValidation.validateFolderName(folder))
            throw new UserInputValidationException("Invalid folder name: " + folder);

        String pathToParent = (relativePath == null) ? "/" + bucket : "/" + bucket + "/" + relativePath;
        logger.info("Creating folder " + folder + " in folder " + pathToParent);
        return storageService.createFolder(
                userService.getUserId(authentication),
                bucket,
                pathToParent,
                folder);
    }

    /**
     * Lists all folders inside a given folder
     * @param authentication the authentication token
     * @param bucketName the bucket to list the folders in
     * @param relativePath the path to the parent folder to list the folders in
     * @param page the page to list
     * @param limit the limit of folders to list
     * @return the response as a {@link ResponseEntity<ObjectNode>}
     * @throws StorageEntityNotFoundException if the bucket or parent folder does not exist
     * @throws StorageEntityCreationException if the folder could not be created
     * @throws UserNotFoundException if the user does not exist
     * @throws UserInputValidationException if the folder path or bucket-/foldername is invalid
     * @throws InvalidTokenException if the token is invalid
     */
    @GetMapping(value = "/{bucketName}/{relativePath}")
    public ResponseEntity<ObjectNode> handleFolderList(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            @PathVariable String bucketName,
            @PathVariable(value = "relativePath", required = false) String relativePath,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "100") int limit)
            throws
            StorageEntityNotFoundException,
            StorageEntityCreationException,
            UserNotFoundException,
            UserInputValidationException,
            InvalidTokenException {

        // Validate user input
        StorageInputValidation inputValidation = new StorageInputValidationImpl();
        if(!inputValidation.validateBucketName(bucketName))
            throw new UserInputValidationException("Invalid bucket name");
        if(page < 0 || limit < 0)
            throw new IllegalArgumentException("Page and limit must be greater than 0");
        if(limit > 100)
            throw new IllegalArgumentException("Cannot get more than 100 buckets at once");

        String pathToParent = (relativePath == null) ? "/" + bucketName : "/" + bucketName + "/" + relativePath;
        logger.info("Getting folder list of folder: " + pathToParent);
        return storageService.loadFolders(userService.getUserId(authentication), bucketName, pathToParent, page, limit);
    }

    /**
     * Creates a new folder in the given bucket
     *
     * @param authentication the authentication token
     * @param bucket the bucket to create the folder in
     * @param folder the name of the folder to create
     * @return the response as a {@link ResponseEntity<ObjectNode>}
     * @throws StorageEntityNotFoundException if the bucket or parent folder does not exist
     * @throws StorageEntityCreationException if the folder could not be created
     * @throws UserNotFoundException if the user does not exist
     * @throws UserInputValidationException if the folder path or bucket-/foldername is invalid
     * @throws InvalidTokenException if the token is invalid
     */
    @PostMapping(value = "/{bucket}")
    public ResponseEntity<ObjectNode> handleDirectoryCreation(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            @PathVariable("bucket") String bucket,
            @RequestParam("folder") String folder)
            throws
            StorageEntityNotFoundException,
            StorageEntityCreationException,
            UserNotFoundException,
            UserInputValidationException,
            InvalidTokenException {
        return handleDirectoryCreation(authentication, bucket, null, folder);
    }

     /**
     * Lists all folders inside a given folder
     * @param authentication the authentication token
     * @param bucketName the bucket to list the folders in
     * @param page the page to list
     * @param limit the limit of folders to list
     * @return the response as a {@link ResponseEntity<ObjectNode>}
     * @throws StorageEntityNotFoundException if the bucket or parent folder does not exist
     * @throws StorageEntityCreationException if the folder could not be created
     * @throws UserNotFoundException if the user does not exist
     * @throws UserInputValidationException if the folder path or bucket-/foldername is invalid
     * @throws InvalidTokenException if the token is invalid
     */
    @GetMapping(value = "/{bucketName}")
    public ResponseEntity<ObjectNode> handleFolderList(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            @PathVariable String bucketName,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "100") int limit)
            throws
            StorageEntityNotFoundException,
            StorageEntityCreationException,
            UserNotFoundException,
            UserInputValidationException,
            InvalidTokenException {
        return handleFolderList(authentication, bucketName, null, page, limit);
    }

}
