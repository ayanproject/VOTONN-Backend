package com.Ayan.Mondal.VOTEONN.SERVICE;

import com.Ayan.Mondal.VOTEONN.DTO.FaceVerificationResponse;
import com.Ayan.Mondal.VOTEONN.DTO.VoterDto;
import com.Ayan.Mondal.VOTEONN.MODEL.*;
import com.Ayan.Mondal.VOTEONN.REPOSITORY.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_features2d.BFMatcher;
import org.bytedeco.opencv.opencv_features2d.ORB;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoterDetailsService {

    private final UserFaceRepository faceRepository;
    private final VoterDetailsRepository voterDetailsRepository;
    private final UserRepository userRepository;
    private final PartyCardsRepo partyRepo;
    private final SaveVoteRepository saveVoteRepository;
    private final EmailService emailService;
    private final FaceVerificationService verificationService;

    // ============================================================
    // 1️⃣ Register voter with face
    // ============================================================

    @Transactional
    public VoterDetails registerVoter(VoterDto voter, byte[] faceImage, String email) {
        UserEntity registeredUser = userRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("user with email " + email + " not found"));

        if (voterDetailsRepository.findByVoterId(voter.getVoterId()).isPresent()) {
            throw new IllegalArgumentException("Voter ID already exists: " + voter.getVoterId());
        }

        if (faceImage == null || faceImage.length == 0) {
            throw new IllegalArgumentException("Face image is required during registration");
        }

        VoterDetails newVoter  = new VoterDetails();

        newVoter.setVoterId(voter.getVoterId());
        newVoter.setEmail(email);
        newVoter.setDob(voter.getDob());
        newVoter.setGender(voter.getGender());
        newVoter.setName(voter.getName());
        newVoter.setPhone(voter.getPhone());
        newVoter.setFatherName(voter.getFatherName());
        newVoter.setSecretPin(voter.getSecretPin());
        newVoter.setHasVoted(false);

        UserFaceEntity newFace = new UserFaceEntity();
        newFace.setFaceImage(faceImage);

        //bidirectional mapping(b/w - VoterDetails & UserFaceEntity)
        newFace.setVoter(newVoter);
        newVoter.setUserFace(newFace);

        //bidirectional mapping(b/w - VoterDetails & UserEntity)
        newVoter.setUser(registeredUser);
        registeredUser.getVoter().add(newVoter);

        voterDetailsRepository.save(newVoter);
        faceRepository.save(newFace);
        userRepository.save(registeredUser);

        return newVoter;
    }

    // ============================================================
    // 2️⃣ Update face image for an existing voter
    // ============================================================
    public UserFaceEntity updateFaceImage(String voterId, byte[] newFaceImage) {

        VoterDetails existingVoter = voterDetailsRepository.findByVoterId(voterId)
                .orElseThrow(()-> new RuntimeException("Voter with id " + voterId + " not found"));

        if (newFaceImage == null || newFaceImage.length == 0) {
            throw new IllegalArgumentException("Face image cannot be empty");
        }

        UserFaceEntity userFace = existingVoter.getUserFace();
        userFace.setFaceImage(newFaceImage);

        existingVoter.setUserFace(userFace);

        UserFaceEntity savedUser = faceRepository.save(userFace);
        voterDetailsRepository.save(existingVoter);

        return savedUser;
    }


    public VoterDetails validatePinAndFace(String voterId, String secretPin, MultipartFile image) throws IOException {

        VoterDetails existVoter = voterDetailsRepository.findByVoterIdAndSecretPin(voterId, secretPin)
                .orElseThrow(() -> new RuntimeException("No voter exist"));

        Long userId = existVoter.getId();

        // Pass byte[] and original filename
        FaceVerificationResponse response = verificationService.verifyFace(userId, image.getBytes(), image.getOriginalFilename());
        System.out.println("Here is log:"+response);
        if ("Same Person".equalsIgnoreCase(response.getMatch())) {
            return existVoter;
        } else {
            throw new RuntimeException("Voter verification failed");
        }
    }



    // ============================================================
    // 6️⃣ CRUD for UserFaceEntity
    // ============================================================

    public List<UserFaceEntity> getAllUserFaces()
    {
        return faceRepository.findAll();
    }

    public Optional<UserFaceEntity> getUserFaceByVoterId(Long voterId)
    {
        return faceRepository.findByVoterId(voterId);
    }

    public void deleteUserFace(Long id)
    {
        faceRepository.deleteById(id);
    }

    public List<VoterDetails> getAllVoters()
    {
        return voterDetailsRepository.findAll();
    }

    public void deleteVoterById(Long id)
    {
        VoterDetails byId = voterDetailsRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Voter id " + id + " not found"));
        voterDetailsRepository.deleteById(id);
    }

    public VoterDetails getVoterById(Long id)
    {
        VoterDetails voterDetails = voterDetailsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Voter id " + id + " not found"));
        return voterDetails;
    }

    public String giveVote(String voterId, String partyName){
        VoterDetails registeredVoter = voterDetailsRepository.findByVoterId(voterId)
                .orElseThrow(() -> new IllegalArgumentException("Voter id " + voterId + " not found"));

        if(registeredVoter.isHasVoted()){
            throw new IllegalArgumentException("Voter " + voterId + " is already voted");
        }

        PartyCards partyNamed = partyRepo.findByPartyName(partyName);
        if(partyNamed == null){
            throw new IllegalArgumentException("Party name " + partyName + " not found");
        }

        SaveVote newVote = new SaveVote();
        newVote.setVoterId(voterId);
        //bidirectional mapping between SaveVote and PartyCards
        newVote.setPartName(partyNamed);
        partyNamed.getVoter().add(newVote);

        registeredVoter.setHasVoted(true);

        saveVoteRepository.save(newVote);
        partyRepo.save(partyNamed);
        voterDetailsRepository.save(registeredVoter);

        emailService.sendVotingConfirmation(registeredVoter.getEmail(), partyName);
        return "success";
    }

}
