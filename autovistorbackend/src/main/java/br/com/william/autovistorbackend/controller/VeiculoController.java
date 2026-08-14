package br.com.william.autovistorbackend.controller;

import br.com.william.autovistorbackend.dto.VeiculoCadastroRequest;
import br.com.william.autovistorbackend.dto.VeiculoResponse;
import br.com.william.autovistorbackend.service.VeiculoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/veiculos")
@RequiredArgsConstructor
public class VeiculoController {

    private final VeiculoService veiculoService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'GERENTE')")
    public ResponseEntity<VeiculoResponse> cadastrar(@Valid @RequestBody VeiculoCadastroRequest request) {
        VeiculoResponse response = veiculoService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'GERENTE')")
    public ResponseEntity<VeiculoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(veiculoService.buscarPorId(id));
    }

    @GetMapping("/cliente/{idCliente}")
    @PreAuthorize("hasRole('GERENTE') or (hasRole('CLIENTE') and #idCliente == authentication.principal.id)")
    public ResponseEntity<List<VeiculoResponse>> listarPorCliente(@PathVariable Long idCliente) {
        return ResponseEntity.ok(veiculoService.listarPorCliente(idCliente));
    }
}