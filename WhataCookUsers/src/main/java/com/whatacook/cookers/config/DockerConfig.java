package com.whatacook.cookers.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DockerConfig {
    public static String readSecret(String secret) throws IOException {
        try {
            return new String(Files.readAllBytes(Paths.get(rootPath + secret))).trim();
        } catch (IOException e) { return System.getenv(secret); }
    }

    private static final String rootPath = "/run/secrets/";
    
}
