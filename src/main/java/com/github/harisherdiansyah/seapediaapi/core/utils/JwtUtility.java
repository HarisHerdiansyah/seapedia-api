package com.github.harisherdiansyah.seapediaapi.core.utils;

import com.github.harisherdiansyah.seapediaapi.features.authentication.UserPrincipalEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtility {
    @Value("${jwt.secret}")
    private String secret;

    /** Access token expiration in milliseconds (default: 900000 = 15 minutes). */
    @Value("${jwt.access-token.expiration}")
    private Long atExp;

    /** Refresh token expiration in milliseconds (default: 604800000 = 7 days). */
    @Value("${jwt.refresh-token.expiration}")
    private Long rtExp;

    private SecretKey getSignKey() {
        byte[] secretBytes = Decoders.BASE64URL.decode(secret);
        return Keys.hmacShaKeyFor(secretBytes);
    }

    private String generateToken(Map<String, Object> claims, UserDetails principal, long expMillis) {
        return Jwts.builder()
                .claims(claims)
                .subject(principal.getUsername())
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expMillis))
                .signWith(getSignKey())
                .compact();
    }

    public String generateAccessToken(Map<String, Object> claims, UserDetails principal) {
        return generateToken(claims, principal, atExp);
    }

    public String generateRefreshToken(Map<String, Object> claims, UserDetails principal) {
        return generateToken(claims, principal, rtExp);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractJti(String token) {
        return extractClaim(token, Claims::getId);
    }

    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(extractClaim(token, claims -> claims.get("userId", String.class)));
    }

    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", List.class));
    }

    public Authentication getAuthentication(String token) {
        List<GrantedAuthority> authorities = extractRoles(token)
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserPrincipalEntity userPrincipal = UserPrincipalEntity.builder()
                .userId(extractUserId(token))
                .username(extractSubject(token))
                .password("")
                .authorities(authorities)
                .build();

        return new UsernamePasswordAuthenticationToken(userPrincipal, null, authorities);
    }

    public ResponseCookie buildRefreshTokenCookie(String refreshToken) {
        boolean hasToken = StringUtils.hasText(refreshToken);
        return ResponseCookie.from("refreshToken", hasToken ? refreshToken : "")
                .httpOnly(true)
                .secure(false)  // TODO: set true in production
                .sameSite("Strict")
                .maxAge(hasToken ? rtExp / 1000 : 0)  // maxAge expects seconds
                .path("/")
                .build();
    }
}
