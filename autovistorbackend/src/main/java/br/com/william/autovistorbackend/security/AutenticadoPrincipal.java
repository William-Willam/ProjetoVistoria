package br.com.william.autovistorbackend.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class AutenticadoPrincipal extends User {

    private final Long id;
    private final String tipo; // "CLIENTE" ou "FUNCIONARIO"

    public AutenticadoPrincipal(Long id, String email, String senhaHash, String tipo, String role) {
        super(email, senhaHash, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
        this.id = id;
        this.tipo = tipo;
    }
}