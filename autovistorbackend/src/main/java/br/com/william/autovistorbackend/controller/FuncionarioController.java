package br.com.william.autovistorbackend.controller;

import br.com.william.autovistorbackend.dto.*;
import br.com.william.autovistorbackend.service.FuncionarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/funcionarios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('GERENTE')")
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    @PostMapping
    public ResponseEntity<FuncionarioResponse> cadastrar(@Valid @RequestBody FuncionarioCadastroRequest request) {
        FuncionarioResponse response = funcionarioService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(funcionarioService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FuncionarioResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody FuncionarioAtualizacaoRequest request) {
        return ResponseEntity.ok(funcionarioService.atualizar(id, request));
    }

    @PostMapping("/{id}/desligamento")
    public ResponseEntity<Void> desligar(
            @PathVariable Long id,
            @Valid @RequestBody DesligamentoRequest request) {
        funcionarioService.desligar(id, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<FuncionarioResponse>> listarTodos() {
        return ResponseEntity.ok(funcionarioService.listarTodos());
    }
}