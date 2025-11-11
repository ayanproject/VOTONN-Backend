package com.Ayan.Mondal.VOTEONN.SERVICE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;  // Used for sending emails

    // Method to send the alert email after OTP verification
    public void sendAlertEmail(String email, String voterId, String name) {
        String emailContent = "🚨 Voting Attempt Alert 🚨\n\n" +
                "Dear " + name + ",\n\n" +
                "We detected an attempt to cast a vote using your Voter ID: **" + voterId + "**.\n\n" +
                "If this action was initiated by you, no further action is needed.\n" +
                "However, if you did **not** attempt to vote, please report this immediately to the VOTEONN Election Commission for further investigation.\n\n" +
                "🛡️ Your voting security is our top priority.\n\n" +
                "Thank you for being a responsible citizen.\n\n" +
                "Sincerely,\n" +
                "Team VOTEONN";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your-email@example.com"); // replace with your email
        message.setTo(email);
        message.setSubject("🚨 VOTEONN Security Alert: Voting Attempt Detected");
        message.setText(emailContent);

        try {
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to send voting confirmation after vote submission
    public void sendVotingConfirmation(String toEmail) {
        String subject = "Voting Confirmation";
        String body = "Dear Voter,\n\n" +
                "Thank you for successfully voting \n" +
                "Your vote has been recorded securely.\n\n" +
                "Regards,\n" +
                "Voting Application Team";

        sendSimpleMail(toEmail, subject, body);
    }

    // 👉 Helper method to send a simple mail
    private void sendSimpleMail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your-email@example.com"); // replace with your email
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        try {
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
