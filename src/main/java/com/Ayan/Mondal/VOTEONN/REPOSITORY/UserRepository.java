package com.Ayan.Mondal.VOTEONN.REPOSITORY;

import com.Ayan.Mondal.VOTEONN.MODEL.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByEmailIgnoreCase(String email);
    List<UserEntity> findByRole(String role);
}