package com.Ayan.Mondal.VOTEONN.CONTROLLER;

import com.Ayan.Mondal.VOTEONN.DTO.CorrectionResponseDTO;
import com.Ayan.Mondal.VOTEONN.SERVICE.CorrectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/correction")
public class CorrectionController {

    @Autowired
    private CorrectionService correctionService;

    /**
     * POST /api/correction/submit
     *
     * Accepts multipart/form-data so it can carry both text fields and a file.
     * Frontend sends via FormData — no JSON body needed.
     *
     * Form fields:
     *   voter_id        (String)
     *   security_pin    (String)
     *   field_to_correct (String)  — name | father | dob | email | pin
     *   current_value   (String)
     *   new_value       (String)
     *   document        (MultipartFile)
     */
    @PostMapping(
            value    = "/submit",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> submitCorrection(
            @RequestParam("voter_id")         String voterId,
            @RequestParam("security_pin")     String securityPin,
            @RequestParam("field_to_correct") String fieldToCorrect,
            @RequestParam("current_value")    String currentValue,
            @RequestParam("new_value")        String newValue,
            @RequestParam("document")         MultipartFile document
    ) {
        try {
            CorrectionResponseDTO response = correctionService.submitCorrectionRequest(
                    voterId, securityPin, fieldToCorrect, currentValue, newValue, document
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to save supporting document. Please try again."));
        }
    }

    /**
     * GET /api/correction/pending
     * Returns all pending correction requests.
     * Protect this with ADMIN role when you add admin dashboard later.
     */
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingRequests() {
        return ResponseEntity.ok(correctionService.getAllPending());
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<?> resolveRequest(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String action = body.get("action");
        if (action == null || (!action.equalsIgnoreCase("APPROVED") && !action.equalsIgnoreCase("REJECTED"))) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid action. Use APPROVED or REJECTED."));
        }
        try {
            correctionService.resolveCorrection(id, action);
            return ResponseEntity.ok(Map.of("message", "Request updated successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}