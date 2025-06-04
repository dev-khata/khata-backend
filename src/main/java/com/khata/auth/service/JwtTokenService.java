package com.khata.auth.service;

import io.jsonwebtoken.io.Decoders;

import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

@Service
public class JwtTokenService {

    // Token validity in seconds
    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60 * 1000;
//    public static final long JWT_TOKEN_VALIDITY = 60 * 1000; // 1 minute in milliseconds

    // Securely generate a secret key (recommend external configuration for production)
    private String secretKey = "";

    /**
     * Constructor to initialize the JwtTokenService and generate a secret key using HmacSHA256.
     * @throws RuntimeException if the key generation fails.
     */
    public JwtTokenService(){
        try{
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keyGen.generateKey();
            secretKey =  Base64.getEncoder().encodeToString(sk.getEncoded());
        }catch (NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the username from the JWT token.
     *
     * @param token The JWT token from which the username is extracted.
     * @return The username stored in the token.
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Retrieves the roles from the JWT token.
     *
     * @param token The JWT token from which roles are extracted.
     * @return A list of roles stored in the token.
     */
    public List<String> getRolesFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("roles", List.class);
    }

    /**
     * Retrieves the expiration date from the JWT token.
     *
     * @param token The JWT token from which the expiration date is extracted.
     * @return The expiration date of the token.
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Extracts a specific claim from the JWT token.
     *
     * @param token The JWT token from which the claim is extracted.
     * @param claimsResolver The function used to resolve and extract the claim.
     * @param <T> The type of the claim.
     * @return The extracted claim.
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates a JWT token for the given user details.
     *
     * @param userDetails The user details to include in the token.
     * @return The generated JWT token.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        Collection<? extends GrantedAuthority> roles = userDetails.getAuthorities();
        claims.put("roles", roles.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        return buildToken(claims, userDetails.getUsername());
    }

    /**
     * Validates the JWT token.
     *
     * @param token The JWT token to validate.
     * @param userDetails The user details to validate the token against.
     * @return True if the token is valid, false otherwise.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !hasTokenExpired(token));
    }

    /**
     * Builds the JWT token using the provided claims and subject.
     *
     * @param claims The claims to include in the token.
     * @param subject The subject (usually the username) to associate with the token.
     * @return The built JWT token.
     */
    private String buildToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(decodeSecretKey())
                .compact();
    }

    /**
     * Retrieves all claims from the JWT token.
     *
     * @param token The JWT token to extract claims from.
     * @return The claims extracted from the token.
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(decodeSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Checks if the JWT token has expired.
     *
     * @param token The JWT token to check.
     * @return True if the token has expired, false otherwise.
     */
    private Boolean hasTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Decodes the secret key from its base64 encoded form.
     *
     * @return The decoded secret key.
     */
    private SecretKey decodeSecretKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

