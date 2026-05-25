package com.Ayan.Mondal.VOTEONN.REPOSITORY;

import com.Ayan.Mondal.VOTEONN.MODEL.DeletionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeletionRequestRepository extends JpaRepository<DeletionRequest, Long> {

    // Find all deletion requests for a voter ID
    List<DeletionRequest> findByVoterId(String voterId);

    // Find by status — for admin review
    List<DeletionRequest> findByStatus(String status);

    // Check if a voter already has a pending deletion request (prevent duplicates)
    boolean existsByVoterIdAndStatus(String voterId, String status);
}