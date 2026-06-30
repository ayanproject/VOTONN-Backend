package com.Ayan.Mondal.VOTEONN.SERVICE;

import com.Ayan.Mondal.VOTEONN.DTO.UserDTO;
import com.Ayan.Mondal.VOTEONN.MODEL.UserEntity;
import com.Ayan.Mondal.VOTEONN.REPOSITORY.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ── Public Registration ──────────────────────────────────────────────────
    // First-admin bootstrap: if DB is empty, first user becomes ADMIN.
    // All subsequent public registrations are always USER.
    @Transactional
    public String registerAsUser(UserDTO user) {
        if (userRepository.findByEmailIgnoreCase(user.getEmail().trim()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }

        UserEntity entity = new UserEntity();
        entity.setAge(user.getAge());
        entity.setEmail(user.getEmail());
        entity.setGender(user.getGender());
        entity.setName(user.getName());
        entity.setPassword(passwordEncoder.encode(user.getPassword()));
        entity.setPhone(user.getPhone());

        // First-admin bootstrap: if no users exist, make this user ADMIN
        if (userRepository.count() == 0) {
            entity.setRole("ADMIN");
            userRepository.save(entity);
            return "First admin registered successfully. You are the system administrator.";
        }

        entity.setRole("USER");
        userRepository.save(entity);
        return "User registered successfully";
    }

    // ── Admin Management (only callable by existing admins) ──────────────────

    /** Promote an existing registered user to ADMIN role */
    @Transactional
    public String promoteToAdmin(String email) {
        UserEntity user = userRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new IllegalArgumentException("No user found with email: " + email));

        if ("ADMIN".equals(user.getRole())) {
            throw new IllegalArgumentException("User is already an admin: " + email);
        }

        user.setRole("ADMIN");
        userRepository.save(user);
        return "User promoted to admin successfully: " + email;
    }

    /** Demote an existing admin back to USER role */
    @Transactional
    public String demoteToUser(String email) {
        UserEntity user = userRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new IllegalArgumentException("No user found with email: " + email));

        if ("USER".equals(user.getRole())) {
            throw new IllegalArgumentException("User is already a normal user: " + email);
        }

        // Safety: prevent removing the last admin
        long adminCount = userRepository.findByRole("ADMIN").size();
        if (adminCount <= 1) {
            throw new IllegalArgumentException("Cannot remove the last admin. At least one admin must exist.");
        }

        user.setRole("USER");
        userRepository.save(user);
        return "Admin access removed for: " + email;
    }

    /** Create a brand-new user directly as ADMIN (admin-only action) */
    @Transactional
    public String createAdminUser(UserDTO user) {
        if (userRepository.findByEmailIgnoreCase(user.getEmail().trim()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }

        UserEntity entity = new UserEntity();
        entity.setAge(user.getAge());
        entity.setRole("ADMIN");
        entity.setEmail(user.getEmail());
        entity.setGender(user.getGender());
        entity.setName(user.getName());
        entity.setPassword(passwordEncoder.encode(user.getPassword()));
        entity.setPhone(user.getPhone());

        userRepository.save(entity);
        return "New admin created successfully: " + user.getEmail();
    }

    /** Get all admin users */
    public List<UserEntity> getAllAdmins() {
        return userRepository.findByRole("ADMIN");
    }

    /** Get all users */
    public List<UserEntity> getAllUser() {
        return userRepository.findAll();
    }
}
