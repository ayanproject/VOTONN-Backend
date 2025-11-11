package com.Ayan.Mondal.VOTEONN.CONTROLLER;

import com.Ayan.Mondal.VOTEONN.DTO.OtpRequest;
import com.Ayan.Mondal.VOTEONN.DTO.VoterDto;
import com.Ayan.Mondal.VOTEONN.MODEL.UserFaceEntity;

import com.Ayan.Mondal.VOTEONN.REPOSITORY.UserFaceRepository;
import com.Ayan.Mondal.VOTEONN.SERVICE.EmailService;
import com.Ayan.Mondal.VOTEONN.SERVICE.FaceVerificationService;
import com.Ayan.Mondal.VOTEONN.SERVICE.FaceService;
import com.Ayan.Mondal.VOTEONN.SERVICE.OtpService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/api/voters")
@RequiredArgsConstructor
public class VoterCardController {

    @Autowired
    private OtpService otpService;

    @Autowired
    private UserFaceRepository userFaceRepository;
    @Autowired
    private final FaceService voterFaceService;
    @Autowired
    private final FaceVerificationService faceVerificationService;
    @Autowired
    private  final EmailService emailService;

    // Register voter with face
    @PostMapping("/register-with-face")
    public ResponseEntity<?> registerVoterWithFace(
            @RequestPart("voter") String voterJson,
            @RequestPart("userEmail") String email,
            @RequestPart("faceImage") MultipartFile faceImage) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            VoterDto voter = mapper.readValue(voterJson, VoterDto.class);
            byte[] faceBytes = faceImage.getBytes();

            UserFaceEntity savedVoter = voterFaceService.registerVoter(voter, faceBytes, email);
            return ResponseEntity.ok(savedVoter);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ Error processing request: " + e.getMessage());
        }
    }
    @PostMapping("/verify")
    public ResponseEntity<?> sendOtp(@RequestBody OtpRequest request) {
        try {
            // ✅ 1. Check if the user/email is valid first
            Optional<UserFaceEntity> userOptional = userFaceRepository.findByEmail(request.getEmail());

            if (userOptional.isEmpty()) {
                // 🛑 User does not exist. Do not send OTP.
                return ResponseEntity.badRequest().body("No user registered with this email.");
            }

            // ✅ 2. User exists. Get their authoritative email.
            UserFaceEntity user = userOptional.get();
            String authoritativeEmail = user.getEmail(); // Use the email from the DB

            // ✅ 3. Send OTP to the confirmed email
            otpService.sendOtpToEmail(authoritativeEmail);

            return ResponseEntity.ok("OTP sent to your registered email.");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpRequest request) {
        // This logic is simple: check the email and OTP
        boolean isOtpValid = otpService.verifyOtp(request.getEmail(), request.getOtp());

        if (isOtpValid) {
            emailService.sendAlertEmail(request.getEmail(),request.getVoterId(),request.getName());
            return ResponseEntity.ok("OTP verified successfully.");

        } else {
            return ResponseEntity.badRequest().body("Invalid or expired OTP.");
        }
    }
    // Update face
    @PostMapping("/{voterId}/update-face")
    public ResponseEntity<?> updateFace(
            @PathVariable String voterId,
            @RequestPart("faceImage") MultipartFile faceImage) {
        try {
            byte[] faceBytes = faceImage.getBytes();
            UserFaceEntity updatedFace = voterFaceService.updateFaceImage(voterId, faceBytes);
            return ResponseEntity.ok(updatedFace);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ Error updating face: " + e.getMessage());
        }
    }

    // Validate voter
    @PostMapping("/validate")
    public ResponseEntity<?> validateVoter(
            @RequestPart String voterId,
            @RequestPart String secretPin,
            @RequestPart("faceImage") MultipartFile faceImage)
    {
        try {
            UserFaceEntity voterDetails = voterFaceService.validatePinAndFace(voterId, secretPin, faceImage);
            return ResponseEntity.status(HttpStatus.OK).body(voterDetails);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // ============================================================
    // 🔹 Other existing endpoints
    // ============================================================
    @GetMapping
    public ResponseEntity<List<UserFaceEntity>> getAllVoters() {
        return ResponseEntity.ok(voterFaceService.getAllVoters());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserFaceEntity> getVoterById(@PathVariable Long id) {
        UserFaceEntity voter = voterFaceService.getVoterById(id);
        return new ResponseEntity<>(voter, HttpStatus.OK);
    }

    @GetMapping("/{voterId}/face")
    public ResponseEntity<UserFaceEntity> getFaceByVoterId(@PathVariable String voterId) {
        Optional<UserFaceEntity> face = voterFaceService.getUserFaceByVoterId(Long.valueOf(voterId));
        return face.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVoter(@PathVariable Long id) {
        voterFaceService.deleteVoterById(id);
        return ResponseEntity.noContent().build();
    }
}