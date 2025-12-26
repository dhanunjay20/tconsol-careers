package com.tcon.careers.repository;

import com.tcon.careers.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends MongoRepository<Job, String> {

    Page<Job> findByIsActiveTrue(Pageable pageable);

    Page<Job> findByIsActiveTrueAndDepartment(String department, Pageable pageable);

    Page<Job> findByIsActiveTrueAndLocation(String location, Pageable pageable);

    Page<Job> findByIsActiveTrueAndType(String type, Pageable pageable);

    Page<Job> findByIsActiveTrueAndExperience(String experience, Pageable pageable);

    @Query("{ 'isActive': true, $or: [ { 'title': { $regex: ?0, $options: 'i' } }, { 'description': { $regex: ?0, $options: 'i' } } ] }")
    Page<Job> searchActiveJobs(String search, Pageable pageable);

    @Query("{ 'isActive': true, 'department': { $in: ?0 } }")
    Page<Job> findByIsActiveTrueAndDepartmentIn(List<String> departments, Pageable pageable);

    @Query("{ 'isActive': true, 'location': { $in: ?0 } }")
    Page<Job> findByIsActiveTrueAndLocationIn(List<String> locations, Pageable pageable);

    List<Job> findByDepartment(String department);

    long countByIsActiveTrue();

    long countByDepartment(String department);

    long countByLocation(String location);

    long countByType(String type);
}

