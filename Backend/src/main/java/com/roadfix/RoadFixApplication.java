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
        System.out.println("  Active Mongo URI: " + maskMongoUri(System.getProperty("MONGODB_URI", "mongodb://localhost:27017/roadfix_db")));
        System.out.println("==================================================");
    }

    /**
     * Reads .env file from parent directory (root) or current working directory
     * and sets System properties so Spring Boot can pick them up via ${PROPERTY_NAME}.
     */
    private static void loadEnvironmentVariables() {
        File[] candidates = new File[] {
            new File("../.env"),       // Root project folder when running from Backend
            new File(".env"),          // Current directory
            new File("Backend/.env")   // Backend folder when running from root
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
                            if (val.contains("mongodb+srv://") || System.getProperty(key) == null) {
                                System.setProperty(key, val);
                                count++;
                            }
                        }
                    }
                    if (count > 0) {
                        System.out.println(">>> [ENV LOADED] Successfully loaded " + count + " variables from: " + envFile.getCanonicalPath());
                        checkMongoConfigWarnings();
                        return;
                    }
                } catch (Exception e) {
                    System.err.println("Note: Could not parse .env file (" + envFile.getName() + "): " + e.getMessage());
                }
            }
        }
        System.out.println(">>> [ENV NOTE] No .env file found; using default properties.");
    }

    private static void checkMongoConfigWarnings() {
        String uri = System.getProperty("MONGODB_URI", "");
        if (uri.contains("<") && uri.contains(">")) {
            System.err.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.err.println("  [MONGODB CONFIG ERROR] Invalid password in MONGODB_URI!");
            System.err.println("  Found placeholder '<>' in your .env connection string.");
            System.err.println("  Please open .env and replace '<>' with your actual database password,");
            System.err.println("  WITHOUT the angle brackets! Example:");
            System.err.println("  mongodb+srv://rt13115_db_user:YOUR_PASSWORD_HERE@cluster0.rkb3vbg.mongodb.net/roadfix");
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
        }
    }

    private static String maskMongoUri(String uri) {
        if (uri == null) return "none";
        // Mask password in logs
        return uri.replaceAll("(?<=://)[^:]+:[^@]+@", "***:***@");
    }
}
