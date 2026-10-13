package com.rentease.service;

import com.rentease.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${rentease.upload.dir:uploads/properties}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create upload directory.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "image.jpg");

        if (originalFilename.contains("..")) {
            throw new BadRequestException("Invalid path sequence in filename: " + originalFilename);
        }

        String extension = "";
        int i = originalFilename.lastIndexOf('.');
        if (i >= 0) {
            extension = originalFilename.substring(i);
        } else {
            extension = ".jpg";
        }

        String newFileName = UUID.randomUUID().toString() + extension;

        try {
            Path targetLocation = this.fileStorageLocation.resolve(newFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/properties/" + newFileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + newFileName + ". Please try again!", ex);
        }
    }

    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith("/uploads/properties/")) {
            return false;
        }
        try {
            String filename = fileUrl.replace("/uploads/properties/", "");
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            return Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            return false;
        }
    }
}
