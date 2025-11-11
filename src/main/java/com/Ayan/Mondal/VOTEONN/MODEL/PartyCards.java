package com.Ayan.Mondal.VOTEONN.MODEL;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class PartyCards {


        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String partyName;
        private String partyUrl;
        private String leader;
        private String leaderUrl;
        private String description;
        private String mission;
        private String vision;

        @OneToMany(mappedBy = "partyName")
        @JsonManagedReference
        private List<SaveVote> voter;


       public PartyCards(){

       }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public String getPartyUrl() {
        return partyUrl;
    }

    public void setPartyUrl(String partyUrl) {
        this.partyUrl = partyUrl;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public String getLeaderUrl() {
        return leaderUrl;
    }

    public void setLeaderUrl(String leaderUrl) {
        this.leaderUrl = leaderUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMission() {
        return mission;
    }

    public void setMission(String mission) {
        this.mission = mission;
    }

    public String getVision() {
        return vision;
    }

    public void setVision(String vision) {
        this.vision = vision;
    }

    public List<SaveVote> getVoter() {
        return voter;
    }

    public void setVoter(List<SaveVote> voter) {
        this.voter = voter;
    }
}
