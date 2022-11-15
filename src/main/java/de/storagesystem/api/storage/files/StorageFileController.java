package de.storagesystem.api.storage.files;

import de.storagesystem.api.exceptions.StorageEntityNotFoundException;
import de.storagesystem.api.exceptions.UserNotFoundException;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/api/files/")
public class StorageFileController {
    private static final Logger logger = LogManager.getLogger(StorageFileController.class);
    private final StorageFileService storageService;
    private final UserService userService;

    @Autowired
    public StorageFileController(StorageFileService storageService, UserService userService) {
        this.storageService = storageService;
        this.userService = userService;
    }

    @GetMapping("/{bucket}/{path}/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            @PathVariable String bucket,
            @PathVariable String path,
            @PathVariable String filename) throws StorageEntityNotFoundException, UserNotFoundException {
        logger.info("Download file " + filename + " from bucket " + bucket + " in path " + path);
        ByteArrayResource file = storageService.loadAsResourcebyPath(
                userService.getUserId(authentication),
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
    public ResponseEntity<Map<String, String>> listUploadedFiles() throws IOException {
        return ResponseEntity.ok().build() ;
    }

    @PostMapping(value = "/", params = {"bucket", "folder"})
    public ResponseEntity<Map<String, String>> handleFileUploadByName(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            @RequestParam("bucket") String bucketName,
            @RequestParam(value = "folder", required = false) String folder,
            @RequestParam("file") MultipartFile file) throws MaxUploadSizeExceededException, StorageEntityNotFoundException, UserNotFoundException {
        logger.info("Upload file " + file.getOriginalFilename() + " to bucket " + bucketName + " in path " + folder);
        boolean stored = storageService.storeFile(userService.getUserId(authentication), bucketName, folder, file);

        if(stored) {
            logger.info("File " + file.getOriginalFilename() + " stored in bucket " + bucketName + " in path " + folder);
            return ResponseEntity.ok(Map.of(
                    "status", "ok",
                    "message", "File stored"));
        } else {
            logger.error("File " + file.getOriginalFilename() + " could not be stored in bucket " + bucketName + " in path " + folder);
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message","File already exists"));
        }
    }
}
