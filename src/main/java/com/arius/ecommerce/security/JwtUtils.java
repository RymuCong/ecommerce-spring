package com.arius.ecommerce.security;

import com.arius.ecommerce.config.AppConstants;
import com.arius.ecommerce.entity.Role;
import com.arius.ecommerce.exception.TokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtUtils {

    private final String secretKey;

    public JwtUtils(@Value("${jwt.secretKey}") String secretKey) {
        this.secretKey = secretKey;
    }

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

    public SecretKey getKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
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
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + AppConstants.JWT_VALIDITY))
                .signWith(getKey())
                .compact();
    }

    private Claims extractAllClaims(String token){
        try {
            return Jwts.parser()
                    .verifyWith(getKey())
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

    public boolean validateToken(String token, UserDetails userDetails){
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String extractToken(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }



}
