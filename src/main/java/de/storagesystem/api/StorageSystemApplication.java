package de.storagesystem.api;

import de.storagesystem.api.buckets.StorageService;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PreDestroy;
import java.nio.file.Path;

@SpringBootApplication
public class StorageSystemApplication implements WebMvcConfigurer {

    private static final Logger logger = LogManager.getLogger(StorageSystemApplication.class);

    public static void main(String[] args) {
        System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");

        logger.info("Starting server");
        SpringApplication.run(StorageSystemApplication.class, args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> storageService.init();
    }

    @PreDestroy
    public void onExit() {
        logger.info("Stopping StorageSystem API Test");
    }

}
