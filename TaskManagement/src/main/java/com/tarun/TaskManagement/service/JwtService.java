package com.tarun.TaskManagement.service;

import com.tarun.TaskManagement.model.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final String secretKey = "KyubcZLn7cdAsXaWed862PfrVFityJlu8t0b9793vWo=";

    public String generateToken(Users user) {

        Map<String,Object> claims = new HashMap<>();

        claims.put("role",user.getRole());
        claims.put("name",user.getName());

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .and()
                .signWith(getKey(), Jwts.SIG.HS256)
                .compact();
    }

    public String generateRefreshToken(Users user) {

        Map<String,Object> claims = new HashMap<>();
        claims.put("role",user.getRole());

        String refreshToken = Jwts.builder()
                .claims()
                .add(claims)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 60 ))
                .and()
                .signWith(getKey(), Jwts.SIG.HS256)
                .compact();

        System.out.println(user.getUsername() + " refresh token : " + refreshToken);
        return refreshToken;
    }

    public SecretKey getKey() {
        byte[] sk = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(sk);

    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims,T> claimResolver){
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }
}
