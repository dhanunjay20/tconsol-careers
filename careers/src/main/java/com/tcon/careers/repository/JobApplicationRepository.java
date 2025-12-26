package com.tcon.careers.repository;

import com.tcon.careers.model.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends MongoRepository<JobApplication, String> {

    Optional<JobApplication> findByApplicationId(String applicationId);

    Page<JobApplication> findByJobId(String jobId, Pageable pageable);

    Page<JobApplication> findByStatus(String status, Pageable pageable);

    Page<JobApplication> findByDepartment(String department, Pageable pageable);

    Page<JobApplication> findByApplicationDateBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    @Query("{ $or: [ { 'firstName': { $regex: ?0, $options: 'i' } }, { 'lastName': { $regex: ?0, $options: 'i' } }, { 'email': { $regex: ?0, $options: 'i' } } ] }")
    Page<JobApplication> searchApplications(String search, Pageable pageable);

    @Query("{ 'email': ?0, 'applicationDate': { $gte: ?1 } }")
    List<JobApplication> findRecentApplicationsByEmail(String email, LocalDateTime since);

    long countByJobId(String jobId);

    long countByStatus(String status);

    long countByDepartment(String department);

    long countByApplicationDateBetween(LocalDateTime from, LocalDateTime to);

    List<JobApplication> findTop10ByOrderByApplicationDateDesc();

    @Query("{ 'status': { $in: ['submitted', 'screening'] } }")
    List<JobApplication> findPendingReviews();
}

