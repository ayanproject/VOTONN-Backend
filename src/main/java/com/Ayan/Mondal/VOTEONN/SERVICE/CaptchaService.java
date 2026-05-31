package com.Ayan.Mondal.VOTEONN.SERVICE;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CaptchaService {

    private static class CaptchaDetails {
        String answer;
        long expiryTime;
        CaptchaDetails(String answer, long expiryTime) {
            this.answer = answer;
            this.expiryTime = expiryTime;
        }
    }

    private final Map<String, CaptchaDetails> captchaStorage = new ConcurrentHashMap<>();
    private final Random random = new Random();
    private static final long EXPIRY_DURATION = 5 * 60 * 1000; // 5 Minutes Validity

    public Map<String, String> generateCaptcha() {
        // Intentionally avoided confusing characters like O, 0, I, 1, l
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghkmnpqrstuvwxyz23456789";
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            text.append(chars.charAt(random.nextInt(chars.length())));
        }

        int width = 160;
        int height = 48;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Background styling matching your var(--ink-2) #13151d color
        g2d.setColor(new Color(19, 21, 29));
        g2d.fillRect(0, 0, width, height);

        // Grid/Noise generation blending into your var(--accent) neon glow
        g2d.setColor(new Color(59, 108, 244, 40));
        for (int i = 0; i < 10; i++) {
            g2d.drawLine(random.nextInt(width), random.nextInt(height), random.nextInt(width), random.nextInt(height));
        }

        // Draw randomized letter positioning
        g2d.setFont(new Font("Arial", Font.BOLD, 26));
        for (int i = 0; i < text.length(); i++) {
            // Alternating neon hues
            g2d.setColor(new Color(140 + random.nextInt(110), 170 + random.nextInt(85), 255));
            char ch = text.charAt(i);

            int x = 12 + (i * 24);
            int y = 32 + random.nextInt(10) - 5;

            double angle = (random.nextDouble() - 0.5) * 0.4;
            g2d.rotate(angle, x, y);
            g2d.drawString(String.valueOf(ch), x, y);
            g2d.rotate(-angle, x, y);
        }
        g2d.dispose();

        String base64Image = "";
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            javax.imageio.ImageIO.write(image, "png", baos);
            base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Error rendering CAPTCHA engine graphic", e);
        }

        String sessionId = UUID.randomUUID().toString();
        captchaStorage.put(sessionId, new CaptchaDetails(text.toString().toLowerCase(), System.currentTimeMillis() + EXPIRY_DURATION));

        cleanupExpiredCaptchas(); // Prunes old cached maps asynchronously

        return Map.of("imageBase64", base64Image, "sessionId", sessionId);
    }

    public boolean verifyCaptcha(String sessionId, String userInput) {
        if (sessionId == null || userInput == null) return false;

        CaptchaDetails details = captchaStorage.remove(sessionId); // Instant removal (Single-use assurance)
        if (details == null || System.currentTimeMillis() > details.expiryTime) {
            return false;
        }

        return details.answer.equalsIgnoreCase(userInput.trim());
    }

    private void cleanupExpiredCaptchas() {
        long now = System.currentTimeMillis();
        captchaStorage.entrySet().removeIf(entry -> now > entry.getValue().expiryTime);
    }
}