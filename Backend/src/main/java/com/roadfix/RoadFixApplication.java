package com.roadfix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RoadFixApplication {
    public static void main(String[] args) {
        SpringApplication.run(RoadFixApplication.class, args);
        System.out.println("==================================================");
        System.out.println("  RoadFix AI Simple Backend Started!");
        System.out.println("  API URL: http://localhost:8080/api/complaints");
        System.out.println("==================================================");
    }
}
