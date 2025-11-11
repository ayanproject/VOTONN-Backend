package com.Ayan.Mondal.VOTEONN.CONTROLLER;

import com.Ayan.Mondal.VOTEONN.DTO.VerifyOtpRequest;
import com.Ayan.Mondal.VOTEONN.DTO.VoteRequest;
import com.Ayan.Mondal.VOTEONN.DTO.VoterCredentialDTO;


import com.Ayan.Mondal.VOTEONN.REPOSITORY.VoterRepository;
import com.Ayan.Mondal.VOTEONN.SERVICE.EmailService;
import com.Ayan.Mondal.VOTEONN.SERVICE.FaceService;
import com.Ayan.Mondal.VOTEONN.SERVICE.OtpService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/voter")
public class VoterAuthController {

    @Autowired
    private FaceService voterService;

    @Autowired
    private VoterRepository repository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;




    @PostMapping("/submit-vote")
    public ResponseEntity<?> submitVote(@RequestBody VoteRequest request) {

        String s = voterService.submitVote(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(s);
    }
}