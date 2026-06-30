package com.Ayan.Mondal.VOTEONN.CONFIG;

import com.Ayan.Mondal.VOTEONN.MODEL.PartyCards;
import com.Ayan.Mondal.VOTEONN.REPOSITORY.PartyCardsRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final PartyCardsRepo partyCardsRepo;

    public DatabaseSeeder(PartyCardsRepo partyCardsRepo) {
        this.partyCardsRepo = partyCardsRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        if (partyCardsRepo.count() == 0) {
            seedParties();
        }
    }

    private void seedParties() {
        createParty("BJP", "bjp.jpg", "Narendra Modi", "modi.jpg", 
                    "Bharatiya Janata Party", "Development and progress for all.", "To make India a global leader.");
                    
        createParty("TMC", "tmc.jpg", "Mamata Banerjee", "mamata.jpg", 
                    "All India Trinamool Congress", "Welfare of the masses.", "Inclusive progress and social justice.");
                    
        createParty("CPIM", "cpim.jpg", "Sitaram Yechury", "sitaram.jpg", 
                    "Communist Party of India (Marxist)", "Socialist democracy and equality.", "A classless society free from exploitation.");
                    
        createParty("CONGRESS", "congress.jpg", "Mallikarjun Kharge", "kharge.jpg", 
                    "Indian National Congress", "Unity, diversity, and secularism.", "Empowerment of every citizen.");
                    
        createParty("OTHERS", "others.jpg", "Independent Leaders", "others.jpg", 
                    "Other political groups and independent candidates.", "Local representation and alternative choices.", "Decentralized governance.");
    }

    private void createParty(String name, String partyUrl, String leader, String leaderUrl, 
                             String desc, String mission, String vision) {
        PartyCards party = new PartyCards();
        party.setPartyName(name);
        party.setPartyUrl(partyUrl);
        party.setLeader(leader);
        party.setLeaderUrl(leaderUrl);
        party.setDescription(desc);
        party.setMission(mission);
        party.setVision(vision);
        party.setVoteCount(0L);
        partyCardsRepo.save(party);
    }
}
