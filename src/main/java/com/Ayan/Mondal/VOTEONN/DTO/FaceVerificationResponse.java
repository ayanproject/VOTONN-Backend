package com.Ayan.Mondal.VOTEONN.DTO;

public class FaceVerificationResponse {

    private String match;

    public String getMatch() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
    }

    @Override
    public String toString() {
        return "FaceVerificationResponse{" +
                "match='" + match + '\'' +
                '}';
    }
}
