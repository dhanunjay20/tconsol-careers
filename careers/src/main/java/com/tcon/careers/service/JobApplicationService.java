package com.tcon.careers.service;

import com.tcon.careers.dto.InterviewScheduleRequest;
import com.tcon.careers.dto.JobApplicationRequest;
import com.tcon.careers.dto.PageResponse;
import com.tcon.careers.dto.StatusUpdateRequest;
import com.tcon.careers.model.Job;
import com.tcon.careers.model.JobApplication;
import com.tcon.careers.repository.JobApplicationRepository;
import com.tcon.careers.repository.JobRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final FileStorageService fileStorageService;
    private final EmailService emailService;
    private final RateLimitService rateLimitService;

    @Value("${app.rate-limit.enabled}")
    private boolean rateLimitEnabled;

    public JobApplication submitApplication(JobApplicationRequest request, MultipartFile resume,
                                           HttpServletRequest httpRequest) throws IOException {
        // Rate limiting
        if (rateLimitEnabled) {
            rateLimitService.checkRateLimit(request.getEmail());
        }

        // Get job details
        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + request.getJobId()));

        if (!job.getIsActive()) {
            throw new RuntimeException("This job posting is no longer active");
        }

        // Upload resume
        String resumeFileName = fileStorageService.uploadFile(resume, request.getJobId());
        String resumeUrl = fileStorageService.getFileUrl(resumeFileName);

        // Map DTO to Model
        JobApplication application = JobApplication.builder()
                .jobId(request.getJobId())
                .jobTitle(job.getTitle())
                .department(job.getDepartment())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .linkedinUrl(request.getLinkedinUrl())
                .portfolioUrl(request.getPortfolioUrl())
                .githubUrl(request.getGithubUrl())
                .currentLocation(request.getCurrentLocation())
                .willingToRelocate(request.getWillingToRelocate())
                .yearsOfExperience(request.getYearsOfExperience())
                .currentRole(request.getCurrentRole())
                .currentCompany(request.getCurrentCompany())
                .noticePeriod(request.getNoticePeriod())
                .expectedSalary(request.getExpectedSalary())
                .coverLetter(request.getCoverLetter())
                .referralSource(request.getReferralSource())
                .referralName(request.getReferralName())
                .additionalComments(request.getAdditionalComments())
                .skills(request.getSkills())
                .certifications(request.getCertifications())
                .education(request.getEducation())
                .resumeUrl(resumeUrl)
                .resumeFileName(resume.getOriginalFilename())
                .resumeFileSize(resume.getSize())
                .status("submitted")
                .applicationDate(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .ipAddress(getClientIP(httpRequest))
                .userAgent(httpRequest.getHeader("User-Agent"))
                .statusHistory(new ArrayList<>())
                .adminNotes(new ArrayList<>())
                .interviewSchedule(new ArrayList<>())
                .build();

        // Add initial status history
        JobApplication.StatusHistory initialStatus = JobApplication.StatusHistory.builder()
                .status("submitted")
                .changedBy("System")
                .changedAt(LocalDateTime.now())
                .notes("Application submitted")
                .build();
        application.getStatusHistory().add(initialStatus);

        // Save application
        JobApplication savedApplication = applicationRepository.save(application);
        log.info("Application submitted successfully with id: {}", savedApplication.getApplicationId());

        // Send emails
        emailService.sendApplicationConfirmation(
                application.getEmail(),
                application.getFirstName() + " " + application.getLastName(),
                job.getTitle(),
                savedApplication.getApplicationId()
        );

        emailService.sendNewApplicationNotification(
                job.getTitle(),
                application.getFirstName() + " " + application.getLastName(),
                savedApplication.getApplicationId()
        );

        return savedApplication;
    }

    public PageResponse<JobApplication> getAllApplications(String jobId, String status, String department,
                                                           LocalDateTime dateFrom, LocalDateTime dateTo,
                                                           String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "applicationDate"));
        Page<JobApplication> applicationPage;

        if (search != null && !search.isEmpty()) {
            applicationPage = applicationRepository.searchApplications(search, pageable);
        } else if (jobId != null && !jobId.isEmpty()) {
            applicationPage = applicationRepository.findByJobId(jobId, pageable);
        } else if (status != null && !status.isEmpty()) {
            applicationPage = applicationRepository.findByStatus(status, pageable);
        } else if (department != null && !department.isEmpty()) {
            applicationPage = applicationRepository.findByDepartment(department, pageable);
        } else if (dateFrom != null && dateTo != null) {
            applicationPage = applicationRepository.findByApplicationDateBetween(dateFrom, dateTo, pageable);
        } else {
            applicationPage = applicationRepository.findAll(pageable);
        }

        return mapToPageResponse(applicationPage);
    }

    public JobApplication getApplicationById(String id) {
        return applicationRepository.findByApplicationId(id)
                .orElseThrow(() -> new RuntimeException("Application not found with id: " + id));
    }

    public JobApplication updateApplicationStatus(String id, StatusUpdateRequest statusUpdate) {
        JobApplication application = getApplicationById(id);
        String currentUser = getCurrentUsername();

        // Update status
        application.setStatus(statusUpdate.getStatus());
        application.setLastUpdated(LocalDateTime.now());

        // Add to status history
        JobApplication.StatusHistory statusHistory = JobApplication.StatusHistory.builder()
                .status(statusUpdate.getStatus())
                .changedBy(currentUser)
                .changedAt(LocalDateTime.now())
                .notes(statusUpdate.getNotes())
                .build();
        application.getStatusHistory().add(statusHistory);

        JobApplication updated = applicationRepository.save(application);
        log.info("Application status updated to {} for id: {}", statusUpdate.getStatus(), id);

        // Send status update email
        emailService.sendStatusUpdateEmail(
                application.getEmail(),
                application.getFirstName() + " " + application.getLastName(),
                application.getJobTitle(),
                statusUpdate.getStatus(),
                statusUpdate.getNotes()
        );

        // Send specific emails based on status
        if ("rejected".equals(statusUpdate.getStatus())) {
            emailService.sendRejectionEmail(
                    application.getEmail(),
                    application.getFirstName() + " " + application.getLastName(),
                    application.getJobTitle()
            );
        } else if ("offer-extended".equals(statusUpdate.getStatus())) {
            emailService.sendOfferEmail(
                    application.getEmail(),
                    application.getFirstName() + " " + application.getLastName(),
                    application.getJobTitle()
            );
        }

        return updated;
    }

    public JobApplication addAdminNote(String id, String note) {
        JobApplication application = getApplicationById(id);
        String currentUser = getCurrentUsername();

        JobApplication.AdminNote adminNote = JobApplication.AdminNote.builder()
                .note(note)
                .addedBy(currentUser)
                .addedAt(LocalDateTime.now())
                .build();

        application.getAdminNotes().add(adminNote);
        application.setLastUpdated(LocalDateTime.now());

        JobApplication updated = applicationRepository.save(application);
        log.info("Admin note added to application: {}", id);

        return updated;
    }

    public JobApplication scheduleInterview(String id, InterviewScheduleRequest interviewRequest) {
        JobApplication application = getApplicationById(id);

        JobApplication.InterviewSchedule interview = JobApplication.InterviewSchedule.builder()
                .round(interviewRequest.getRound())
                .scheduledDate(interviewRequest.getScheduledDate())
                .interviewer(interviewRequest.getInterviewer())
                .type(interviewRequest.getType())
                .meetingLink(interviewRequest.getMeetingLink())
                .location(interviewRequest.getLocation())
                .status("scheduled")
                .build();

        application.getInterviewSchedule().add(interview);
        application.setStatus("interview-scheduled");
        application.setLastUpdated(LocalDateTime.now());

        // Add to status history
        JobApplication.StatusHistory statusHistory = JobApplication.StatusHistory.builder()
                .status("interview-scheduled")
                .changedBy(getCurrentUsername())
                .changedAt(LocalDateTime.now())
                .notes("Interview scheduled for round " + interviewRequest.getRound())
                .build();
        application.getStatusHistory().add(statusHistory);

        JobApplication updated = applicationRepository.save(application);
        log.info("Interview scheduled for application: {}", id);

        // Send interview invitation email
        emailService.sendInterviewInvitation(
                application.getEmail(),
                application.getFirstName() + " " + application.getLastName(),
                application.getJobTitle(),
                interviewRequest.getScheduledDate(),
                interviewRequest.getInterviewer(),
                interviewRequest.getType(),
                interviewRequest.getMeetingLink(),
                interviewRequest.getLocation()
        );

        return updated;
    }

    public Map<String, String> trackApplication(String applicationId) {
        JobApplication application = getApplicationById(applicationId);

        Map<String, String> trackingInfo = new HashMap<>();
        trackingInfo.put("applicationId", application.getApplicationId());
        trackingInfo.put("jobTitle", application.getJobTitle());
        trackingInfo.put("department", application.getDepartment());
        trackingInfo.put("status", application.getStatus());
        trackingInfo.put("applicationDate", application.getApplicationDate().toString());
        trackingInfo.put("lastUpdated", application.getLastUpdated().toString());

        return trackingInfo;
    }

    public void deleteApplication(String id) {
        JobApplication application = getApplicationById(id);

        // Delete resume from S3
        try {
            String fileName = application.getResumeUrl().substring(application.getResumeUrl().lastIndexOf("/") + 1);
            fileStorageService.deleteFile(fileName);
        } catch (Exception e) {
            log.error("Error deleting resume file: {}", e.getMessage());
        }

        // Delete application
        applicationRepository.delete(application);
        log.info("Application deleted: {}", id);
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalApplications", applicationRepository.count());

        // Stats by status
        Map<String, Long> byStatus = new HashMap<>();
        byStatus.put("submitted", applicationRepository.countByStatus("submitted"));
        byStatus.put("screening", applicationRepository.countByStatus("screening"));
        byStatus.put("interview-scheduled", applicationRepository.countByStatus("interview-scheduled"));
        byStatus.put("interview-completed", applicationRepository.countByStatus("interview-completed"));
        byStatus.put("rejected", applicationRepository.countByStatus("rejected"));
        byStatus.put("offer-extended", applicationRepository.countByStatus("offer-extended"));
        byStatus.put("hired", applicationRepository.countByStatus("hired"));
        stats.put("byStatus", byStatus);

        // Stats by department
        Map<String, Long> byDepartment = new HashMap<>();
        byDepartment.put("Engineering", applicationRepository.countByDepartment("Engineering"));
        byDepartment.put("Design", applicationRepository.countByDepartment("Design"));
        byDepartment.put("Management", applicationRepository.countByDepartment("Management"));
        stats.put("byDepartment", byDepartment);

        return stats;
    }

    public List<JobApplication> getRecentApplications() {
        return applicationRepository.findTop10ByOrderByApplicationDateDesc();
    }

    public List<JobApplication> getPendingReviews() {
        return applicationRepository.findPendingReviews();
    }

    private String getCurrentUsername() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception e) {
            return "System";
        }
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    private <T> PageResponse<T> mapToPageResponse(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .first(page.isFirst())
                .build();
    }
}
