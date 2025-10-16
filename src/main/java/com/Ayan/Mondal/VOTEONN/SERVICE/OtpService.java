package com.Ayan.Mondal.VOTEONN.SERVICE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    @Autowired
    private JavaMailSender javaMailSender;

    // In-memory store for OTPs (email -> OTP)
    private final Map<String, String> otpStore = new ConcurrentHashMap<>();

    public void sendOtpToEmail(String email) {
        String otp = generateOtp();
        otpStore.put(email, otp);  // Store OTP against email
        sendOtpEmail(email, otp);
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = random.nextInt(900000) + 100000;
        return String.valueOf(otp);
    }

    private void sendOtpEmail(String email, String otp) {
        String emailContent = "Dear User,\n\n" +
                "Your OTP for voting verification is: " + otp + "\n\n" +
                "Please use this OTP to complete your voting verification.\n\n" +
                "Thank you for using VOTEONN.";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your-email@example.com");
        message.setTo(email);
        message.setSubject("VOTEONN OTP Verification");
        message.setText(emailContent);

        try {
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean verifyOtp(String email, String inputOtp) {
        String storedOtp = otpStore.get(email);
        if (storedOtp != null && storedOtp.equals(inputOtp)) {
            otpStore.remove(email); // Invalidate OTP after successful verification
            return true;
        }
        return false;
    }
}