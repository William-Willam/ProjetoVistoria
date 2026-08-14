package br.com.william.autovistorbackend.controller;

import br.com.william.autovistorbackend.dto.ClienteAtualizacaoRequest;
import br.com.william.autovistorbackend.dto.ClienteCadastroRequest;
import br.com.william.autovistorbackend.dto.ClienteResponse;
import br.com.william.autovistorbackend.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<ClienteResponse> cadastrar(@Valid @RequestBody ClienteCadastroRequest request) {
        ClienteResponse response = clienteService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE') or (hasRole('CLIENTE') and #id == authentication.principal.id)")
    public ResponseEntity<ClienteResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE') or (hasRole('CLIENTE') and #id == authentication.principal.id)")
    public ResponseEntity<ClienteResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClienteAtualizacaoRequest request) {
        return ResponseEntity.ok(clienteService.atualizar(id, request));
    }
}