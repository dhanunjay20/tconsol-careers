package com.tcon.careers.controller;

import com.tcon.careers.dto.*;
import com.tcon.careers.model.JobApplication;
import com.tcon.careers.service.JobApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Job Applications", description = "Job application management")
public class JobApplicationController {

    private final JobApplicationService applicationService;

    @PostMapping(value = "/applications", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Submit job application", description = "Submit a new job application with resume upload")
    public ResponseEntity<ApiResponse<JobApplication>> submitApplication(
            @RequestPart("application") @Valid JobApplicationRequest applicationRequest,
            @RequestPart("resume") MultipartFile resume,
            HttpServletRequest request
    ) {
        try {
            JobApplication submitted = applicationService.submitApplication(applicationRequest, resume, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Application submitted successfully", submitted));
        } catch (Exception e) {
            log.error("Error submitting application: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to submit application: " + e.getMessage()));
        }
    }

    @GetMapping("/applications/track/{applicationId}")
    @Operation(summary = "Track application", description = "Track application status by application ID (public endpoint)")
    public ResponseEntity<ApiResponse<Map<String, String>>> trackApplication(
            @PathVariable String applicationId
    ) {
        try {
            Map<String, String> trackingInfo = applicationService.trackApplication(applicationId);
            return ResponseEntity.ok(ApiResponse.success(trackingInfo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/admin/applications")
    // @PreAuthorize("hasRole('ADMIN')") // TODO: Enable for production
    @Operation(summary = "Get all applications", description = "Fetch all applications with filters (Admin only)")
    public ResponseEntity<ApiResponse<PageResponse<JobApplication>>> getAllApplications(
            @RequestParam(required = false) String jobId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<JobApplication> applications = applicationService.getAllApplications(
                jobId, status, department, dateFrom, dateTo, search, page, size
        );
        return ResponseEntity.ok(ApiResponse.success(applications));
    }

    @GetMapping("/admin/applications/{id}")
    // @PreAuthorize("hasRole('ADMIN')") // TODO: Enable for production
    @Operation(summary = "Get application by ID", description = "Fetch a single application by ID (Admin only)")
    public ResponseEntity<ApiResponse<JobApplication>> getApplicationById(@PathVariable String id) {
        try {
            JobApplication application = applicationService.getApplicationById(id);
            return ResponseEntity.ok(ApiResponse.success(application));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/admin/applications/{id}/status")
    // @PreAuthorize("hasRole('ADMIN')") // TODO: Enable for production
    @Operation(summary = "Update application status", description = "Update the status of an application (Admin only)")
    public ResponseEntity<ApiResponse<JobApplication>> updateApplicationStatus(
            @PathVariable String id,
            @Valid @RequestBody StatusUpdateRequest statusUpdate
    ) {
        try {
            JobApplication updated = applicationService.updateApplicationStatus(id, statusUpdate);
            return ResponseEntity.ok(ApiResponse.success("Status updated successfully", updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/admin/applications/{id}/notes")
    // @PreAuthorize("hasRole('ADMIN')") // TODO: Enable for production
    @Operation(summary = "Add admin note", description = "Add a note to an application (Admin only)")
    public ResponseEntity<ApiResponse<JobApplication>> addAdminNote(
            @PathVariable String id,
            @Valid @RequestBody AdminNoteRequest noteRequest
    ) {
        try {
            JobApplication updated = applicationService.addAdminNote(id, noteRequest.getNote());
            return ResponseEntity.ok(ApiResponse.success("Note added successfully", updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/admin/applications/{id}/interview")
    // @PreAuthorize("hasRole('ADMIN')") // TODO: Enable for production
    @Operation(summary = "Schedule interview", description = "Schedule an interview for an application (Admin only)")
    public ResponseEntity<ApiResponse<JobApplication>> scheduleInterview(
            @PathVariable String id,
            @Valid @RequestBody InterviewScheduleRequest interviewRequest
    ) {
        try {
            JobApplication updated = applicationService.scheduleInterview(id, interviewRequest);
            return ResponseEntity.ok(ApiResponse.success("Interview scheduled successfully", updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/admin/applications/{id}")
    // @PreAuthorize("hasRole('ADMIN')") // TODO: Enable for production
    @Operation(summary = "Delete application", description = "Delete an application and associated files (Admin only)")
    public ResponseEntity<ApiResponse<String>> deleteApplication(@PathVariable String id) {
        try {
            applicationService.deleteApplication(id);
            return ResponseEntity.ok(ApiResponse.success("Application deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/admin/dashboard/stats")
    // @PreAuthorize("hasRole('ADMIN')") // TODO: Enable for production
    @Operation(summary = "Get dashboard statistics", description = "Get application statistics for admin dashboard (Admin only)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats() {
        Map<String, Object> stats = applicationService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/admin/dashboard/recent-applications")
    // @PreAuthorize("hasRole('ADMIN')") // TODO: Enable for production
    @Operation(summary = "Get recent applications", description = "Get the 10 most recent applications (Admin only)")
    public ResponseEntity<ApiResponse<List<JobApplication>>> getRecentApplications() {
        List<JobApplication> applications = applicationService.getRecentApplications();
        return ResponseEntity.ok(ApiResponse.success(applications));
    }

    @GetMapping("/admin/dashboard/pending-reviews")
    // @PreAuthorize("hasRole('ADMIN')") // TODO: Enable for production
    @Operation(summary = "Get pending reviews", description = "Get applications pending review (Admin only)")
    public ResponseEntity<ApiResponse<List<JobApplication>>> getPendingReviews() {
        List<JobApplication> applications = applicationService.getPendingReviews();
        return ResponseEntity.ok(ApiResponse.success(applications));
    }
}
