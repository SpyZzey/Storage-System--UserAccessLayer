package de.storagesystem.api.storage.buckets;

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

/**
 * @author Simon Brebeck
 */
@Controller
@RequestMapping("/api/buckets")
public class BucketController {

    /**
     * The {@link Logger} for this class.
     */
    private static final Logger logger = LogManager.getLogger(BucketController.class);

    /**
     * The {@link BucketService} that is used to access the buckets.
     */
    private final BucketService storageService;

    /**
     * The {@link UserService} that is used to access the users.
     */
    private final UserService userService;

    /**
     * Creates a new instance of {@link BucketController} with the given {@link BucketService} and {@link UserService}.
     *
     * @param bucketService The {@link BucketService} that is used to access the buckets.
     * @param userService    The {@link UserService} that is used to access the users.
     */
    @Autowired
    public BucketController(BucketService bucketService, UserService userService) {
        this.storageService = bucketService;
        this.userService = userService;
    }


    /**
     * Creates a new bucket with the given name for user.
     * @param authentication The authentication of the user.
     * @param bucketName The name of the bucket.
     * @return The ResponseEntity with the status code.
     * @throws MaxUploadSizeExceededException If the size of the file is too big.
     * @throws StorageEntityAlreadyExistsException If the bucket already exists.
     * @throws UserInputValidationException If the bucket name is not valid.
     * @throws InvalidTokenException If the token is invalid.
     */
    @PostMapping(value = "/")
    public ResponseEntity<ObjectNode> handleBucketCreation(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            @RequestParam("bucket") String bucketName)
            throws MaxUploadSizeExceededException,
            StorageEntityAlreadyExistsException,
            UserInputValidationException,
            InvalidTokenException {
        // Validate user input
        StorageInputValidation inputValidation = new StorageInputValidationImpl();
        if(!inputValidation.validateBucketName(bucketName)) // Validate bucket name
            throw new UserInputValidationException("Invalid bucket name");

        logger.info("Creating bucket: " + bucketName);

        // Create bucket
        return storageService.createBucket(userService.getUserId(authentication), bucketName);
    }

    @DeleteMapping(value = "/{bucketName}")
    public ResponseEntity<ObjectNode> handleBucketDeletion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            @PathVariable String bucketName)
            throws UserInputValidationException,
            InvalidTokenException {
        // Validate user input
        StorageInputValidation inputValidation = new StorageInputValidationImpl();
        if(!inputValidation.validateBucketName(bucketName)) // Validate bucket name
            throw new UserInputValidationException("Invalid bucket name");

        logger.info("Deleting bucket: " + bucketName);

        // Delete bucket
        return storageService.deleteBucket(userService.getUserId(authentication), bucketName);
    }

    @GetMapping(value = "/")
    public ResponseEntity<ObjectNode> handleBucketList(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "100") int limit)
            throws InvalidTokenException {

        // Validate user input
        if(page < 0 || limit < 0) throw new IllegalArgumentException("Page and limit must be greater than 0");
        if(limit > 100) throw new IllegalArgumentException("Cannot get more than 100 buckets at once");

        logger.info("Getting bucket list");
        // Get bucket list
        return storageService.loadPage(userService.getUserId(authentication), page, limit);
    }

    @GetMapping(value = "/{bucketName}")
    public ResponseEntity<ObjectNode> handleBucketInfo(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            @PathVariable String bucketName)
            throws UserInputValidationException,
            InvalidTokenException {
        // Validate user input
        StorageInputValidation inputValidation = new StorageInputValidationImpl();
        if(!inputValidation.validateBucketName(bucketName)) // Validate bucket name
            throw new UserInputValidationException("Invalid bucket name");

        logger.info("Getting bucket info: " + bucketName);
        // Get bucket info
        return storageService.loadBucketInfo(userService.getUserId(authentication), bucketName);
    }

    @GetMapping(value = "/{bucketName}/files")
    public ResponseEntity<ObjectNode> handleFileList(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            @PathVariable String bucketName,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "100") int limit)
            throws UserInputValidationException,
            InvalidTokenException {
        // Validate user input
        StorageInputValidation inputValidation = new StorageInputValidationImpl();
        if(!inputValidation.validateBucketName(bucketName))
            throw new UserInputValidationException("Invalid bucket name");
        if(page < 0 || limit < 0)
            throw new IllegalArgumentException("Page and limit must be greater than 0");
        if(limit > 100)
            throw new IllegalArgumentException("Cannot get more than 100 buckets at once");

        logger.info("Getting file list of bucket: " + bucketName);
        return storageService.loadBucketFiles(userService.getUserId(authentication), bucketName, page, limit);
    }

    @GetMapping(value = "/{bucketName}/folders")
    public ResponseEntity<ObjectNode> handleFolderList(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            @PathVariable String bucketName,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "100") int limit)
            throws UserInputValidationException,
            InvalidTokenException {
        // Validate user input
        StorageInputValidation inputValidation = new StorageInputValidationImpl();
        if(!inputValidation.validateBucketName(bucketName))
            throw new UserInputValidationException("Invalid bucket name");
        if(page < 0 || limit < 0)
            throw new IllegalArgumentException("Page and limit must be greater than 0");
        if(limit > 100)
            throw new IllegalArgumentException("Cannot get more than 100 buckets at once");

        logger.info("Getting folder list of bucket: " + bucketName);
        return storageService.loadBucketFolders(userService.getUserId(authentication), bucketName, page, limit);
    }

}
