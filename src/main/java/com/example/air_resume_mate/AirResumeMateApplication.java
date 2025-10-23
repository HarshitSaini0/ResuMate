package com.example.air_resume_mate;

import io.github.cdimascio.dotenv.Dotenv; // <-- Import Dotenv
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AirResumeMateApplication {

    public static void main(String[] args) {
        // Load .env file and set system properties
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing() // Don't crash if .env is not found (e.g., in production)
                .load();
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });

        // Run Spring Boot
        SpringApplication.run(AirResumeMateApplication.class, args);
    }
}