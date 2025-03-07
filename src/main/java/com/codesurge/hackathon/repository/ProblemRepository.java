package com.codesurge.hackathon.repository;

import com.codesurge.hackathon.model.Problem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProblemRepository extends MongoRepository<Problem, String> {

    /**
     * Find problem by its title
     */
    Optional<Problem> findByTitle(String title);

    /**
     * Find problems by track
     */
    List<Problem> findByTrack(String track);

    /**
     * Find problems by release date ordered by deadline
     */
    List<Problem> findByReleaseDateBeforeOrderByDeadline(java.time.LocalDateTime date);

    /**
     * Check if problem exists by title
     */
    boolean existsByTitle(String title);

    /**
     * Delete problem by title
     */
    void deleteByTitle(String title);
}