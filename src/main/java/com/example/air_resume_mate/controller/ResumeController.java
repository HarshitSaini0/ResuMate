package com.example.air_resume_mate.controller;

import com.example.air_resume_mate.model.Education;
import com.example.air_resume_mate.model.Project;
import com.example.air_resume_mate.model.User;
import com.example.air_resume_mate.model.WorkExperience;
import com.example.air_resume_mate.repository.EducationRepository;
import com.example.air_resume_mate.repository.ProjectRepository;
import com.example.air_resume_mate.repository.UserRepository;
import com.example.air_resume_mate.repository.WorkExperienceRepository;
import com.example.air_resume_mate.service.ResumeGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    private final ResumeGenerationService resumeGenerationService;
    private final UserRepository userRepository;
    private final WorkExperienceRepository workExperienceRepository;
    private final EducationRepository educationRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public ResumeController(ResumeGenerationService resumeGenerationService, UserRepository userRepository,
                            WorkExperienceRepository workExperienceRepository, EducationRepository educationRepository,
                            ProjectRepository projectRepository) {
        this.resumeGenerationService = resumeGenerationService;
        this.userRepository = userRepository;
        this.workExperienceRepository = workExperienceRepository;
        this.educationRepository = educationRepository;
        this.projectRepository = projectRepository;
    }

    // DTO for the request body
    public static class GenerateRequest {
        public String jobRole;
    }

    @PostMapping("/generate/summary")
    public ResponseEntity<String> generateSummary(@RequestBody GenerateRequest request) {
        // 1. Get the currently logged-in user's email from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = ((UserDetails) authentication.getPrincipal()).getUsername();

        // 2. Find the user in the database
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Long userId = currentUser.getId();

        // 3. Fetch all profile data for this user
        List<String> userProfileData = new ArrayList<>();

        List<WorkExperience> experiences = workExperienceRepository.findAll(); // In real app, filter by userId
        userProfileData.addAll(experiences.stream()
                .filter(exp -> exp.getUser() != null && exp.getUser().getId().equals(userId)) // Filter for current user
                .map(exp -> "Job: " + exp.getJobTitle() + " at " + exp.getCompany() + ". Description: " + exp.getDescription())
                .collect(Collectors.toList()));

        List<Education> educations = educationRepository.findAll(); // In real app, filter by userId
        userProfileData.addAll(educations.stream()
                .filter(edu -> edu.getUser() != null && edu.getUser().getId().equals(userId)) // Filter for current user
                .map(edu -> "Degree: " + edu.getDegree() + " in " + edu.getFieldOfStudy() + " from " + edu.getInstitution())
                .collect(Collectors.toList()));

        List<Project> projects = projectRepository.findAll(); // In real app, filter by userId
        userProfileData.addAll(projects.stream()
                .filter(proj -> proj.getUser() != null && proj.getUser().getId().equals(userId)) // Filter for current user
                .map(proj -> "Project: " + proj.getProjectName() + ". Description: " + proj.getDescription())
                .collect(Collectors.toList()));

        // 4. Call the RAG service
        String generatedSummary = resumeGenerationService.generateResumeSummary(request.jobRole, userProfileData);

        // 5. Return the result
        return ResponseEntity.ok(generatedSummary);
    }
}