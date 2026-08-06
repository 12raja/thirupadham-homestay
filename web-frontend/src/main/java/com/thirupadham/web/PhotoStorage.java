package com.thirupadham.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class PhotoStorage {

    @Value("${homestay.upload-dir:/data/uploads}")
    private String uploadDir;

    public void save(MultipartFile file) throws IOException {
        Path dir = Paths.get(uploadDir);
        Files.createDirectories(dir);

        String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : "photo";
        String extension = original.contains(".") ? original.substring(original.lastIndexOf('.')) : "";
        // A random prefix avoids two guests' phones both uploading
        // "IMG_0001.jpg" and one silently overwriting the other.
        String filename = UUID.randomUUID() + extension;

        Files.copy(file.getInputStream(), dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
    }

    public List<String> listLatest(int limit) {
        Path dir = Paths.get(uploadDir);
        if (!Files.exists(dir)) {
            return List.of();
        }
        try (Stream<Path> files = Files.list(dir)) {
            return files
                    .sorted(Comparator.comparingLong(this::lastModified).reversed())
                    .limit(limit)
                    .map(p -> "/uploads/" + p.getFileName())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return List.of();
        }
    }

    private long lastModified(Path path) {
        try {
            return Files.getLastModifiedTime(path).toInstant().toEpochMilli();
        } catch (IOException e) {
            return Instant.EPOCH.toEpochMilli();
        }
    }
}
