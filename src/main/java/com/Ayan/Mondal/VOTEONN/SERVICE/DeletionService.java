package com.Ayan.Mondal.VOTEONN.SERVICE;

import com.Ayan.Mondal.VOTEONN.DTO.DeletionResponseDTO;
import com.Ayan.Mondal.VOTEONN.MODEL.DeletionRequest;
import com.Ayan.Mondal.VOTEONN.REPOSITORY.DeletionRequestRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DeletionService {

    @Autowired
    private DeletionRequestRepository deletionRepo;

    // Set in application.properties:  file.upload-dir=uploads/deletion
    @Value("${file.upload-dir:uploads/deletion}")
    private String uploadDir;

    /**
     * Saves a voter deletion request after storing the death certificate.
     *
     * @param fullName           Voter's full name
     * @param fatherName         Father's name
     * @param dob                Date of birth (yyyy-MM-dd)
     * @param email              Registered email
     * @param securityPin        4-digit security PIN
     * @param voterId            Voter ID
     * @param deathCertificate   Uploaded death certificate file
     * @return DeletionResponseDTO with saved ID and status
     */
    @Transactional
    public DeletionResponseDTO submitDeletionRequest(
            String fullName,
            String fatherName,
            String dob,
            String email,
            String securityPin,
            String voterId,
            MultipartFile deathCertificate
    ) throws IOException {

        // 1. Prevent duplicate pending requests for the same voter
        if (deletionRepo.existsByVoterIdAndStatus(voterId, "PENDING")) {
            throw new IllegalStateException(
                    "A deletion request for this Voter ID is already under review."
            );
        }

        // 2. Save death certificate to disk
        String certPath = saveFile(deathCertificate, "deletion");

        // 3. Build and persist the entity
        DeletionRequest req = new DeletionRequest();
        req.setFullName(fullName);
        req.setFatherName(fatherName);
        req.setDob(dob);
        req.setEmail(email);
        req.setSecurityPin(securityPin);
        req.setVoterId(voterId);
        req.setDeathCertificatePath(certPath);
        req.setStatus("PENDING");
        req.setSubmittedAt(LocalDateTime.now());

        DeletionRequest saved = deletionRepo.save(req);

        return new DeletionResponseDTO(
                saved.getId(),
                saved.getStatus(),
                "Deletion request submitted successfully. It will be verified within 15 working days."
        );
    }

    /** Returns all pending deletion requests — for admin use later */
    public List<DeletionRequest> getAllPending() {
        return deletionRepo.findByStatus("PENDING");
    }

    // ── Internal helper ───────────────────────────────────────────────────────

    private String saveFile(MultipartFile file, String subfolder) throws IOException {
        Path dir = Paths.get(uploadDir, subfolder);
        Files.createDirectories(dir);

        String originalName = file.getOriginalFilename();
        String extension    = (originalName != null && originalName.contains("."))
                ? originalName.substring(originalName.lastIndexOf("."))
                : "";
        String uniqueName   = UUID.randomUUID() + extension;

        Path targetPath = dir.resolve(uniqueName);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return targetPath.toString();
    }
}