package br.com.william.autovistorbackend.controller;

import br.com.william.autovistorbackend.dto.LoginRequest;
import br.com.william.autovistorbackend.dto.LoginResponse;
import br.com.william.autovistorbackend.security.AutenticadoPrincipal;
import br.com.william.autovistorbackend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.senha())
            );

            AutenticadoPrincipal principal = (AutenticadoPrincipal) authentication.getPrincipal();
            String token = jwtService.gerarToken(principal);
            String role = principal.getAuthorities().iterator().next().getAuthority();

            return ResponseEntity.ok(new LoginResponse(token, principal.getTipo(), role));

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("E-mail ou senha inválidos.");
        }
    }
}