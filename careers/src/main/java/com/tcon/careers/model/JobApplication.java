package com.tcon.careers.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "job_applications")
public class JobApplication {

    @Id
    @Builder.Default
    private String applicationId = UUID.randomUUID().toString();

    // Job Reference
    @NotBlank(message = "Job ID is required")
    @Indexed
    private String jobId;

    private String jobTitle;
    private String department;

    // Applicant Personal Information
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Indexed
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    private String linkedinUrl;
    private String portfolioUrl;
    private String githubUrl;

    // Location Details
    @NotBlank(message = "Current location is required")
    private String currentLocation;

    @Builder.Default
    private Boolean willingToRelocate = false;

    // Professional Information
    @NotNull(message = "Years of experience is required")
    private Integer yearsOfExperience;

    @NotBlank(message = "Current role is required")
    private String currentRole;

    private String currentCompany;

    @NotBlank(message = "Notice period is required")
    private String noticePeriod; // 'Immediate', '15 days', '30 days', '60 days', '90 days'

    private String expectedSalary;

    // Application Documents
    @NotBlank(message = "Resume URL is required")
    private String resumeUrl;

    @NotBlank(message = "Resume file name is required")
    private String resumeFileName;

    private Long resumeFileSize;

    private String coverLetter;

    // Additional Information
    private String referralSource; // 'LinkedIn', 'Job Board', 'Referral', 'Company Website', 'Other'
    private String referralName;
    private String additionalComments;

    // Application Status Tracking
    @Builder.Default
    private String status = "submitted"; // 'submitted', 'screening', 'interview-scheduled', 'interview-completed', 'rejected', 'offer-extended', 'hired'

    @Builder.Default
    private List<StatusHistory> statusHistory = new ArrayList<>();

    // Skills & Qualifications
    private List<String> skills;
    private List<String> certifications;

    @NotBlank(message = "Education is required")
    private String education; // 'High School', 'Bachelor's', 'Master's', 'PhD'

    // Metadata
    @Builder.Default
    @Indexed
    private LocalDateTime applicationDate = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime lastUpdated = LocalDateTime.now();

    private String ipAddress;
    private String userAgent;

    // Admin Notes
    @Builder.Default
    private List<AdminNote> adminNotes = new ArrayList<>();

    // Interview Details
    @Builder.Default
    private List<InterviewSchedule> interviewSchedule = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusHistory {
        private String status;
        private String changedBy;
        @Builder.Default
        private LocalDateTime changedAt = LocalDateTime.now();
        private String notes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminNote {
        private String note;
        private String addedBy;
        @Builder.Default
        private LocalDateTime addedAt = LocalDateTime.now();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InterviewSchedule {
        private Integer round;
        private LocalDateTime scheduledDate;
        private String interviewer;
        private String type; // 'phone', 'video', 'in-person', 'technical'
        private String feedback;
        private String status; // 'scheduled', 'completed', 'cancelled'
        private String meetingLink;
        private String location;
    }
}

