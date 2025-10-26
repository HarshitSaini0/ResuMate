package com.example.air_resume_mate.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp; // Import this

import java.time.LocalDateTime; // Import this

@Entity
@Getter
@Setter
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The unique filename assigned when stored on the server (e.g., UUID.docx)
    @Column(nullable = false, unique = true)
    private String fileName;

    // The original filename provided by the user (e.g., "my_resume_template.docx")
    @Column(nullable = false)
    private String originalFileName;

    // Store the content type (e.g., "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    private String contentType;

    // Timestamp when the template was uploaded
    @CreationTimestamp // Automatically set the time when created
    private LocalDateTime uploadTimestamp;

    // Link to the user who uploaded the template
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;



    // Constructors, Getters/Setters are handled by Lombok

}