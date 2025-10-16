package com.Ayan.Mondal.VOTEONN.REPOSITORY;

import com.Ayan.Mondal.VOTEONN.MODEL.VoterDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VoterDetailsRepository extends JpaRepository<VoterDetails, Long> {
    Optional<VoterDetails> findByVoterId(String voterId);
    Optional<VoterDetails> findByUserId(Long userId);
    Optional<VoterDetails> findBySecretPin(String secretPin);
    Optional<VoterDetails> findByVoterIdAndSecretPin(String voterId, String secretPin);
}
