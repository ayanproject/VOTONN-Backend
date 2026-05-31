package com.Ayan.Mondal.VOTEONN.DTO;

/**
 * DTO for the /api/login endpoint.
 * Now includes captchaToken for reCAPTCHA v3 server-side verification.
 */
public class LoginDTO {
    private String email;
    private String password;
    private String captchaAnswer;   // 👈 Match this precisely
    private String captchaSessionId; // 👈 Match this precisely

    // Add standard boilerplate getters & setters for both properties here...
    public String getCaptchaAnswer() { return captchaAnswer; }
    public void setCaptchaAnswer(String captchaAnswer) { this.captchaAnswer = captchaAnswer; }
    public String getCaptchaSessionId() { return captchaSessionId; }
    public void setCaptchaSessionId(String captchaSessionId) { this.captchaSessionId = captchaSessionId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}