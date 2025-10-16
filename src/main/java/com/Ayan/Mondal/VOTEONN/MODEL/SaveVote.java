package com.Ayan.Mondal.VOTEONN.MODEL;

import jakarta.persistence.*;

@Entity
public class SaveVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String voterId;

    @ManyToOne
    @JoinColumn(name = "party_id")
    private PartyCards partName;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVoterId() {
        return voterId;
    }

    public void setVoterId(String voterId) {
        this.voterId = voterId;
    }

    public PartyCards getPartName() {
        return partName;
    }

    public void setPartName(PartyCards partName) {
        this.partName = partName;
    }
}
