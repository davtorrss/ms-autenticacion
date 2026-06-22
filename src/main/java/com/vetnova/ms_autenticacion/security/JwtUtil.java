package com.vetnova.ms_autenticacion.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Lee la clave secreta desde el archivo application.yml
    @Value("${jwt.secret}")
    private String secret;

    // Lee el tiempo de expiración desde el archivo application.yml
    @Value("${jwt.expiration}")
    private Long expiration;

    // Método interno para encriptar la clave secreta
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // 1. FABRICAR EL TOKEN
    public String generateToken(String username, String rol) {
        return Jwts.builder()
                .setSubject(username) 
                .claim("rol", rol)    // Guardamos el rol (ej: Administrador, Recepcionista)
                .setIssuedAt(new Date()) 
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // Fecha de muerte
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Firma digital
                .compact();
    }

    // 2. LEER EL TOKEN (Extraer el nombre de usuario)
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 3. VALIDAR EL TOKEN (Saber si es falso o si ya expiró)
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true; 
        } catch (Exception e) {
            return false; 
        }
    }
}