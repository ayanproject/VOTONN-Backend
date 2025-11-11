package com.Ayan.Mondal.VOTEONN.DTO;

public class OtpRequest {
    // Fields from your JavaScript
    private String email;
    private String voterId;
    private String name;
    private String otp;

    // --- Getters and Setters ---
    // Spring needs these to fill the data

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getVoterId() {
        return voterId;
    }
    public void setVoterId(String voterId) {
        this.voterId = voterId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getOtp() {
        return otp;
    }
    public void setOtp(String otp) {
        this.otp = otp;
    }
}
