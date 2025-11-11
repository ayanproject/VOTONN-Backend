package com.Ayan.Mondal.VOTEONN.MODEL;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.Date;


@Entity
@Table(name = "user_face_entity")
public class UserFaceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGBLOB")
    private byte[] faceImage;

    @Column(nullable = false, unique = true)
    private String encryptedVoterId;

    @Column(nullable = false ,length = 60)
    private String encryptedSecretPin;

    private String name;
    private String fatherName;
    private String email;
    private String phone;
    private String gender;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;
    private boolean hasVoted;








    // =======================================
    // 🔹 Add this: relationship with VoterDetails
    // =======================================
//    @OneToOne
//    @JoinColumn(name = "voter_id", referencedColumnName = "id")
//    private VoterDetails voter; // 👈 this matches mappedBy = "voter" in VoterDetails

    // =======================================
    // 🔹 Utility methods for encryption
    // =======================================
    public void setVoterIdSecure(String voterId) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        this.encryptedVoterId = encoder.encode(voterId);
    }

    public void setSecretPinSecure(String secretPin) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        this.encryptedSecretPin = encoder.encode(secretPin);
    }

    // =======================================
    // Getters & Setters
    // =======================================


    public boolean isHasVoted() {
        return hasVoted;
    }

    public void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getFaceImage() {
        return faceImage;
    }

    public void setFaceImage(byte[] faceImage) {
        this.faceImage = faceImage;
    }

    public String getEncryptedVoterId() {
        return encryptedVoterId;
    }

    public void setEncryptedVoterId(String encryptedVoterId) {
        this.encryptedVoterId = encryptedVoterId;
    }

    public String getEncryptedSecretPin() {
        return encryptedSecretPin;
    }

    public void setEncryptedSecretPin(String encryptedSecretPin) {
        this.encryptedSecretPin = encryptedSecretPin;
    }

//    public VoterDetails getVoter() {
//        return voter;
//    }
//
//    public void setVoter(VoterDetails voter) {
//        this.voter = voter;
//    }
}
