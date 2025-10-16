package com.Ayan.Mondal.VOTEONN.CONTROLLER;

import com.Ayan.Mondal.VOTEONN.DTO.VoterDto;
import com.Ayan.Mondal.VOTEONN.MODEL.UserFaceEntity;
import com.Ayan.Mondal.VOTEONN.MODEL.VoterDetails;
import com.Ayan.Mondal.VOTEONN.SERVICE.FaceVerificationService;
import com.Ayan.Mondal.VOTEONN.SERVICE.VoterDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/api/voters")
@RequiredArgsConstructor
public class VoterCardController {


    @Autowired
    private final VoterDetailsService voterDetailsService;
    @Autowired
    private final FaceVerificationService faceVerificationService;

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

            VoterDetails savedVoter = voterDetailsService.registerVoter(voter, faceBytes, email);
            return ResponseEntity.ok(savedVoter);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ Error processing request: " + e.getMessage());
        }
    }

    // Update face
    @PostMapping("/{voterId}/update-face")
    public ResponseEntity<?> updateFace(
            @PathVariable String voterId,
            @RequestPart("faceImage") MultipartFile faceImage) {
        try {
            byte[] faceBytes = faceImage.getBytes();
            UserFaceEntity updatedFace = voterDetailsService.updateFaceImage(voterId, faceBytes);
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
            VoterDetails voterDetails = voterDetailsService.validatePinAndFace(voterId, secretPin, faceImage);
            return ResponseEntity.status(HttpStatus.OK).body(voterDetails);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // ============================================================
    // 🔹 Other existing endpoints
    // ============================================================
    @GetMapping
    public ResponseEntity<List<VoterDetails>> getAllVoters() {
        return ResponseEntity.ok(voterDetailsService.getAllVoters());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VoterDetails> getVoterById(@PathVariable Long id) {
        VoterDetails voter = voterDetailsService.getVoterById(id);
        return new ResponseEntity<>(voter, HttpStatus.OK);
    }

    @GetMapping("/{voterId}/face")
    public ResponseEntity<UserFaceEntity> getFaceByVoterId(@PathVariable String voterId) {
        Optional<UserFaceEntity> face = voterDetailsService.getUserFaceByVoterId(Long.valueOf(voterId));
        return face.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVoter(@PathVariable Long id) {
        voterDetailsService.deleteVoterById(id);
        return ResponseEntity.noContent().build();
    }
}