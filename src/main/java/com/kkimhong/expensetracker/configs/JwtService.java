package com.kkimhong.expensetracker.configs;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey signingKey;  // ← must be initialized at startup

    @PostConstruct
    private void initKey() {
        log.info(">>> Initializing signing key");  // add this to verify it runs
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public String generateToken(UserDetails userDetails, String expenseScope, UUID scopeDepartmentId) {
        List<String> authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", authorities);
        claims.put("expenseScope", expenseScope);
        if (scopeDepartmentId != null) {
            claims.put("scopeDepartmentId", scopeDepartmentId.toString());
        }

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    // Single parse — returns null if invalid/expired
    public Claims validateAndExtractClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            return null;
        }
    }

    public String extractUsername(Claims claims) {
        return claims.getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<SimpleGrantedAuthority> extractAuthorities(Claims claims) {
        List<String> authorities = (List<String>) claims.get("authorities");
        if (authorities == null) return List.of();
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

}