package com.rentease.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${rentease.upload.dir:uploads/properties}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String resolvedUploadDir = (uploadDir == null || uploadDir.isBlank()) ? "uploads/properties" : uploadDir;
        Path uploadPath = Paths.get(resolvedUploadDir).toAbsolutePath().normalize();
        String uploadAbsolutePath = uploadPath.toString();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadAbsolutePath + "/");
    }
}

