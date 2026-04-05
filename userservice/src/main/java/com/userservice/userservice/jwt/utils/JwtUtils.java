package com.userservice.userservice.jwt.utils;

import com.userservice.userservice.dtos.LoginRequest;
import com.userservice.userservice.dtos.UserDetail;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtUtils {

    String KEY = "070df949d4329a55045e2b309b106ef6";

    public String generateToken(UserDetail detail){
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(detail.getEmail())
                .claim("userId", detail.getId())
                .claim("name", detail.getName())
                .signWith(Keys.hmacShaKeyFor(KEY.getBytes()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration((new Date(System.currentTimeMillis() + 1000*60*60)))
                .compact();
    }

    public String nameExtractor(String token){
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(KEY.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validToken(String token, UserDetails details){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(KEY.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();

        boolean isExpired = claims.getExpiration().before(new Date(System.currentTimeMillis()));

        return !isExpired && details.getUsername().equals(claims.getSubject());
    }
}
