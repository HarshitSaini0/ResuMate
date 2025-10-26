package com.example.air_resume_mate.service;

import com.example.air_resume_mate.model.Template;
import com.example.air_resume_mate.model.User;
import com.example.air_resume_mate.repository.TemplateRepository;
import com.example.air_resume_mate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils; // Import StringUtils
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID; // Import UUID

@Service
public class TemplateService {

    private final Path templateStorageLocation;
    private final TemplateRepository templateRepository;
    private final UserRepository userRepository;

    @Autowired
    public TemplateService(@Value("${file.upload-dir}") String uploadDir,
                           TemplateRepository templateRepository,
                           UserRepository userRepository) {
        this.templateRepository = templateRepository;
        this.userRepository = userRepository;
        this.templateStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        // Create the directory if it doesn't exist
        try {
            Files.createDirectories(this.templateStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public Template storeTemplate(MultipartFile file) {
        // 1. Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = ((UserDetails) authentication.getPrincipal()).getUsername();
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found for storing template"));

        // 2. Clean and validate filename
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        if (originalFileName.contains("..")) {
            throw new RuntimeException("Sorry! Filename contains invalid path sequence " + originalFileName);
        }
        // Basic check for DOCX (can be improved)
        if (!originalFileName.toLowerCase().endsWith(".docx")) {
            throw new RuntimeException("Invalid file type. Only .docx templates are allowed.");
        }

        // 3. Generate unique filename
        String fileExtension = "";
        int lastDot = originalFileName.lastIndexOf('.');
        if (lastDot > 0) {
            fileExtension = originalFileName.substring(lastDot);
        }
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        try {
            // 4. Save the file to the target location
            Path targetLocation = this.templateStorageLocation.resolve(uniqueFileName);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            // 5. Create and save template metadata to database
            Template template = new Template();
            template.setFileName(uniqueFileName);
            template.setOriginalFileName(originalFileName);
            template.setContentType(file.getContentType());
            template.setUser(currentUser);

            return templateRepository.save(template);

        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }
}