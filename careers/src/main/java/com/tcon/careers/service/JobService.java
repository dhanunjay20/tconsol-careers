package com.tcon.careers.service;

import com.tcon.careers.dto.PageResponse;
import com.tcon.careers.model.Job;
import com.tcon.careers.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    public PageResponse<Job> getAllActiveJobs(String department, String location, String type,
                                              String experience, String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "postedDate"));
        Page<Job> jobPage;

        if (search != null && !search.isEmpty()) {
            jobPage = jobRepository.searchActiveJobs(search, pageable);
        } else if (department != null && !department.isEmpty()) {
            jobPage = jobRepository.findByIsActiveTrueAndDepartment(department, pageable);
        } else if (location != null && !location.isEmpty()) {
            jobPage = jobRepository.findByIsActiveTrueAndLocation(location, pageable);
        } else if (type != null && !type.isEmpty()) {
            jobPage = jobRepository.findByIsActiveTrueAndType(type, pageable);
        } else if (experience != null && !experience.isEmpty()) {
            jobPage = jobRepository.findByIsActiveTrueAndExperience(experience, pageable);
        } else {
            jobPage = jobRepository.findByIsActiveTrue(pageable);
        }

        return mapToPageResponse(jobPage);
    }

    public Job getJobById(String id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));
    }

    public Job createJob(Job job) {
        job.setPostedDate(LocalDateTime.now());
        job.setCreatedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());
        job.setIsActive(true);

        Job savedJob = jobRepository.save(job);
        log.info("Job created successfully with id: {}", savedJob.getId());
        return savedJob;
    }

    public Job updateJob(String id, Job jobUpdate) {
        Job existingJob = getJobById(id);

        existingJob.setTitle(jobUpdate.getTitle());
        existingJob.setDepartment(jobUpdate.getDepartment());
        existingJob.setLocation(jobUpdate.getLocation());
        existingJob.setType(jobUpdate.getType());
        existingJob.setSalary(jobUpdate.getSalary());
        existingJob.setExperience(jobUpdate.getExperience());
        existingJob.setDescription(jobUpdate.getDescription());
        existingJob.setRequirements(jobUpdate.getRequirements());
        existingJob.setResponsibilities(jobUpdate.getResponsibilities());
        existingJob.setColor(jobUpdate.getColor());
        existingJob.setClosingDate(jobUpdate.getClosingDate());
        existingJob.setUpdatedAt(LocalDateTime.now());

        Job updated = jobRepository.save(existingJob);
        log.info("Job updated successfully with id: {}", id);
        return updated;
    }

    public void deleteJob(String id) {
        Job job = getJobById(id);
        job.setIsActive(false);
        job.setUpdatedAt(LocalDateTime.now());
        jobRepository.save(job);
        log.info("Job soft deleted with id: {}", id);
    }

    public Map<String, Object> getJobStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalActiveJobs", jobRepository.countByIsActiveTrue());
        stats.put("totalJobs", jobRepository.count());

        // Stats by department
        Map<String, Long> byDepartment = new HashMap<>();
        byDepartment.put("Engineering", jobRepository.countByDepartment("Engineering"));
        byDepartment.put("Design", jobRepository.countByDepartment("Design"));
        byDepartment.put("Management", jobRepository.countByDepartment("Management"));
        stats.put("byDepartment", byDepartment);

        // Stats by location
        Map<String, Long> byLocation = new HashMap<>();
        byLocation.put("Remote", jobRepository.countByLocation("Remote"));
        byLocation.put("Hybrid", jobRepository.countByLocation("Hybrid"));
        byLocation.put("On-site", jobRepository.countByLocation("On-site"));
        stats.put("byLocation", byLocation);

        // Stats by type
        Map<String, Long> byType = new HashMap<>();
        byType.put("Full-time", jobRepository.countByType("Full-time"));
        byType.put("Part-time", jobRepository.countByType("Part-time"));
        byType.put("Contract", jobRepository.countByType("Contract"));
        stats.put("byType", byType);

        return stats;
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

