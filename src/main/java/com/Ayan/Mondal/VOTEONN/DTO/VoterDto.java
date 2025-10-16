package com.Ayan.Mondal.VOTEONN.DTO;

import jakarta.persistence.*;

import java.time.LocalDate;

public class VoterDto {

    private String name;
    private String fatherName;
    private String gender;
    @Temporal(TemporalType.DATE)
    private LocalDate dob;
    @Column(unique = true, nullable = false)
    private String voterId;
    private String email;
    private String phone;
    private String secretPin;


    public String getSecretPin() {
        return secretPin;
    }
    public void setSecretPin(String secretPin) {
        this.secretPin = secretPin;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getVoterId() {
        return voterId;
    }

    public void setVoterId(String voterId) {
        this.voterId = voterId;
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

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
