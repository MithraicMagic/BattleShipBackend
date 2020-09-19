package com.bs.epic.battleships.verification;

import com.bs.epic.battleships.rest.repository.dto.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class JwtUtil {
    private SecretKey SECRET_KEY;

    public JwtUtil(@Value("${jwt.secret.key}") String key) {
        SECRET_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
    }

    public String extractToken(String token) { return token.replace("Bearer ", ""); }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final var claims = extractAllClaims(token);
        if (claims.isPresent()) {
            return claimsResolver.apply(claims.get());
        }

        return null;
    }

    private Optional<Claims> extractAllClaims(String token) {
        try {
            return Optional.of(Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody());
        }
        catch (JwtException ex) {
            return Optional.empty();
        }
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(User user) {
        return createToken(new HashMap<>(), user.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256).compact();
    }

    public Boolean validateToken(String token, User user) {
        final String username = extractUsername(token);
        return (username.equals(user.getUsername()) && !isTokenExpired(token));
    }
}
