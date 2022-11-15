package de.storagesystem.api.storage.folders;

import de.storagesystem.api.storage.buckets.BucketServiceImpl;
import de.storagesystem.api.storage.buckets.BucketService;
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

@Controller
@RequestMapping("/api/folders/")
public class StorageFolderController {

    private static final Logger logger = LogManager.getLogger(StorageFolderController.class);

    private final StorageFolderService storageService;
    private final UserService userService;

    @Autowired
    public StorageFolderController(StorageFolderService storageFolderService, UserService userService) {
        this.storageService = storageFolderService;
        this.userService = userService;
    }
    @PostMapping(value = "/", params = {"bucket"})
    public ResponseEntity<Map<String, String>> handleDirectoryCreation(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            @RequestParam("bucket") String bucket,
            @RequestParam(value = "parent", required = false) String parent,
            @RequestParam("folder") String folder) throws MaxUploadSizeExceededException {
        boolean created = storageService.createFolder(userService.getUserId(authentication), bucket, parent, folder);
        if(created) return ResponseEntity.ok(Map.of(
                "status", "ok",
                "message", "Folder created"));

        return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message","Folder already exists"));
    }

}
