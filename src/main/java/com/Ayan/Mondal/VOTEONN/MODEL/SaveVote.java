package com.Ayan.Mondal.VOTEONN.MODEL;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class SaveVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String voterId;

    @ManyToOne
    @JoinColumn(name = "part_name_id")
    @JsonBackReference
    private PartyCards partyName;



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

    public PartyCards getPartyName() {
        return partyName;
    }

    public void setPartyName(PartyCards partyName) {
        this.partyName = partyName;
    }
}
