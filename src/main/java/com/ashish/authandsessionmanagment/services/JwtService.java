package com.ashish.authandsessionmanagment.services;

import com.ashish.authandsessionmanagment.entities.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String JWT_SECRET;

    public SecretKey getSecretKet() {
        return  Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
    }

    public String generateAccessToken(UserEntity userEntity) {
      return Jwts.builder()
                .subject(userEntity.getId())
                .claim("name", userEntity.getName())
                .claim("email", userEntity.getEmail())
                .claim("role", userEntity.getRoles())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10 ) ) // 10 minutes
                .signWith(getSecretKet())
                .compact();
    }

    public String generateRefreshToken(UserEntity userEntity) {
        return Jwts.builder()
                .subject(userEntity.getId())
                .claim("name", userEntity.getName())
                .claim("email", userEntity.getEmail())
                .claim("role", userEntity.getRoles())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365  ) ) // 1 year
                .signWith(getSecretKet())
                .compact();
    }
    public boolean validateToken(String token) {
        Jwts.parser()
                .verifyWith(getSecretKet())
                .build()
                .parseSignedClaims(token);
        return true;
    }
    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKet())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }
}
