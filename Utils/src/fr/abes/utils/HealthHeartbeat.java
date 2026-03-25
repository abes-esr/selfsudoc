package fr.abes.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;

public class HealthHeartbeat {

    private static final Path HEALTH_FILE = Paths.get("/tmp/selfsudoc_heartbeat");

    public static void touch() {
        try {
            if (!Files.exists(HEALTH_FILE)) {
                Files.createFile(HEALTH_FILE);
            }
            Files.setLastModifiedTime(HEALTH_FILE, FileTime.fromMillis(System.currentTimeMillis()));
        } catch (IOException e) {
            System.err.println("Heartbeat update failed: " + e.getMessage());
        }
    }
}
