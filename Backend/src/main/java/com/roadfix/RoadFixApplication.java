package com.roadfix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

@SpringBootApplication
public class RoadFixApplication {

    public static void main(String[] args) {
        loadEnvironmentVariables();
        SpringApplication.run(RoadFixApplication.class, args);
        System.out.println("==================================================");
        System.out.println("  RoadFix AI (MongoDB Edition) Running!");
        System.out.println("  Portal URL : http://localhost:" + System.getProperty("PORT", "8080") + "/");
        System.out.println("  API Status : http://localhost:" + System.getProperty("PORT", "8080") + "/api/database/status");
        System.out.println("  MongoDB URI: " + System.getProperty("MONGODB_URI", "mongodb://localhost:27017/roadfix_db"));
        System.out.println("==================================================");
    }

    /**
     * Reads .env file from working directory or parent directory and populates System properties
     * so Spring Boot can pick them up via ${PROPERTY_NAME} syntax.
     */
    private static void loadEnvironmentVariables() {
        File[] candidates = new File[] {
            new File(".env"),
            new File("../.env"),
            new File("Backend/.env")
        };

        for (File envFile : candidates) {
            if (envFile.exists() && envFile.isFile()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
                    String line;
                    int count = 0;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#")) continue;
                        int eq = line.indexOf('=');
                        if (eq > 0) {
                            String key = line.substring(0, eq).trim();
                            String val = line.substring(eq + 1).trim();
                            // Strip matching quotes if present
                            if ((val.startsWith("\"") && val.endsWith("\"")) || (val.startsWith("'") && val.endsWith("'"))) {
                                if (val.length() >= 2) {
                                    val = val.substring(1, val.length() - 1);
                                }
                            }
                            // Only set if not already set by system/OS environment
                            if (System.getProperty(key) == null && System.getenv(key) == null) {
                                System.setProperty(key, val);
                                count++;
                            }
                        }
                    }
                    System.out.println(">>> [ENV LOADED] Successfully loaded " + count + " variables from: " + envFile.getCanonicalPath());
                    return;
                } catch (Exception e) {
                    System.err.println("Note: Could not parse .env file (" + envFile.getName() + "): " + e.getMessage());
                }
            }
        }
        System.out.println(">>> [ENV NOTE] No .env file found; using default properties.");
    }
}
