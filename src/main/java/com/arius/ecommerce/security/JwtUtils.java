package com.arius.ecommerce.security;

import com.arius.ecommerce.config.AppConstants;
import com.arius.ecommerce.entity.Role;
import com.arius.ecommerce.exception.TokenExpiredException;
import com.arius.ecommerce.service.RedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtUtils {

    private final RedisService redisService;

    public JwtUtils(RedisService redisService) {
        this.redisService = redisService;
    }

    // uncomment this constructor if you want to generate a new secret key

//    public JwtUtils(){
//        secretKey = generateSecretKey();
//        System.out.println("Secret key: " + secretKey);
//    }
//
//    public String generateSecretKey(){
//        try {
//            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA512");
//            SecretKey secretKey = keyGen.generateKey();
//            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException("Error generating secret key" + e);
//        }
//    }

    // end of comment

    public SecretKey getKey(String key){
        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String name, Set<Role> authorities){
        Map<String,Object> claims = new HashMap<>();
        claims.put("roles", authorities.stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList()));

        return Jwts.builder()
                .claims(claims)
                .subject(name)
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + AppConstants.ACCESS_TOKEN_VALIDITY))
                .signWith(getKey(AppConstants.accessKey))
                .compact();
    }

    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .id(UUID.randomUUID().toString())
                .expiration(new Date(System.currentTimeMillis() + AppConstants.REFRESH_TOKEN_VALIDITY))
                .signWith(getKey(AppConstants.refreshKey))
                .compact();
    }

    private Claims extractAllClaims(String token){
        try {
            return Jwts.parser()
                    .verifyWith(getKey(AppConstants.accessKey))
                    .build()
                    .parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException ex) {
            throw new TokenExpiredException("JWT token has expired");
        }
    }

    private <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractUserName(String token){
        return extractClaim(token,Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }

    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String userName = extractUserName(token);

            // Check if token is blacklisted
            Claims claims = Jwts.parser()
                    .verifyWith(getKey(AppConstants.accessKey))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String jwtId = claims.getId();
            if (redisService.get(jwtId) != null) {
                throw new TokenExpiredException("Token is blacklisted");
            }

            return userName.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (TokenExpiredException e) {
            throw e;
        }
        catch (Exception e) {
            throw new TokenExpiredException("Invalid token");
        }
    }

    public String extractToken(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getKey(AppConstants.refreshKey))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String jwtId = claims.getId();
            if (redisService.get(jwtId) != null) {
                throw new TokenExpiredException("Token is blacklisted");
            }

            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                throw new TokenExpiredException("Token has expired");
            }

            return true;
        } catch (TokenExpiredException e) {
            throw e;
        } catch (Exception e) {
            throw new TokenExpiredException("Invalid token");
        }
    }

    public long extractTokenExpired(String token, String key) {
        try {
            Date expirationDate = Jwts.parser()
                    .verifyWith(getKey(key))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();

            long expirationTime = expirationDate.getTime();
            long currentTime = System.currentTimeMillis();
            return Math.max(expirationTime - currentTime, 0);
        } catch (Exception e) {
            throw new TokenExpiredException("Invalid token");
        }
    }

    public String getEmailFromRefreshToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey(AppConstants.refreshKey))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

}
