
package com.example.suivreapp.service;


import java.util.Base64;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


import io.jsonwebtoken.JwtException;

import com.example.suivreapp.model.Patient;
import com.example.suivreapp.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;








import io.jsonwebtoken.*;

import java.util.*;

@Service
public class JwtService {

    // 256-bit secret key for regular authentication
    public static final String SECRET_KEY = "39C9467AE5638297DD42C9F133C61" + "39C9467AE5638297DD42C9F133C61"; 

    // Reset secret key (use a strong secret key for production)
    private static final String RESET_SECRET_KEY = "]=hC'y&cN1Yl?EzR*#fXPT!0`fLjDu/|V>sdQ{`(vj$LmhnX4af$y0M}]o#X839Y";
    private long resetTokenExpiration = 86400000;  // 24 hours expiration time for reset token
    private long jwtExpiration = 3600000;  // 1 hour expiration time for regular auth token

    

    // Generate JWT token for regular login (authentication)
    public String generateAuthToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        return buildToken(claims, userDetails, jwtExpiration, SECRET_KEY);
    }

   
    // Generate JWT token for User or Patient (use the regular key for login)
    public String buildToken(Map<String, Object> claims, UserDetails userDetails, long expiration, String secretKey) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // Validate the token (for regular login)
    public boolean validateAuthToken(String token) {
        try {
            extractUsername(token, SECRET_KEY);  // This will validate the token using the regular secret key
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
            return false;
        }
    }
    

    public String generateResetToken(String email) {
        // Use Keys.secretKeyFor to generate a secure key for HS512
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

        return Jwts.builder()
            .setSubject(email)
            .setExpiration(Date.from(Instant.now().plus(24, ChronoUnit.HOURS)))
            .signWith(key)  // Use the generated key
            .compact();
    }
 



public String decodeUrlSafe(String tokenPart) {
    // Add padding if necessary
    int padding = tokenPart.length() % 4;
    if (padding > 0) {
        tokenPart = tokenPart + "=".repeat(4 - padding);  // Add necessary padding
    }

    // Decode the URL-safe base64 string
    return new String(Base64.getUrlDecoder().decode(tokenPart));
}

public String decodeJwt(String token) {
    // Debugging logs
    System.out.println("Decoding JWT Token: " + token);
    
    String[] parts = token.split("\\.");
    if (parts.length != 3) {
        throw new IllegalArgumentException("Invalid JWT token");
    }

    String header = new String(Base64.getDecoder().decode(parts[0]));
    String payload = new String(Base64.getDecoder().decode(parts[1]));

    System.out.println("Decoded Header: " + header);  // Log header
    System.out.println("Decoded Payload: " + payload);  // Log payload

    // Return or further processing as needed
    return payload;  // Just for example purposes
}

  
    public boolean validateResetToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid token format.");
            }

            // Decode the token (use URL-safe decoding)
            String decodedHeader = decodeUrlSafe(parts[0]);
            String decodedPayload = decodeUrlSafe(parts[1]);
            String decodedSignature = decodeUrlSafe(parts[2]);

            System.out.println("Decoded Header: " + decodedHeader);
            System.out.println("Decoded Payload: " + decodedPayload);

            // Proceed with your validation logic
            extractUsername(token, RESET_SECRET_KEY);  // Use the regular reset secret key for validation
            return true;

        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Invalid reset token: " + e.getMessage());
            return false;
        }
    }


    // Extract username (email) from reset token
    public String extractUsername(String token, String secretKey) {
        System.out.println("Extracting username from token: " + token);  // Debugging line
        return extractClaim(token, Claims::getSubject, secretKey);
    }

    // Extract claims from JWT token using a specific key
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, String secretKey) {
        final Claims claims = extractAllClaims(token, secretKey);
        return claimsResolver.apply(claims);
    }

    // Extract all claims from JWT token
    private Claims extractAllClaims(String token, String secretKey) {
        try {
            System.out.println("Parsing JWT with secret key: " + secretKey);  // Debugging line
            return Jwts.parserBuilder()
                .setSigningKey(secretKey)  // Using the appropriate key
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (Exception e) {
            System.out.println("Error during parsing JWT: " + e.getMessage());  // Debugging line
            throw new JwtException("Invalid JWT token");
        }
    }

    // Reset password endpoint: Assuming it's a POST request where the token is passed in the body
    public boolean resetPassword(HttpServletRequest request) {
        try {
            String token = request.getParameter("token");  // Assuming token is passed as a parameter
            
            // Validate the token
            boolean isValid = validateResetToken(token);
            if (!isValid) {
                System.out.println("Token validation failed.");
                return false;
            }

            // Process the password reset logic here (e.g., updating the password)
            System.out.println("Token is valid. Proceeding with password reset.");
            return true;
        } catch (Exception e) {
            System.out.println("Error in resetPassword: " + e.getMessage());
            return false;
        }
    }
    public String extractEmailFromResetToken(String token) {
        Claims claims = extractAllClaims(token, RESET_SECRET_KEY);
        return claims.getSubject();  // The subject in a JWT is typically the user (in this case, email)
    }

}

