package de.storagesystem.api.buckets;

import de.storagesystem.api.exceptions.StorageEntityAlreadyExistsException;
import de.storagesystem.api.exceptions.StorageEntityNotFoundException;
import de.storagesystem.api.exceptions.UserNotFoundException;
import de.storagesystem.api.users.User;
import de.storagesystem.api.users.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/buckets")
public class BucketController {

    private static final Logger logger = LogManager.getLogger(BucketController.class);

    private final StorageService storageService;
    private final UserService userService;

    @Autowired
    public BucketController(BucketService bucketService, UserService userService) {
        this.storageService = bucketService;
        this.userService = userService;
    }

    @GetMapping("/download/{bucket}/{path}/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            @PathVariable String bucket,
            @PathVariable String path,
            @PathVariable String filename) throws StorageEntityNotFoundException, UserNotFoundException {
        logger.info("Download file " + filename + " from bucket " + bucket + " in path " + path);
        ByteArrayResource file = storageService.loadAsResourcebyPath(
                getUserId(authentication),
                bucket,
                Path.of(File.separator + path + File.separator + filename));

        logger.info("Sending file " + filename + " from bucket " + bucket + " in path " + path);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentLength(file.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }

    @GetMapping("/")
    public String listUploadedFiles(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            Model model) throws MaxUploadSizeExceededException {
        model.addAttribute("files", storageService.loadAll(getUserId(authentication)).map(
                        path -> MvcUriComponentsBuilder.fromMethodName(BucketController.class,
                                "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList()));

        return "uploadForm";
    }

    @PostMapping(value = "/files", params = {"bucket", "dir"})
    public ResponseEntity<Map<String, String>> handleFileUploadByName(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            @RequestParam("bucket") String bucketName,
            @RequestParam(value = "dir", required = false) String dir,
            @RequestParam("file") MultipartFile file) throws MaxUploadSizeExceededException, StorageEntityNotFoundException, UserNotFoundException {
        logger.info("Upload file " + file.getOriginalFilename() + " to bucket " + bucketName + " in path " + dir);
        boolean stored = storageService.storeFile(getUserId(authentication), bucketName, dir, file);

        if(stored) {
            logger.info("File " + file.getOriginalFilename() + " stored in bucket " + bucketName + " in path " + dir);
            return ResponseEntity.ok(Map.of(
                    "status", "ok",
                    "message", "File stored"));
        } else {
            logger.error("File " + file.getOriginalFilename() + " could not be stored in bucket " + bucketName + " in path " + dir);
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message","File already exists"));
        }
    }

    @PostMapping(value = "/directory/", params = {"bucket"})
    public ResponseEntity<Map<String, String>> handleDirectoryCreation(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            @RequestParam("bucket") String bucketName,
            @RequestParam(value = "parentDirectory", required = false) String parentDirectory,
            @RequestParam("directory") String directory) throws MaxUploadSizeExceededException {
        boolean created = storageService.createDirectory(getUserId(authentication), bucketName, parentDirectory, directory);
        if(created) return ResponseEntity.ok(Map.of(
                "status", "ok",
                "message", "Directory created"));

        return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message","Directory already exists"));
    }

    @PostMapping(value = "/")
    public ResponseEntity<Map<String, String>> handleBucketCreation(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            @RequestParam("bucket") String bucketName)
            throws MaxUploadSizeExceededException, StorageEntityAlreadyExistsException {
        boolean created = storageService.createBucket(getUserId(authentication), bucketName);
        if(created) return ResponseEntity.ok(Map.of(
                "status", "ok",
                "message", "Bucket created"));

        return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message","Bucket already exists"));
    }

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

    public Long getUserId(String authentication) {
        Optional<User> user = userService.getUser(authentication);
        if(user.isEmpty()) throw new UserNotFoundException();
        return user.get().id();
    }
}
