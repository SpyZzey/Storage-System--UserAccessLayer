package de.storagesystem.api;

import de.storagesystem.api.auth.Authentication;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
public class StorageSystemApplication {

    private static final Logger logger = LogManager.getLogger(StorageSystemApplication.class);

    public static void main(String[] args) {
        /*
        String privateKeyPath = Authentication.getPathToPrivateKey();
        String publicKeyPath = Authentication.getPathToPublicKey();
        try {
            Authentication.createRSAKey(publicKeyPath, privateKeyPath);
        } catch (NoSuchAlgorithmException | IOException e) {
            logger.error("Starting StorageSystem API");
            throw new RuntimeException(e);
        }
         */
        SpringApplication.run(StorageSystemApplication.class, args);
    }

    @PreDestroy
    public void onExit() {
        logger.info("Stopping StorageSystem API Test");
    }

}
