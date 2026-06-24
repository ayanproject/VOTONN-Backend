package com.Ayan.Mondal.VOTEONN.CONFIG;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;


    // 2. Token expiration times
    private static final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 5; // 5 minutes
    private static final long REFRESH_TOKEN_VALIDITY = 1000 * 60 * 60 * 24 * 2L; // 2 days

    // 3. Extracts username (email in your case) from token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 4. Extracts a single claim from the token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 5. Generates an access token for a user
    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, ACCESS_TOKEN_VALIDITY, "access");
    }

    // Generates a refresh token for a user
    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, REFRESH_TOKEN_VALIDITY, "refresh");
    }

    // 6. Generates a token with extra claims and specific validity
    private String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long validity,
            String tokenType
    ) {
        extraClaims.put("type", tokenType);
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + validity))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 7. Validates the token
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        String type = extractClaim(token, claims -> claims.get("type", String.class));
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token) && "access".equals(type);
    }
    
    // Validates a refresh token
    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        String type = extractClaim(token, claims -> claims.get("type", String.class));
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token) && "refresh".equals(type);
    }

    // 8. Checks if the token is expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 9. Extracts the expiration date from the token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 10. Extracts all claims from the token
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 11. Gets the signing key
    // ...
    // 11. Gets the signing key
    private Key getSignInKey() {
        // This line now uses the raw bytes of your string,
        // so it will work with any secret.
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}