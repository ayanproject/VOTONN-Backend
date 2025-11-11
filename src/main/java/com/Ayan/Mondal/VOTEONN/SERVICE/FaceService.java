package com.Ayan.Mondal.VOTEONN.SERVICE;

import com.Ayan.Mondal.VOTEONN.DTO.VoteRequest;
import com.Ayan.Mondal.VOTEONN.DTO.VoterCredentialDTO;
import com.Ayan.Mondal.VOTEONN.DTO.VoterDto;
import com.Ayan.Mondal.VOTEONN.MODEL.*;
import com.Ayan.Mondal.VOTEONN.REPOSITORY.*;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class FaceService {


    @Autowired
    SaveVoteRepository voteRepository;
    @Autowired
    EmailService emailService;
    @Autowired
  VoterRepository voterRepository;
    @Autowired
    private PartyCardsRepo cardsRepo;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private UserFaceRepository userFaceRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // ==============================================================
    // 🔹 Register voter with face, secretPin, and voterId encryption
    // ==============================================================
    @Transactional
    public UserFaceEntity registerVoter(VoterDto voterDto, byte[] faceBytes, String email) {
        // ✅ 1. Check if email already exists
        Optional<UserFaceEntity> existingVoter = userFaceRepository.findByEmail(email);
        if (existingVoter.isPresent()) {
            throw new IllegalArgumentException("Email already registered: " + email);
        }

        // ✅ 2. Create ONE entity and set ALL properties
        UserFaceEntity voter = new UserFaceEntity();
        voter.setName(voterDto.getName());
        voter.setFatherName(voterDto.getFatherName());
        voter.setGender(voterDto.getGender());
        voter.setDob(voterDto.getDob());
        voter.setEmail(email);
        voter.setPhone(voterDto.getPhone());
        voter.setHasVoted(false);

        // ✅ 3. Set the face image and encrypted values on the SAME object
        voter.setFaceImage(faceBytes);
        voter.setVoterIdSecure(voterDto.getVoterId());
        voter.setSecretPinSecure(voterDto.getSecretPin()); // <-- THIS LINE FIXES THE ERROR

        // ✅ 4. Save the single entity ONE time
        return userFaceRepository.save(voter);
    }
    // ==============================================================
    // 🔹 Update existing face image
    // ==============================================================
    @Transactional
    public UserFaceEntity updateFaceImage(String voterId, byte[] faceBytes) {
        Optional<UserFaceEntity> faceOpt = userFaceRepository.findByEncryptedVoterId(voterId);
        if (faceOpt.isEmpty()) {
            throw new IllegalArgumentException("Voter not found for given ID");
        }

        UserFaceEntity faceEntity = faceOpt.get();
        faceEntity.setFaceImage(faceBytes);
        return userFaceRepository.save(faceEntity);
    }

    // ==============================================================
    // 🔹 Validation handled by Python (Spring just stores)
    // ==============================================================
    public UserFaceEntity validatePinAndFace(String voterId, String secretPin, MultipartFile faceImage) {
        throw new UnsupportedOperationException("Validation is handled by FastAPI (Python).");
    }

    // ==============================================================
    // 🔹 Other helper methods
    // ==============================================================
    public List<UserFaceEntity> getAllVoters() {
        return userFaceRepository.findAll();
    }

    public UserFaceEntity getVoterById(Long id) {
        return userFaceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Voter not found with ID: " + id));
    }

    public Optional<UserFaceEntity> getUserFaceByVoterId(Long voterId) {
        return userFaceRepository.findById(voterId);
    }

    public void deleteVoterById(Long id) {
        userFaceRepository.deleteById(id);
    }

    public boolean verifyVoter(VoterCredentialDTO dto) {
        Optional<UserFaceEntity> voterOpt = userFaceRepository.findByEncryptedVoterId(dto.getVoterId());
        if (voterOpt.isPresent()) {
            UserFaceEntity voter = voterOpt.get();

            // Convert LocalDate to String safely
            String voterDob = voter.getDob().toString();         // e.g., "2004-06-27"
            String dtoDob = dto.getDob().toString();

            return voter.getName().trim().equalsIgnoreCase(dto.getName().trim())
                    && voter.getFatherName().trim().equalsIgnoreCase(dto.getFatherName().trim())
                    && voter.getGender().trim().equalsIgnoreCase(dto.getGender().trim())
                    && voterDob.equals(dtoDob)
                    && voter.getEmail().trim().equalsIgnoreCase(dto.getEmail().trim())
                    && voter.getPhone().trim().equals(dto.getPhone().trim())
                    && !voter.isHasVoted();
        }
        return false;
    }public String submitVote(VoteRequest request) {
        PartyCards byPartyName = cardsRepo.findByPartyName(request.getPartyName());
        if (byPartyName == null) {
            throw new RuntimeException("Invalid party name: " + request.getPartyName());
        }

        // =========================================================
        // ✅ FIX 1: Find the voter by their EMAIL, not their Voter ID
        // =========================================================
        Optional<UserFaceEntity> byVoterEmail = userFaceRepository.findByEmail(request.getEmail());

        if (byVoterEmail.isEmpty()) {
            // Now this error message makes sense
            throw new RuntimeException("Voter not found for email: " + request.getEmail());
        }

        UserFaceEntity voter = byVoterEmail.get(); // Get the voter from the correct Optional
        if (voter.isHasVoted()) {
            throw new RuntimeException("You have already voted!");
        }

        voter.setHasVoted(true);

        SaveVote newVote = new SaveVote();
        newVote.setPartyName(byPartyName);

        // This is correct: you are encrypting the Voter ID for the 'SaveVote' table
        newVote.setVoterId(passwordEncoder.encode(request.getVoterId()));

        byPartyName.getVoter().add(newVote);

        // =========================================================
        // ✅ FIX 2: Save the voter using the CORRECT repository
        // =========================================================
        userFaceRepository.save(voter); // Use 'userFaceRepository'

        cardsRepo.save(byPartyName);
        voteRepository.save(newVote);

        emailService.sendVotingConfirmation(request.getEmail());
        return "Vote is given successfully";
    }
}