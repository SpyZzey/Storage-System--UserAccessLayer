package de.storagesystem.api.storage.files;

import com.fasterxml.jackson.databind.node.ObjectNode;
import de.storagesystem.api.exceptions.*;
import de.storagesystem.api.storage.StorageInputValidation;
import de.storagesystem.api.storage.StorageInputValidationImpl;
import de.storagesystem.api.users.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Simon Brebeck
 */
@Controller
@RequestMapping("/api/files/")
public class StorageFileController {

    /**
     * The {@link Logger} for this class
     */
    private static final Logger logger = LogManager.getLogger(StorageFileController.class);

    /**
     * The {@link StorageFileService} to access the storage files
     */
    private final StorageFileService storageService;

    /**
     * The {@link UserService} that is used to access the users.
     */
    private final UserService userService;

    /**
     * Instantiates a new Storage file controller.
     * @param storageService the storage service to access the storage files
     */
    @Autowired
    public StorageFileController(
            UserService userService,
            @Qualifier("storageFileService") StorageFileService storageService) {
        this.userService = userService;
        this.storageService = storageService;
    }

    /**
     * Gets the file from a folder inside a bucket with a given name for a user
     *
     * @param authentication the authentication token of the user
     * @param bucket the bucket name where the file is located
     * @param path the path of the file
     * @return the file as a {@link ResponseEntity<Resource>}
     * @throws StorageEntityNotFoundException if the bucket, folder or file does not exist
     * @throws UserNotFoundException if the user does not exist
     * @throws UserInputValidationException if the bucket, folder or file name is invalid
     * @throws InvalidTokenException if the authentication token is invalid
     */
    @GetMapping("/{bucket}/{path}/download")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            @PathVariable String bucket,
            @PathVariable String path)
            throws
            StorageEntityNotFoundException,
            UserNotFoundException,
            UserInputValidationException,
            InvalidTokenException {
        StorageInputValidation inputValidation = new StorageInputValidationImpl();
        if(!inputValidation.validateBucketName(bucket))
            throw new UserInputValidationException("Invalid bucket name");
        if(!inputValidation.validateFilePath(path))
            throw new UserInputValidationException("Invalid file path");

        String pathToParent = (path == null) ? "/" + bucket : "/" + bucket + "/" + path;
        logger.info("Download file " + pathToParent + " from bucket " + bucket);
        return storageService.loadFile(userService.getUserId(authentication), bucket, pathToParent);
    }

    /**
     * Deletes a file from a folder inside a bucket with a given name for a user
     *
     * @param authentication the authentication token of the user
     * @param bucket the bucket name where the file is located
     * @param path the path of the file
     * @return the file as a {@link ResponseEntity<ObjectNode>}
     * @throws StorageEntityNotFoundException if the bucket, folder or file does not exist
     * @throws UserNotFoundException if the user does not exist
     * @throws UserInputValidationException if the bucket, folder or file name is invalid
     * @throws InvalidTokenException if the authentication token is invalid
     */
    @DeleteMapping("/{bucket}/{path}")
    @ResponseBody
    public ResponseEntity<ObjectNode> deleteFile(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            @PathVariable String bucket,
            @PathVariable String path)
            throws
            StorageEntityNotFoundException,
            UserNotFoundException,
            UserInputValidationException,
            InvalidTokenException {

        StorageInputValidation inputValidation = new StorageInputValidationImpl();
        if(!inputValidation.validateBucketName(bucket))
            throw new UserInputValidationException("Invalid bucket name");
        if(!inputValidation.validateFilePath(path))
            throw new UserInputValidationException("Invalid file path");

        String pathToParent = (path == null) ? "/" + bucket : "/" + bucket + "/" + path;
        logger.info("Delete file " + pathToParent + " from bucket " + bucket);
        return storageService.deleteFile(userService.getUserId(authentication), bucket, pathToParent);
    }

    /**
     * Uploads a file to a folder inside a bucket for a user
     *
     * @param authentication the authentication token of the user
     * @param bucket the bucket name where the file should be uploaded
     * @param folderPath the folder path where the file should be uploaded
     * @param file the file to upload
     * @return the response as a {@link ResponseEntity<ObjectNode>}
     * @throws MaxUploadSizeExceededException if the file is too big
     * @throws StorageEntityNotFoundException if the bucket or folder does not exist
     * @throws UserNotFoundException if the user does not exist
     * @throws UserInputValidationException if the bucket, folder name is invalid
     * @throws InvalidTokenException if the authentication token is invalid
     */
    @PostMapping("/{bucket}/{folderPath}")
    public ResponseEntity<ObjectNode> handleFileUploadByName(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            @PathVariable("bucket") String bucket,
            @PathVariable(value = "folderPath", required = false) String folderPath,
            @RequestParam("file") MultipartFile file)
            throws
            MaxUploadSizeExceededException,
            StorageEntityNotFoundException,
            StorageEntityCreationException,
            StorageEntityAlreadyExistsException,
            UserNotFoundException,
            UserInputValidationException,
            InvalidTokenException {
        // Validate user input
        StorageInputValidation inputValidation = new StorageInputValidationImpl();
        if(!inputValidation.validateBucketName(bucket)) // Check if the bucket name is valid
            throw new UserInputValidationException("Invalid bucket name");
        if(!inputValidation.validateFolderPath(folderPath)) // Check if the folder path is valid
            throw new UserInputValidationException("Invalid folder path");

        String pathToParent = (folderPath == null) ? "/" + bucket : "/" + bucket + "/" + folderPath;
        logger.info("Upload file " + file.getOriginalFilename() + " to bucket " + bucket + " in path " + pathToParent);
        return storageService.storeFile(userService.getUserId(authentication), bucket, pathToParent, file);
    }

    /**
     * Lists all files in a folder inside a bucket for a user
     *
     * @param authentication the authentication token of the user
     * @param bucketName the bucket name where the file is located
     * @param relativePath the folder path where the file is located
     * @param page the page number
     * @param limit the number of files to return
     * @return the list of files as a {@link ResponseEntity<ObjectNode>}
     * @throws StorageEntityNotFoundException if the bucket or folder does not exist
     * @throws StorageEntityCreationException if the folder could not be created
     * @throws StorageEntityAlreadyExistsException if the folder already exists
     * @throws UserInputValidationException if the bucket or folder name is invalid
     * @throws InvalidTokenException if the authentication token is invalid
     */
    @GetMapping(value = "/{bucketName}/{relativePath}")
    public ResponseEntity<ObjectNode> handleFileList(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            @PathVariable String bucketName,
            @PathVariable(value = "relativePath", required = false) String relativePath,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "100") int limit)
            throws
            StorageEntityNotFoundException,
            StorageEntityCreationException,
            StorageEntityAlreadyExistsException,
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
        logger.info("Getting file list of folder: " + pathToParent);
        return storageService.loadFiles(userService.getUserId(authentication), bucketName, pathToParent, page, limit);
    }

    /**
     * Lists all files in a folder inside a bucket for a user
     *
     * @param authentication the authentication token of the user
     * @param bucketName the bucket name where the file is located
     * @param page the page number
     * @param limit the number of files to return
     * @return the list of files as a {@link ResponseEntity<ObjectNode>}
     * @throws StorageEntityNotFoundException if the bucket or folder does not exist
     * @throws StorageEntityCreationException if the folder could not be created
     * @throws StorageEntityAlreadyExistsException if the folder already exists
     * @throws UserInputValidationException if the bucket or folder name is invalid
     * @throws InvalidTokenException if the authentication token is invalid
     */
    @GetMapping(value = "/{bucketName}")
    public ResponseEntity<ObjectNode> handleFileList(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            @PathVariable String bucketName,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "100") int limit)
            throws
            StorageEntityNotFoundException,
            StorageEntityCreationException,
            StorageEntityAlreadyExistsException,
            UserInputValidationException,
            InvalidTokenException {
        return handleFileList(authentication, bucketName, null, page, limit);
    }

    /**
     * Uploads a file to a folder inside a bucket for a user
     *
     * @param authentication the authentication token of the user
     * @param bucket the bucket name where the file should be uploaded
     * @param file the file to upload
     * @return the response as a {@link ResponseEntity<ObjectNode>}
     * @throws MaxUploadSizeExceededException if the file is too big
     * @throws StorageEntityNotFoundException if the bucket or folder does not exist
     * @throws UserNotFoundException if the user does not exist
     * @throws UserInputValidationException if the bucket, folder name is invalid
     * @throws InvalidTokenException if the authentication token is invalid
     */
    @PostMapping("/{bucket}")
    public ResponseEntity<ObjectNode> handleFileUploadByName(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            @PathVariable("bucket") String bucket,
            @RequestParam("file") MultipartFile file)
            throws
            MaxUploadSizeExceededException,
            StorageEntityNotFoundException,
            StorageEntityCreationException,
            StorageEntityAlreadyExistsException,
            UserNotFoundException,
            UserInputValidationException,
            InvalidTokenException{
        return handleFileUploadByName(authentication, bucket, null, file);
    }


}
