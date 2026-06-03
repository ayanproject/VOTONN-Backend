package com.Ayan.Mondal.VOTEONN.CONTROLLER;

import com.Ayan.Mondal.VOTEONN.CONFIG.JwtService;
import com.Ayan.Mondal.VOTEONN.DTO.LoginDTO;
import com.Ayan.Mondal.VOTEONN.DTO.UserDTO;
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

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired private UserService userService;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtService jwtService;
    @Autowired private CaptchaService captchaService;
    @Autowired private GoogleAuthService googleAuthService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO user) {
        return ResponseEntity.ok(userService.registerAsUser(user));
    }

    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@RequestBody UserDTO user) {
        return ResponseEntity.ok(userService.registerAsAdmin(user));
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
}