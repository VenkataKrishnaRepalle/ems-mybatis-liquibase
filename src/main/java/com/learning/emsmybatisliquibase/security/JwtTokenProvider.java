package com.learning.emsmybatisliquibase.security;

import com.learning.emsmybatisliquibase.dao.EmployeeDao;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private final EmployeeDao employeeDao;
    @Value("${app.jwt.secret}")
    private String jwtSecretKey;
    @Value("${app.jwt-expiration-milliseconds}")
    private Long jwtExpirationDate;

    public JwtTokenProvider(EmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
    }


    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        var employee = employeeDao.get(UUID.fromString(username));
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(key())
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtSecretKey)
        );
    }

    public String getUsername(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }


    public boolean validateToken(String token) {
        Jwts.parser()
                .setSigningKey(key())
                .build()
                .parse(token);
        return true;
    }

}
