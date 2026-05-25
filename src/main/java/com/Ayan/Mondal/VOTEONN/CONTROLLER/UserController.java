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
    @Autowired private UserRepository userRepository;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtService jwtService;

    // ── NEW: Inject the two new services ──────────────────────────────────────
    @Autowired private CaptchaService captchaService;
    @Autowired private GoogleAuthService googleAuthService;
    // ─────────────────────────────────────────────────────────────────────────

    // ── Register user ─────────────────────────────────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO user) {
        return ResponseEntity.ok(userService.registerAsUser(user));
    }

    // ── Register admin ────────────────────────────────────────────────────────
    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@RequestBody UserDTO user) {
        return ResponseEntity.ok(userService.registerAsAdmin(user));
    }

    // ── Login (email + password + reCAPTCHA v3) ───────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDTO dto) {

        // 1. Verify reCAPTCHA v3 token FIRST
        //    If the score is too low or the token is missing, reject the request.
        if (!captchaService.verifyCaptcha(dto.getCaptchaToken())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "status",  HttpStatus.FORBIDDEN.value(),
                            "message", "Security check failed. Please reload the page and try again."
                    ));
        }

        // 2. Authenticate credentials
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
            );

            org.springframework.security.core.userdetails.UserDetails userDetails =
                    (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();

            String token = jwtService.generateToken(userDetails);
            return ResponseEntity.ok(Map.of("token", token));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status",  HttpStatus.UNAUTHORIZED.value(),
                            "message", "Invalid email or password"
                    ));
        }
    }

    // ── Google OAuth2 login ───────────────────────────────────────────────────
    /**
     * Called after the user completes Google's consent screen on the frontend.
     * Receives the raw Google ID token (credential), verifies it,
     * then returns a standard app JWT — exactly like the /login endpoint.
     */
    @PostMapping("/auth/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> body) {
        String credential = body.get("credential");

        try {
            Map<String, String> result = googleAuthService.authenticateWithGoogle(credential);
            return ResponseEntity.ok(result);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Google authentication failed: invalid token."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Google authentication error: " + e.getMessage()));
        }
    }

    // ── Get all users (protected) ─────────────────────────────────────────────
    @GetMapping("/all/user")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUser());
    }
}