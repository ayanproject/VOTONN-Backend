package com.Ayan.Mondal.VOTEONN.REPOSITORY;

import com.Ayan.Mondal.VOTEONN.MODEL.CorrectionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorrectionRequestRepository extends JpaRepository<CorrectionRequest, Long> {

    // Find all requests for a specific voter (useful for admin dashboard later)
    List<CorrectionRequest> findByVoterId(String voterId);

    // Find by status — e.g. all PENDING for admin review
    List<CorrectionRequest> findByStatus(String status);
}