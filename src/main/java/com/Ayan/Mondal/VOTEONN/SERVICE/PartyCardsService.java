package com.Ayan.Mondal.VOTEONN.SERVICE;

import com.Ayan.Mondal.VOTEONN.DTO.PartyDTO;
import com.Ayan.Mondal.VOTEONN.MODEL.PartyCards;
import com.Ayan.Mondal.VOTEONN.MODEL.SaveVote;
import com.Ayan.Mondal.VOTEONN.REPOSITORY.PartyCardsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PartyCardsService {


    private final PartyCardsRepo partyCardsRepo;

    public PartyCardsService(PartyCardsRepo partyCardsRepo) {
        this.partyCardsRepo = partyCardsRepo;
    }

    public PartyCards getPartyByPartyName(String partyName) {
        return partyCardsRepo.findByPartyName(partyName);
    }

    public PartyCards addNewParty(PartyDTO party){
        PartyCards newParty = new PartyCards();
        newParty.setPartyName(party.getPartyName());
        newParty.setPartyUrl(party.getPartyUrl());
        newParty.setDescription(party.getDescription());
        newParty.setLeaderUrl(party.getLeaderUrl());
        newParty.setLeader(party.getLeader());
        newParty.setVision(party.getVision());
        newParty.setMission(party.getMission());
        newParty.setVoter(Collections.emptyList());
        partyCardsRepo.save(newParty);
        return newParty;
    }

    public List<SaveVote> getAllVotes(String partyName){
        PartyCards byPartyName = partyCardsRepo.findByPartyName(partyName);
        return byPartyName.getVoter();
    }
}
