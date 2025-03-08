package com.codesurge.hackathon.repository;

import com.codesurge.hackathon.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByTeamName(String teamName);
    // Optional<List<User>> findByTeamNameList(String teamName);
    List<User> findByTeamNameIn(List<String> teamNames);
    boolean existsByTeamName(String teamName);
    boolean existsByUsername(String username);
    long countByTeamNameIsNotNull();
    List<User> findByAssignedProblemId(String problemId);
}