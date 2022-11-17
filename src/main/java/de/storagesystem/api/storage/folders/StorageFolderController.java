package de.storagesystem.api.storage.folders;

import de.storagesystem.api.exceptions.InvalidTokenException;
import de.storagesystem.api.exceptions.UserInputValidationException;
import de.storagesystem.api.storage.StorageInputValidation;
import de.storagesystem.api.storage.StorageInputValidationImpl;
import de.storagesystem.api.users.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
     * @param authentication the authentication token
     * @param bucket the bucket to create the folder in
     * @param parent the path to the parent folder to create the folder in
     * @param folder the name of the folder to create
     * @return the response as a {@link Map<String, String>}
     * @throws MaxUploadSizeExceededException if the upload size exceeds the maximum upload size
     * @throws UserInputValidationException if the folder path or bucket-/foldername is invalid
     * @throws InvalidTokenException if the token is invalid
     */
    @PostMapping(value = "/", params = {"bucket", "folder"})
    public ResponseEntity<Map<String, String>> handleDirectoryCreation(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            @RequestParam("bucket") String bucket,
            @RequestParam(value = "parent", required = false) String parent,
            @RequestParam("folder") String folder)
            throws MaxUploadSizeExceededException,
            UserInputValidationException,
            InvalidTokenException {
        StorageInputValidation inputValidation = new StorageInputValidationImpl();
        if(!inputValidation.validateBucketName(bucket))
            throw new UserInputValidationException("Invalid bucket name: " + bucket);
        if(!inputValidation.validateFolderPath(parent))
            throw new UserInputValidationException("Invalid folder path: " + parent);
        if(!inputValidation.validateFolderName(folder))
            throw new UserInputValidationException("Invalid folder name: " + folder);

        logger.info("Creating folder " + folder + " in bucket " + bucket + " with parent " + parent);
        boolean created = storageService.createFolder(userService.getUserId(authentication), bucket, parent, folder);
        if(created) return ResponseEntity.ok(Map.of(
                "status", "ok",
                "message", "Folder created"));
        else return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message","Folder already exists"));
    }

}
