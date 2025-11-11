package com.Ayan.Mondal.VOTEONN.CONTROLLER;

import com.Ayan.Mondal.VOTEONN.MODEL.PartyCards;
import com.Ayan.Mondal.VOTEONN.SERVICE.PartyCardsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = "https://red-plant-01033d700.3.azurestaticapps.net")
@RestController
@RequestMapping("/api")

public class PartyCardsController {


        @Autowired
        PartyCardsService PartyCardsService;


        @GetMapping("/party")
        public ResponseEntity<?> getPartyByPartyName(@RequestParam String partyName) {
            PartyCards partyByPartyName = PartyCardsService.getPartyByPartyName(partyName);
            return ResponseEntity.status(HttpStatus.CREATED).body(partyByPartyName);

        }
}
