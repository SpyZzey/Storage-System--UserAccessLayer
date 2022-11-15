package de.storagesystem.api.storage.buckets;

import de.storagesystem.api.exceptions.StorageEntityAlreadyExistsException;
import de.storagesystem.api.exceptions.StorageEntityNotFoundException;
import de.storagesystem.api.exceptions.UserNotFoundException;
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

@Controller
@RequestMapping("/api/buckets")
public class BucketController {

    private static final Logger logger = LogManager.getLogger(BucketController.class);

    private final BucketService storageService;
    private final UserService userService;

    @Autowired
    public BucketController(BucketService bucketService, UserService userService) {
        this.storageService = bucketService;
        this.userService = userService;
    }

    @PostMapping(value = "/")
    public ResponseEntity<Map<String, String>> handleBucketCreation(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            @RequestParam("bucket") String bucketName)
            throws MaxUploadSizeExceededException, StorageEntityAlreadyExistsException {
        boolean created = storageService.createBucket(userService.getUserId(authentication), bucketName);
        if(created) return ResponseEntity.ok(Map.of(
                "status", "ok",
                "message", "Bucket created"));

        return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message","Bucket already exists"));
    }


}
