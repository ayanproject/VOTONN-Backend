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

@Service
public class GoogleAuthService {

    @Autowired private UserRepository userRepository;
    @Autowired private JwtService jwtService;

    private static final String CLIENT_ID = "1024481193894-rb7hqih2vc62nvpsurrq9c56fok1tter.apps.googleusercontent.com";

    public Map<String, String> authenticateWithGoogle(String idTokenString) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken == null) {
            throw new SecurityException("Cryptographic verification failed for Google Token Context.");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String googleId = payload.getSubject();
        String email = payload.getEmail();
        String name = (String) payload.get("name");

        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        UserEntity userEntity;

        if (userOpt.isPresent()) {
            userEntity = userOpt.get();
            if (userEntity.getGoogleId() == null) {
                userEntity.setGoogleId(googleId);
                userEntity.setAuthProvider("GOOGLE");
                userRepository.save(userEntity);
            }
        } else {
            // Automatic self-provisioning for external social voters
            userEntity = new UserEntity();
            userEntity.setName(name);
            userEntity.setEmail(email);
            userEntity.setGoogleId(googleId);
            userEntity.setAuthProvider("GOOGLE");
            userEntity.setRole("USER");
            userEntity.setAge(18); // Default valid voting threshold baseline
            userRepository.save(userEntity);
        }

        UserDetails userDetails = User.withUsername(userEntity.getEmail())
                .password("")
                .authorities(userEntity.getRole())
                .build();

        String appToken = jwtService.generateToken(userDetails);

        return Map.of(
                "token", appToken,
                "name", userEntity.getName(),
                "email", userEntity.getEmail()
        );
    }
}