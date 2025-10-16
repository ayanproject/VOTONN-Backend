package com.Ayan.Mondal.VOTEONN.DTO;


import java.sql.Date;
import java.time.LocalDate;

public class VoterCredentialDTO {
    private String name;
    private String fatherName;
    private String gender;
    private LocalDate dob;
    private  String voterId;
    private String email;
    private String phone;

    public VoterCredentialDTO(){

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

    public void setDob(Date dob) {
        this.dob = dob.toLocalDate();
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
