package com.Ayan.Mondal.VOTEONN.DTO;

public class PartyDTO {
    private String partyName;
    private String partyUrl;
    private String leader;
    private String leaderUrl;
    private String description;
    private String mission;
    private String vision;


    public PartyDTO(){
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
}
