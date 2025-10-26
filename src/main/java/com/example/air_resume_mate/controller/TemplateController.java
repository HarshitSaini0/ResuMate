package com.example.air_resume_mate.controller;

import com.example.air_resume_mate.model.Template;
import com.example.air_resume_mate.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder; // Import this

import java.net.URI; // Import this

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    private final TemplateService templateService;

    @Autowired
    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Template> uploadTemplate(@RequestParam("file") MultipartFile file) {
        // Basic check if the file is empty
        if (file.isEmpty()) {
            // Consider a more specific error response
            return ResponseEntity.badRequest().build();
        }

        try {
            Template savedTemplate = templateService.storeTemplate(file);

            // Construct the URI for the newly created resource (optional but good practice)
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}") // Assuming you might add a GET endpoint later
                    .buildAndExpand(savedTemplate.getId())
                    .toUri();

            // Return 201 Created status with the location and the saved template info
            return ResponseEntity.created(location).body(savedTemplate);

        } catch (RuntimeException e) {
            // Log the exception (using a proper logger is recommended)
            System.err.println("Error uploading template: " + e.getMessage());
            // Return an appropriate error response (e.g., 400 Bad Request or 500 Internal Server Error)
            // Depending on the exception type (e.g., invalid filename vs. storage issue)
            return ResponseEntity.badRequest().build(); // Simple error for now
        }
    }

    // --- Optional: Add an endpoint to list user's templates later ---
    // @GetMapping("/my-templates")
    // public ResponseEntity<List<Template>> getUserTemplates() { ... }
}