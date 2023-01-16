package com.prgrms.be.intermark.auth;


import com.prgrms.be.intermark.domain.user.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.DeclareMixin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import static com.prgrms.be.intermark.auth.constant.JwtConstants.*;

@Slf4j
@Component
public class TokenProvider {
    @Value("${jwt.secret.access}")
    private String ACCESS_SECRET_KEY;
    @Value("${jwt.secret.refresh}")
    private String REFRESH_SECRET_KEY;

    private Key accessKey;
    private Key refreshKey;

    @PostConstruct
    public void initialize() {
        byte[] accessKeyBytes = Decoders.BASE64.decode(ACCESS_SECRET_KEY);
        this.accessKey = Keys.hmacShaKeyFor(accessKeyBytes);

        byte[] secretKeyBytes = Decoders.BASE64.decode(REFRESH_SECRET_KEY);
        this.refreshKey = Keys.hmacShaKeyFor(secretKeyBytes);
    }

    public String createAceessToken(Long userId, UserRole role){
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim(AUTHORITIES_KEY, role)
                .setExpiration(new Date(new Date().getTime() + ACCESS_TOKEN_EXP))
                .signWith(accessKey,SignatureAlgorithm.HS256)
                .compact();
    }
    public String createRefreshToken(Long userId, UserRole role){
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim(AUTHORITIES_KEY, role)
                .setExpiration(new Date(new Date().getTime() + REFRESH_TOKEN_EXP))
                .signWith(refreshKey,SignatureAlgorithm.HS256)
                .compact();
    }
    public String getUserIdFromAccessToken(String accessToken) {
        return Jwts.parserBuilder()
                .setSigningKey(accessKey)
                .build()
                .parseClaimsJws(accessToken)
                .getBody().getSubject();
    }

    public String getUserIdFromRefreshToken(String refreshToken) {
        return Jwts.parserBuilder()
                .setSigningKey(refreshKey)
                .build()
                .parseClaimsJws(refreshToken)
                .getBody().getSubject();
    }

    public Long getExpiration(String accessToken) {
        Date expiration = Jwts.parserBuilder()
                .build()
                .parseClaimsJws(accessToken)
                .getBody().getExpiration();
        return expiration.getTime() - new Date().getTime();
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = Jwts.parserBuilder().setSigningKey(accessKey).build()
                .parseClaimsJws(accessToken).getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(new String[]{claims.get(AUTHORITIES_KEY).toString()})
                        .map(SimpleGrantedAuthority::new).toList();

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, accessToken, authorities);
    }

    public boolean validate(String accessToken) {
        return this.getTokenClaims(accessToken) != null;
    }

    private Claims getTokenClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(accessKey)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (SecurityException e) {
            log.info("Invalid JWT signature.");
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
        }
        return null;
    }
}
