package com.Ayan.Mondal.VOTEONN.REPOSITORY;

import com.Ayan.Mondal.VOTEONN.MODEL.UserFaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserFaceRepository extends JpaRepository<UserFaceEntity, Long> {
    Optional<UserFaceEntity> findByVoter_VoterId(String voterId); // find face by voterId (String)
    Optional<UserFaceEntity> findByVoterId(Long voterId);          // find face by voter PK
}
