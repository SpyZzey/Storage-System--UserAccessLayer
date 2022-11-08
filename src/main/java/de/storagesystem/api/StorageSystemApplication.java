package de.storagesystem.api;

import de.storagesystem.api.auth.Authentication;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
public class StorageSystemApplication {

    public static void main(String[] args) {
        String privateKeyPath = Authentication.getPathToPrivateKey();
        String publicKeyPath = Authentication.getPathToPublicKey();
        try {
            Authentication.createRSAKey(publicKeyPath, privateKeyPath);
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
        SpringApplication.run(StorageSystemApplication.class, args);
    }

}
