package com.Ayan.Mondal.VOTEONN.CONTROLLER;

import com.Ayan.Mondal.VOTEONN.DTO.EmailRequest;
import com.Ayan.Mondal.VOTEONN.DTO.PasswordResetRequest;
import com.Ayan.Mondal.VOTEONN.DTO.VerifyOtpRequest;
import com.Ayan.Mondal.VOTEONN.MODEL.UserEntity;
import com.Ayan.Mondal.VOTEONN.REPOSITORY.UserRepository;
import com.Ayan.Mondal.VOTEONN.SERVICE.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/forgot-password")

public class ForgotPasswordController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // STEP 1: Check if email exists, then trigger your existing OTP generation & mailing
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody EmailRequest request) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Email not registered in our database.");
        }

        UserEntity user = userOpt.get();
        if ("GOOGLE".equals(user.getAuthProvider())) {
            return ResponseEntity.badRequest().body("This account uses Google Login. Please sign in with Google.");
        }

        try {
            // Reusing your existing method from OtpService directly
            otpService.sendOtpToEmail(request.getEmail());
            return ResponseEntity.ok("OTP sent successfully to your email.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error sending email. Please try again.");
        }
    }

    // STEP 2: Verify the OTP using your existing OtpService logic
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest request) {
        // Reusing your existing verification method
        boolean isValid = otpService.verifyOtp(request.getEmail(), request.getOtp());

        if (isValid) {
            return ResponseEntity.ok("OTP verified successfully.");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired OTP code.");
        }
    }

    // STEP 3: Directly update the database with the newly encoded password
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User no longer found.");
        }

        try {
            UserEntity user = userOpt.get();
            // Securely encode the new password and update the user record
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            return ResponseEntity.ok("Password updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to update password in database.");
        }
    }
}
