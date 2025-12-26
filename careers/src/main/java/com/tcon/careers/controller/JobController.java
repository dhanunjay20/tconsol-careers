package com.tcon.careers.controller;

import com.tcon.careers.dto.ApiResponse;
import com.tcon.careers.dto.PageResponse;
import com.tcon.careers.model.Job;
import com.tcon.careers.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Jobs", description = "Job listings management")
public class JobController {

    private final JobService jobService;

    @GetMapping("/jobs")
    @Operation(summary = "Get all active job listings", description = "Fetch all active jobs with optional filters and pagination")
    public ResponseEntity<ApiResponse<PageResponse<Job>>> getAllJobs(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String experience,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        PageResponse<Job> jobs = jobService.getAllActiveJobs(department, location, type, experience, search, page, limit);
        return ResponseEntity.ok(ApiResponse.success(jobs));
    }

    @GetMapping("/jobs/{id}")
    @Operation(summary = "Get job by ID", description = "Fetch a single job by its ID")
    public ResponseEntity<ApiResponse<Job>> getJobById(@PathVariable String id) {
        try {
            Job job = jobService.getJobById(id);
            return ResponseEntity.ok(ApiResponse.success(job));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/jobs/stats")
    @Operation(summary = "Get job statistics", description = "Get aggregated statistics about jobs")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getJobStats() {
        Map<String, Object> stats = jobService.getJobStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @PostMapping("/admin/jobs")
    // @PreAuthorize("hasRole('ADMIN')") // TODO: Enable for production
    @Operation(summary = "Create new job", description = "Create a new job listing (Admin only)")
    public ResponseEntity<ApiResponse<Job>> createJob(@Valid @RequestBody Job job) {
        try {
            Job createdJob = jobService.createJob(job);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Job created successfully", createdJob));
        } catch (Exception e) {
            log.error("Error creating job: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create job: " + e.getMessage()));
        }
    }

    @PutMapping("/admin/jobs/{id}")
    // @PreAuthorize("hasRole('ADMIN')") // TODO: Enable for production
    @Operation(summary = "Update job", description = "Update an existing job listing (Admin only)")
    public ResponseEntity<ApiResponse<Job>> updateJob(
            @PathVariable String id,
            @Valid @RequestBody Job job
    ) {
        try {
            Job updatedJob = jobService.updateJob(id, job);
            return ResponseEntity.ok(ApiResponse.success("Job updated successfully", updatedJob));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/admin/jobs/{id}")
    // @PreAuthorize("hasRole('ADMIN')") // TODO: Enable for production
    @Operation(summary = "Delete job", description = "Soft delete a job listing (Admin only)")
    public ResponseEntity<ApiResponse<String>> deleteJob(@PathVariable String id) {
        try {
            jobService.deleteJob(id);
            return ResponseEntity.ok(ApiResponse.success("Job deleted successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
