package com.whatacook.cookers.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class dockerSecrets {
    public static String readSecret(String secrect, String fallbackEnv) throws IOException {
        try {
            return new String(Files.readAllBytes(Paths.get(rootPath + secrect))).trim();
        } catch (IOException e) {
            return System.getenv(fallbackEnv);
        }
    }

    private static final String rootPath = "/run/secrets/";
}
