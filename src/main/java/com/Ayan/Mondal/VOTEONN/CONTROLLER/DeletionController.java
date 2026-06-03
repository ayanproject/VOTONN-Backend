package com.Ayan.Mondal.VOTEONN.CONTROLLER;

import com.Ayan.Mondal.VOTEONN.DTO.DeletionResponseDTO;
import com.Ayan.Mondal.VOTEONN.SERVICE.DeletionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/deletion")
public class DeletionController {

    @Autowired
    private DeletionService deletionService;

    /**
     * POST /api/deletion/submit
     *
     * Accepts multipart/form-data.
     *
     * Form fields:
     *   full_name           (String)
     *   father_name         (String)
     *   dob                 (String)  — yyyy-MM-dd
     *   email               (String)
     *   security_pin        (String)
     *   voter_id            (String)
     *   death_certificate   (MultipartFile)
     */
    @PostMapping(
            value    = "/submit",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> submitDeletion(
            @RequestParam("full_name")          String fullName,
            @RequestParam("father_name")        String fatherName,
            @RequestParam("dob")                String dob,
            @RequestParam("email")              String email,
            @RequestParam("security_pin")       String securityPin,
            @RequestParam("voter_id")           String voterId,
            @RequestParam("death_certificate")  MultipartFile deathCertificate
    ) {
        try {
            DeletionResponseDTO response = deletionService.submitDeletionRequest(
                    fullName, fatherName, dob, email, securityPin, voterId, deathCertificate
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalStateException e) {
            // Duplicate pending request
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to save death certificate. Please try again."));
        }
    }

    /**
     * GET /api/deletion/pending
     * Returns all pending deletion requests.
     * Secure with ADMIN role later.
     */
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingRequests() {
        return ResponseEntity.ok(deletionService.getAllPending());
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<?> resolveRequest(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String action = body.get("action");
        if (action == null || (!action.equalsIgnoreCase("APPROVED") && !action.equalsIgnoreCase("REJECTED"))) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid action. Use APPROVED or REJECTED."));
        }
        try {
            deletionService.resolveDeletion(id, action);
            return ResponseEntity.ok(Map.of("message", "Request updated successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}