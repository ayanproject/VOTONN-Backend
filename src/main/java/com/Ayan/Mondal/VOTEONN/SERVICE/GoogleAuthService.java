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

    @Value("${google.client-id:930858823292-6nr1enve464pdt8jjbh3ekqoerrq42f5.apps.googleusercontent.com}")
    private String clientId;

    public UserDetails authenticateWithGoogle(String idTokenString) throws Exception {
        String finalClientId = (clientId == null || clientId.trim().isEmpty()) 
            ? "930858823292-6nr1enve464pdt8jjbh3ekqoerrq42f5.apps.googleusercontent.com" 
            : clientId.trim();

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(finalClientId))
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken == null) {
            throw new SecurityException("Cryptographic verification failed for Google Token Context.");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String googleId = payload.getSubject();
        String email = payload.getEmail();
        String name = (String) payload.get("name");

        Optional<UserEntity> userOpt = userRepository.findByEmailIgnoreCase(email.trim());
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
                .authorities("ROLE_" + userEntity.getRole())
                .build();

        return userDetails;
    }
}