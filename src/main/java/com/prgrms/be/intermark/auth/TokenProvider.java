package com.prgrms.be.intermark.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;

import static com.prgrms.be.intermark.auth.constant.JwtConstants.ACCESS_TOKEN_EXP;
import static com.prgrms.be.intermark.auth.constant.JwtConstants.REFRESH_TOKEN_EXP;


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

    public String createAceessToken(Long userId){
        return Jwts.builder()
                .setSubject(userId.toString())
                .setExpiration(new Date(new Date().getTime() + ACCESS_TOKEN_EXP))
                .signWith(accessKey,SignatureAlgorithm.HS256)
                .compact();
    }
    public String createRefreshToken(Long userId){
        return Jwts.builder()
                .setSubject(userId.toString())
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
}
