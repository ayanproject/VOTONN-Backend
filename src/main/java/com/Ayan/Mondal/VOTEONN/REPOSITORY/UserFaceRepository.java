package com.Ayan.Mondal.VOTEONN.REPOSITORY;

import com.Ayan.Mondal.VOTEONN.MODEL.UserFaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserFaceRepository extends JpaRepository<UserFaceEntity, Long> {
    // ✅ This one is GOOD (matches "encryptedVoterId" field)
    Optional<UserFaceEntity> findByEncryptedVoterId(String encryptedVoterId);

    // ✅ This one is GOOD (matches "encryptedSecretPin" field)
    Optional<UserFaceEntity> findByEncryptedSecretPin(String encryptedSecretPin);

    // ✅ This one is GOOD (matches "email" field)
    Optional<UserFaceEntity> findByEmail(String email);
}
