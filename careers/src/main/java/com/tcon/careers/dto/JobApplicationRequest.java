package com.tcon.careers.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationRequest {

    @NotBlank(message = "Job ID is required")
    private String jobId;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    private String linkedinUrl;
    private String portfolioUrl;
    private String githubUrl;

    @NotBlank(message = "Current location is required")
    private String currentLocation;

    private Boolean willingToRelocate;

    @NotNull(message = "Years of experience is required")
    private Integer yearsOfExperience;

    @NotBlank(message = "Current role is required")
    private String currentRole;

    private String currentCompany;

    @NotBlank(message = "Notice period is required")
    private String noticePeriod;

    private String expectedSalary;

    private String coverLetter;

    private String referralSource;
    private String referralName;
    private String additionalComments;

    private List<String> skills;
    private List<String> certifications;

    @NotBlank(message = "Education is required")
    private String education;
}
