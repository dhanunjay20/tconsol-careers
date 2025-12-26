package com.tcon.careers.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "jobs")
public class Job {

    @Id
    private String id;

    @NotBlank(message = "Title is required")
    @Indexed
    private String title;

    @NotBlank(message = "Department is required")
    private String department; // 'Engineering', 'Design', 'Management'

    @NotBlank(message = "Location is required")
    private String location; // 'Remote', 'Hybrid', 'On-site'

    @NotBlank(message = "Type is required")
    private String type; // 'Full-time', 'Part-time', 'Contract'

    @NotBlank(message = "Salary is required")
    private String salary;

    @NotBlank(message = "Experience level is required")
    private String experience; // 'Junior', 'Mid-Level', 'Senior'

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Requirements are required")
    private List<String> requirements;

    @NotNull(message = "Responsibilities are required")
    private List<String> responsibilities;

    private String color; // gradient color class

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private LocalDateTime postedDate = LocalDateTime.now();

    private LocalDateTime closingDate;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}

