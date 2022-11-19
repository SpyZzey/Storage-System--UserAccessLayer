package de.storagesystem.api;

import de.storagesystem.api.storage.buckets.BucketService;
import de.storagesystem.api.storage.servers.StorageServer;
import de.storagesystem.api.storage.servers.StorageServerDAO;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PreDestroy;
import java.io.File;
import java.util.Optional;

@SpringBootApplication
public class StorageSystemApplication implements WebMvcConfigurer {

    /**
     * The {@link Logger} for this class
     */
    private static final Logger logger = LogManager.getLogger(StorageSystemApplication.class);

    /**
     * The main method to start the application.
     */
    public static void main(String[] args) {
        System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");

        logger.info("Starting server");
        SpringApplication.run(StorageSystemApplication.class, args);
    }

    /**
     * Exit event
     */
    @PreDestroy
    public void onExit() {
        logger.info("Stopping StorageSystem API Test");

    }

}
