package br.com.william.autovistorbackend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey chave;
    private final long expiracaoMs;

    public JwtService(
            @Value("${jwt.secret}") String segredo,
            @Value("${jwt.expiration-ms}") long expiracaoMs) {
        this.chave = Keys.hmacShaKeyFor(segredo.getBytes(StandardCharsets.UTF_8));
        this.expiracaoMs = expiracaoMs;
    }

    public String gerarToken(AutenticadoPrincipal principal) {
        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + expiracaoMs);

        return Jwts.builder()
                .subject(principal.getUsername()) // e-mail
                .claim("id", principal.getId())
                .claim("tipo", principal.getTipo())
                .claim("role", principal.getAuthorities().iterator().next().getAuthority())
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(chave)
                .compact();
    }

    public String extrairEmail(String token) {
        return parseClaims(token).getPayload().getSubject();
    }

    public boolean tokenValido(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private io.jsonwebtoken.Jws<io.jsonwebtoken.Claims> parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token);
    }
}