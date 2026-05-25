package com.Ayan.Mondal.VOTEONN.SERVICE;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Verifies a Google reCAPTCHA v3 token against Google's siteverify API.
 * Returns true only if the token is genuine AND the risk score >= 0.5.
 *
 * Score guide:
 *   1.0 → very likely human
 *   0.0 → very likely bot
 *   0.5 → our threshold (safe middle ground)
 */
@Service
public class CaptchaService {

    @Value("${recaptcha.secret}")
    private String recaptchaSecret;

    private static final String VERIFY_URL =
            "https://www.google.com/recaptcha/api/siteverify";

    // Minimum score to consider a request human
    private static final double SCORE_THRESHOLD = 0.5;

    public boolean verifyCaptcha(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        try {
            RestTemplate restTemplate = new RestTemplate();

            // Google expects a POST (or GET) with secret + response as query params
            String url = VERIFY_URL + "?secret=" + recaptchaSecret + "&response=" + token;

            @SuppressWarnings("unchecked")
            ResponseEntity<Map> response = restTemplate.postForEntity(url, null, Map.class);

            Map<String, Object> body = response.getBody();
            if (body == null) return false;

            boolean success = Boolean.TRUE.equals(body.get("success"));
            double  score   = body.get("score") != null
                    ? ((Number) body.get("score")).doubleValue()
                    : 0.0;

            return success && score >= SCORE_THRESHOLD;

        } catch (Exception e) {
            // Log and fail safe — deny the request if verification itself fails
            System.err.println("[CaptchaService] Verification error: " + e.getMessage());
            return false;
        }
    }
}