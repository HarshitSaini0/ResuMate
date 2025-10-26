package com.example.air_resume_mate.repository;

import com.example.air_resume_mate.model.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Import List

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {

    // Add a method to find all templates belonging to a specific user
    List<Template> findByUserId(Long userId);

}