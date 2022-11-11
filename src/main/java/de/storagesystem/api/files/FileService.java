
package de.storagesystem.api.files;

import de.storagesystem.api.cryptography.FileAESCryptographer;
import de.storagesystem.api.cryptography.FileCryptographer;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.util.UUID;
import java.util.stream.Stream;

@Component
public class FileService implements StorageService {

    private static final String ROOT = "C:\\Users\\simon\\Desktop\\StorageSystem\\upload-directory\\";

    private final ResourceLoader resourceLoader;

    @Autowired
    public FileService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }


    @Override
    public void init() {
        File file = new File(ROOT);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Override
    public void store(String key, MultipartFile file) {
        try {

            Dotenv dotenv = Dotenv.load();

            String uuidFilename = dotenv.get("SERVER_UUID_PREFIX") + UUID.randomUUID().toString();
            // TODO: STORE FILE LINK IN DATABASE

            SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();
            FileCryptographer fc = new FileAESCryptographer(secretKey, "AES/CBC/PKCS5Padding");
            fc.encryptFile(ROOT + uuidFilename, file.getBytes());


            /*
            File newFile = new File(ROOT + key);
            file.transferTo(newFile);
             */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.list(Paths.get(ROOT));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Path load(String key) {
        return Paths.get(ROOT + key);
    }

    @Override
    public Resource loadAsResource(String key) {
        Resource resource = resourceLoader.getResource("file:" + ROOT + key);
        return resource;
    }
}
