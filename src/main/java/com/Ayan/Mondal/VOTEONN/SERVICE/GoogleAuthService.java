package com.Ayan.Mondal.VOTEONN.SERVICE;

import com.Ayan.Mondal.VOTEONN.CONFIG.JwtService;
import com.Ayan.Mondal.VOTEONN.MODEL.UserEntity;
import com.Ayan.Mondal.VOTEONN.REPOSITORY.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Handles Google OAuth2 authentication (client-side Google Identity Services flow).
 *
 * Flow:
 *  1. Frontend gets a Google ID token (credential) via the GIS popup.
 *  2. Frontend POSTs it to /api/auth/google.
 *  3. This service verifies the token with Google's servers.
 *  4. Finds or creates the user in the local DB.
 *  5. Returns a regular app JWT — same as the password login flow.
 */
@Service
public class GoogleAuthService {

    @Value("${google.client-id}")
    private String googleClientId;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    /**
     * @param credential  The raw Google ID token string from the frontend.
     * @return  Map with "token" (app JWT) and "name" (display name).
     * @throws Exception  If the token is invalid or verification fails.
     */
    public Map<String, String> authenticateWithGoogle(String credential) throws Exception {
        if (credential == null || credential.isBlank()) {
            throw new IllegalArgumentException("Google credential is missing.");
        }

        // ── 1. Verify the ID token with Google ────────────────────────────────
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance()
        )
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken = verifier.verify(credential);
        if (idToken == null) {
            throw new SecurityException("Invalid or expired Google token.");
        }

        // ── 2. Extract user info from the verified token ───────────────────────
        Payload payload  = idToken.getPayload();
        String email     = payload.getEmail();
        String name      = (String) payload.get("name");
        String googleId  = payload.getSubject();   // Google's unique user ID

        // ── 3. Find existing user or create a new one ─────────────────────────
        Optional<UserEntity> existing = userRepository.findByEmail(email);
        UserEntity user;

        if (existing.isEmpty()) {
            // First-time Google login → create account automatically
            user = new UserEntity();
            user.setEmail(email);
            user.setName(name);
            user.setGoogleId(googleId);
            user.setAuthProvider("GOOGLE");
            user.setRole("USER");
            user.setPassword("");   // No password for OAuth users — BCrypt won't match ""
            userRepository.save(user);

        } else {
            user = existing.get();
            // If an existing LOCAL user logs in with Google for the first time,
            // link their Google ID to the account.
            if (user.getGoogleId() == null || user.getGoogleId().isBlank()) {
                user.setGoogleId(googleId);
                user.setAuthProvider("GOOGLE");
                userRepository.save(user);
            }
        }

        // ── 4. Generate a standard app JWT for the user ───────────────────────
        UserDetails userDetails = User
                .withUsername(user.getEmail())
                .password(user.getPassword() != null ? user.getPassword() : "")
                .roles(user.getRole() != null ? user.getRole() : "USER")
                .build();

        String token = jwtService.generateToken(userDetails);

        return Map.of(
                "token", token,
                "name",  user.getName() != null ? user.getName() : email
        );
    }
}