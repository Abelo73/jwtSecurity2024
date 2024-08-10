package com.act.springJwt.service;

import com.act.springJwt.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private String secreteKey = "ebacb9b0011669c695ff6bfab6808d2df4a7a30a76bf165cc12c0dc08d63af58";

    public <T> T extractClaims(String token, Function<Claims, T> resolver){
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

//    Extracting username
    public String extractUsername(String token){
        return extractClaims(token, Claims::getSubject);
    }

    public boolean isValid(String token, User user){
        String username = extractUsername(token);
        return (username.equals(user.getUsername()) && !isTokenIsExpired(token));
    }

    boolean isTokenIsExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token){
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    public String generateToke(User user){
        String token = Jwts
                .builder()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 24 * 60 *60 *1000))
                .signWith(getSignInKey())
                .compact();
        return token;
    }

    private SecretKey getSignInKey(){
        byte [] keyBytes = Decoders.BASE64URL.decode(secreteKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }



    public String generatePasswordResetToken(User user) {
        return Jwts
                .builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000)) // 15 minutes validity
                .signWith(getSignInKey())
                .compact();
    }



//    public boolean isTokenExpired(String token) {
//    }
}
