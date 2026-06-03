package com.Ayan.Mondal.VOTEONN.SERVICE;

import com.Ayan.Mondal.VOTEONN.DTO.CorrectionResponseDTO;
import com.Ayan.Mondal.VOTEONN.MODEL.CorrectionRequest;
import com.Ayan.Mondal.VOTEONN.REPOSITORY.CorrectionRequestRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.Ayan.Mondal.VOTEONN.MODEL.UserFaceEntity;
import com.Ayan.Mondal.VOTEONN.REPOSITORY.UserFaceRepository;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CorrectionService {

    @Autowired
    private CorrectionRequestRepository correctionRepo;

    @Autowired
    private UserFaceRepository userFaceRepository;

    // Folder where uploaded documents are saved.
    // Set this in application.properties:  file.upload-dir=uploads/correction
    @Value("${file.upload-dir:uploads/correction}")
    private String uploadDir;

    /**
     * Saves a correction request to the database after storing the document file.
     *
     * @param voterId        Voter's ID
     * @param securityPin    Current 4-digit security PIN (identity verification)
     * @param fieldToCorrect Which field: name | father | dob | email | pin
     * @param currentValue   The current (wrong) value
     * @param newValue       The desired new value
     * @param document       The supporting document uploaded by the user
     * @return CorrectionResponseDTO with the saved request ID and status
     */
    @Transactional
    public CorrectionResponseDTO submitCorrectionRequest(
            String voterId,
            String securityPin,
            String fieldToCorrect,
            String currentValue,
            String newValue,
            MultipartFile document
    ) throws IOException {

        // 1. Validate allowed field names to prevent injection
        List<String> allowedFields = List.of("name", "father", "dob", "email", "pin");
        if (!allowedFields.contains(fieldToCorrect)) {
            throw new IllegalArgumentException("Invalid field: " + fieldToCorrect);
        }

        // 2. Save the document to disk
        String docPath = saveFile(document, "correction");

        // 3. Build and persist the entity
        CorrectionRequest req = new CorrectionRequest();
        req.setVoterId(voterId);
        req.setSecurityPin(securityPin);          // plain text — hash later if needed
        req.setFieldToCorrect(fieldToCorrect);
        req.setCurrentValue(currentValue);
        req.setNewValue(newValue);
        req.setDocumentPath(docPath);
        req.setStatus("PENDING");
        req.setSubmittedAt(LocalDateTime.now());

        CorrectionRequest saved = correctionRepo.save(req);

        return new CorrectionResponseDTO(
                saved.getId(),
                saved.getStatus(),
                "Correction request submitted successfully. It will be reviewed within 7 working days."
        );
    }

    /** Returns all pending correction requests — for admin use later */
    public List<CorrectionRequest> getAllPending() {
        return correctionRepo.findByStatus("PENDING");
    }

    @Transactional
    public void resolveCorrection(Long id, String action) {
        CorrectionRequest req = correctionRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Correction request not found with ID: " + id));
        req.setStatus(action.toUpperCase());
        correctionRepo.save(req);

        if ("APPROVED".equalsIgnoreCase(action)) {
            org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder = 
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
            UserFaceEntity voter = userFaceRepository.findAll().stream()
                    .filter(v -> encoder.matches(req.getVoterId(), v.getEncryptedVoterId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Voter not found with matching Voter ID"));

            String field = req.getFieldToCorrect();
            String newValue = req.getNewValue();

            if ("name".equalsIgnoreCase(field)) {
                voter.setName(newValue);
            } else if ("father".equalsIgnoreCase(field)) {
                voter.setFatherName(newValue);
            } else if ("dob".equalsIgnoreCase(field)) {
                voter.setDob(java.time.LocalDate.parse(newValue));
            } else if ("email".equalsIgnoreCase(field)) {
                voter.setEmail(newValue);
            } else if ("pin".equalsIgnoreCase(field)) {
                voter.setSecretPinSecure(newValue);
            }
            userFaceRepository.save(voter);
        }
    }

    // ── Internal helper ───────────────────────────────────────────────────────

    private String saveFile(MultipartFile file, String subfolder) throws IOException {
        // Create directory if it doesn't exist
        Path dir = Paths.get(uploadDir, subfolder);
        Files.createDirectories(dir);

        // Give the file a unique name to avoid collisions
        String originalName = file.getOriginalFilename();
        String extension    = (originalName != null && originalName.contains("."))
                ? originalName.substring(originalName.lastIndexOf("."))
                : "";
        String uniqueName   = UUID.randomUUID() + extension;

        Path targetPath = dir.resolve(uniqueName);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        // Return the relative path stored in DB
        return dir.resolve(uniqueName).toString();
    }
}