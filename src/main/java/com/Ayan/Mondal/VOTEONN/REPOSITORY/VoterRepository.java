package com.Ayan.Mondal.VOTEONN.REPOSITORY;

import com.Ayan.Mondal.VOTEONN.MODEL.UserFaceEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoterRepository extends JpaRepository<UserFaceEntity, Long> {
    Optional<UserFaceEntity> findByEncryptedVoterId(String encryptedVoterId);
}

