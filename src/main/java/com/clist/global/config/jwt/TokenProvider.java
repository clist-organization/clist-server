package com.clist.global.config.jwt;

import com.clist.domain.user.entity.User;
import com.clist.domain.user.repository.UserRepository;
import com.clist.global.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenProvider {

    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;
    private SecretKey secretKey;

    @PostConstruct
    void init() {
        byte[] bytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
        this.secretKey = Keys.hmacShaKeyFor(bytes);
    }

    public String generateToken(User user, Duration duration) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + duration.toMillis()), user);
    }

    private String makeToken(Date expire, User user) {
        Date now = new Date();
        return Jwts.builder()
                .header().type("JWT")
                .and()
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiration(expire)
                .subject(user.getEmail())
                .claim("id", user.getId().toString())
                .signWith(secretKey)
                .compact();
    }

    public boolean validToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    public UUID getUserId(String token) {
        String id = getClaims(token).get("id", String.class);
        return UUID.fromString(id);
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        String email = getSubject(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        CustomUserDetails userDetails = new CustomUserDetails(user);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}