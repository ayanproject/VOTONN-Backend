package com.Ayan.Mondal.VOTEONN.CONTROLLER;

import com.Ayan.Mondal.VOTEONN.CONFIG.JwtService;
import com.Ayan.Mondal.VOTEONN.DTO.LoginDTO;
import com.Ayan.Mondal.VOTEONN.DTO.UserDTO;

import com.Ayan.Mondal.VOTEONN.REPOSITORY.UserRepository;
import com.Ayan.Mondal.VOTEONN.SERVICE.UserService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager; // <-- INJECT AuthManager

    @Autowired
    private JwtService jwtService; // <-- INJECT JwtService

    // Register user
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO user) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.registerAsUser(user));
    }

    // Register admin (This should be secured! Not in PUBLIC_API)
    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@RequestBody UserDTO user) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.registerAsAdmin(user));
    }

    // === THIS IS THE UPDATED LOGIN METHOD ===
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDTO dto) {
        try {
            // 1. Authenticate user AND capture the fully authenticated object
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
            );

            // 2. Get the REAL authenticated user details from the object
            // This is the 'UserDetails' object you created in UserDetailsServiceImpl
            org.springframework.security.core.userdetails.UserDetails userDetails =
                    (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();

            // 3. Now, generate a token using the REAL user details
            // This will correctly include any roles (like "USER" or "ADMIN")
            String token = jwtService.generateToken(userDetails);

            // 4. Return the token
            return ResponseEntity.ok(Map.of("token", token));

        } catch (AuthenticationException e) {
            // This now sends a proper JSON object, which your JavaScript will understand
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", HttpStatus.UNAUTHORIZED.value(),
                            "message", "Invalid email or password"
                    ));
        }
    }
    // ===========================================

    // Get all users (This is now PROTECTED)
    @GetMapping("/all/user")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUser());
    }
}