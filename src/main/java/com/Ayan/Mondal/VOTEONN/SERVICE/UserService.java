package com.Ayan.Mondal.VOTEONN.SERVICE;

import com.Ayan.Mondal.VOTEONN.DTO.LoginDTO;
import com.Ayan.Mondal.VOTEONN.DTO.UserDTO;
import com.Ayan.Mondal.VOTEONN.MODEL.UserEntity;
import com.Ayan.Mondal.VOTEONN.REPOSITORY.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register as normal user
    @Transactional
    public String registerAsUser(UserDTO user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }
        UserEntity entity = new UserEntity();
        entity.setAge(user.getAge());
        entity.setRole("USER");
        entity.setEmail(user.getEmail());
        entity.setGender(user.getGender());
        entity.setName(user.getName());
        entity.setPassword(passwordEncoder.encode(user.getPassword()));
        entity.setPhone(user.getPhone());

        userRepository.save(entity);
        return "User registered successfully";
    }

    // Register as Admin
    @Transactional
    public String registerAsAdmin(UserDTO user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
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
        return "Admin registered successfully";
    }

    // Login Method
    public String loginUser(LoginDTO dto) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(dto.getEmail());
        if (userOpt.isEmpty()) {
            return "User not found with email: " + dto.getEmail();
        }

        UserEntity user = userOpt.get();

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return "Invalid password!";
        }

        return "Login successful for user: " + user.getName() + " (Role: " + user.getRole() + ")";
    }

    public List<UserEntity> getAllUser() {
        return userRepository.findAll();
    }
}
