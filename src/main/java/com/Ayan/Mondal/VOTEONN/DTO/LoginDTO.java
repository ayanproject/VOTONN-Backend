package com.Ayan.Mondal.VOTEONN.DTO;

/**
 * DTO for the /api/login endpoint.
 * Now includes captchaToken for reCAPTCHA v3 server-side verification.
 */
public class LoginDTO {

    private String email;
    private String password;

    // reCAPTCHA v3 token — generated invisibly on the frontend before form submit
    private String captchaToken;

    public LoginDTO() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getCaptchaToken() { return captchaToken; }
    public void setCaptchaToken(String captchaToken) { this.captchaToken = captchaToken; }
}