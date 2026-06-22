package com.Ayan.Mondal.VOTEONN.CONTROLLER;

import com.Ayan.Mondal.VOTEONN.CONFIG.JwtService;
import com.Ayan.Mondal.VOTEONN.DTO.LoginDTO;
import com.Ayan.Mondal.VOTEONN.DTO.UserDTO;
import com.Ayan.Mondal.VOTEONN.MODEL.UserEntity;
import com.Ayan.Mondal.VOTEONN.REPOSITORY.UserRepository;
import com.Ayan.Mondal.VOTEONN.SERVICE.CaptchaService;
import com.Ayan.Mondal.VOTEONN.SERVICE.GoogleAuthService;
import com.Ayan.Mondal.VOTEONN.SERVICE.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired private UserService userService;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtService jwtService;
    @Autowired private CaptchaService captchaService;
    @Autowired private GoogleAuthService googleAuthService;

    // ── Public Registration (first-admin bootstrap handled in service) ────────
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO user) {
        try {
            return ResponseEntity.ok(Map.of("message", userService.registerAsUser(user)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // ── NEW: Dynamic Custom CAPTCHA Endpoint ─────────────────────────────────
    @GetMapping("/captcha")
    public ResponseEntity<?> getCaptcha() {
        return ResponseEntity.ok(captchaService.generateCaptcha());
    }

    // ── Login (email + password + Custom Alphanumeric CAPTCHA Validation) ─────
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDTO dto) {

        // Validating the internal security layer challenge
        if (!captchaService.verifyCaptcha(dto.getCaptchaSessionId(), dto.getCaptchaAnswer())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "status",  HttpStatus.FORBIDDEN.value(),
                            "message", "Security check failed. Incorrect captcha input."
                    ));
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
            );

            org.springframework.security.core.userdetails.UserDetails userDetails =
                    (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();

            String token = jwtService.generateToken(userDetails);

            // Extract the role (strip "ROLE_" prefix added by UserDetailsServiceImpl)
            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(a -> a.getAuthority().replace("ROLE_", ""))
                    .orElse("USER");

            return ResponseEntity.ok(Map.of("token", token, "role", role));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status",  HttpStatus.UNAUTHORIZED.value(),
                            "message", "Invalid email or password"
                    ));
        }
    }

    @PostMapping("/auth/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> body) {
        String credential = body.get("credential");

        try {
            Map<String, String> result = googleAuthService.authenticateWithGoogle(credential);
            return ResponseEntity.ok(result);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Google verification failed: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal auth system handling processing mistake: " + e.getMessage()));
        }
    }

    @GetMapping("/all/user")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUser());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  ADMIN MANAGEMENT ENDPOINTS (ROLE_ADMIN required — secured via SecurityConfig)
    // ══════════════════════════════════════════════════════════════════════════

    /** Promote an existing registered user to ADMIN */
    @PostMapping("/admin/promote")
    public ResponseEntity<?> promoteToAdmin(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email is required."));
        }
        try {
            String result = userService.promoteToAdmin(email.trim());
            return ResponseEntity.ok(Map.of("message", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** Demote an admin back to normal user */
    @PostMapping("/admin/demote")
    public ResponseEntity<?> demoteToUser(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email is required."));
        }
        try {
            String result = userService.demoteToUser(email.trim());
            return ResponseEntity.ok(Map.of("message", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** Create a brand-new admin user from scratch */
    @PostMapping("/admin/create")
    public ResponseEntity<?> createAdmin(@RequestBody UserDTO user) {
        try {
            String result = userService.createAdminUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /** Get all current admin users (for admin management table) */
    @GetMapping("/admin/list")
    public ResponseEntity<?> getAllAdmins() {
        List<UserEntity> admins = userService.getAllAdmins();
        // Return only safe fields (no passwords)
        List<Map<String, Object>> safeList = admins.stream().map(a -> Map.<String, Object>of(
                "id", a.getId(),
                "name", a.getName() != null ? a.getName() : "N/A",
                "email", a.getEmail(),
                "phone", a.getPhone() != null ? a.getPhone() : "N/A"
        )).collect(Collectors.toList());
        return ResponseEntity.ok(safeList);
    }
}