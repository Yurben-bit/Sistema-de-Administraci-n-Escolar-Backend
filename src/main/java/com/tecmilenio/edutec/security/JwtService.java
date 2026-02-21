package com.tecmilenio.edutec.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.security.Key;

@Service // Le dice a Spring que esta clase es un componente que puede ser inyectado en
         // otros lados.
public class JwtService {
    // Clave de al menos 32 caracteres
    private static final String SECRET_KEY = "12345678910111213141516171819200";
    // pre-generando la llave criptográfica solo cuando inicia la aplicación
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                // Fíjate en el orden: primero la llave, luego el algoritmo
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

}
