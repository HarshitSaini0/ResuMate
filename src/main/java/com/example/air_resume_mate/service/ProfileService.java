package com.example.air_resume_mate.service;

import com.example.air_resume_mate.model.Education;
import com.example.air_resume_mate.model.Project;
import com.example.air_resume_mate.model.User;
import com.example.air_resume_mate.model.WorkExperience;
import com.example.air_resume_mate.repository.EducationRepository;
import com.example.air_resume_mate.repository.ProjectRepository;
import com.example.air_resume_mate.repository.UserRepository;
import com.example.air_resume_mate.repository.WorkExperienceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final WorkExperienceRepository workExperienceRepository;
    private final EducationRepository educationRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public ProfileService(UserRepository userRepository, WorkExperienceRepository workExperienceRepository, EducationRepository educationRepository, ProjectRepository projectRepository) {
        this.userRepository = userRepository;
        this.workExperienceRepository = workExperienceRepository;
        this.educationRepository = educationRepository;
        this.projectRepository = projectRepository;
    }

    // Method to add work experience to a specific user
    public WorkExperience addWorkExperience(Long userId, WorkExperience experience) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        experience.setUser(user);
        return workExperienceRepository.save(experience);
    }

    // Method to add education to a specific user
    public Education addEducation(Long userId, Education education) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        education.setUser(user);
        return educationRepository.save(education);
    }

    // Method to add a project to a specific user
    public Project addProject(Long userId, Project project) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        project.setUser(user);
        return projectRepository.save(project);
    }
}