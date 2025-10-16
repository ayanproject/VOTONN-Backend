package com.Ayan.Mondal.VOTEONN.MODEL;

import jakarta.persistence.*;

@Entity
public class UserFaceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private byte[] faceImage;

    @OneToOne
    @JoinColumn(name = "voter_id", referencedColumnName = "id")
    private VoterDetails voter;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public byte[] getFaceImage() { return faceImage; }
    public void setFaceImage(byte[] faceImage) { this.faceImage = faceImage; }

    public VoterDetails getVoter() { return voter; }
    public void setVoter(VoterDetails voter) { this.voter = voter; }
}
