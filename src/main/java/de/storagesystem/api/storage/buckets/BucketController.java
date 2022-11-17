package de.storagesystem.api.storage.buckets;

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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.Map;
import java.util.stream.Collectors;

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
    public ResponseEntity<Map<String, String>> handleBucketCreation(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            @RequestParam("bucket") String bucketName)
            throws MaxUploadSizeExceededException,
            StorageEntityAlreadyExistsException,
            UserInputValidationException,
            InvalidTokenException {
        StorageInputValidation inputValidation = new StorageInputValidationImpl();
        if(!inputValidation.validateBucketName(bucketName))
            throw new UserInputValidationException("Invalid bucket name");

        logger.info("Creating bucket: " + bucketName);
        boolean created = storageService.createBucket(userService.getUserId(authentication), bucketName);
        if(created) return ResponseEntity.ok(Map.of(
                "status", "ok",
                "message", "Bucket created"));

        return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message","Bucket already exists"));
    }


}
