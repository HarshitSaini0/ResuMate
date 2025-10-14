package com.example.air_resume_mate.controller;

import com.example.air_resume_mate.model.Education;
import com.example.air_resume_mate.model.Project;
import com.example.air_resume_mate.model.WorkExperience;
import com.example.air_resume_mate.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileService profileService;

    @Autowired
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    // Endpoint to add work experience for a user
    @PostMapping("/{userId}/experience")
    public WorkExperience addWorkExperience(@PathVariable Long userId, @RequestBody WorkExperience experience) {
        return profileService.addWorkExperience(userId, experience);
    }

    // Endpoint to add education for a user
    @PostMapping("/{userId}/education")
    public Education addEducation(@PathVariable Long userId, @RequestBody Education education) {
        return profileService.addEducation(userId, education);
    }

    // Endpoint to add a project for a user
    @PostMapping("/{userId}/project")
    public Project addProject(@PathVariable Long userId, @RequestBody Project project) {
        return profileService.addProject(userId, project);
    }
}