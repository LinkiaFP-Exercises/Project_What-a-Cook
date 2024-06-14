package com.whatacook.cookers.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utility class for reading Docker secrets from a specified path.
 * <p>
 * Methods:
 * - readSecret(String secret): Reads the secret value from the specified path or environment variable.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
public class DockerConfig {

    private static final String rootPath = "/run/secrets/";

    /**
     * Reads the secret value from the specified path or environment variable.
     *
     * @param secret The name of the secret.
     * @return The secret value as a string.
     * @throws IOException If an I/O error occurs.
     */
    public static String readSecret(String secret) throws IOException {
        try {
            return new String(Files.readAllBytes(Paths.get(rootPath + secret))).trim();
        } catch (IOException e) {
            return System.getenv(secret);
        }
    }
}
