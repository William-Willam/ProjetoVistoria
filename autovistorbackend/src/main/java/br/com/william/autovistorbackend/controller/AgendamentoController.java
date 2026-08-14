package br.com.william.autovistorbackend.controller;

import br.com.william.autovistorbackend.dto.*;
import br.com.william.autovistorbackend.security.AutenticadoPrincipal;
import br.com.william.autovistorbackend.service.AgendamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<AgendamentoResponse> cadastrar(
            @Valid @RequestBody AgendamentoCadastroRequest request,
            @AuthenticationPrincipal AutenticadoPrincipal principal) {
        AgendamentoResponse response = agendamentoService.cadastrar(request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/reagendar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<AgendamentoResponse> reagendar(
            @PathVariable Long id,
            @Valid @RequestBody ReagendamentoRequest request,
            @AuthenticationPrincipal AutenticadoPrincipal principal) {
        return ResponseEntity.ok(agendamentoService.reagendar(id, principal.getId(), request));
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Void> cancelar(
            @PathVariable Long id,
            @AuthenticationPrincipal AutenticadoPrincipal principal) {
        agendamentoService.cancelar(id, principal.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/designar-vistoriador")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<AgendamentoResponse> designarVistoriador(
            @PathVariable Long id,
            @Valid @RequestBody DesignarVistoriadorRequest request) {
        return ResponseEntity.ok(agendamentoService.designarVistoriador(id, request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'GERENTE', 'VISTORIADOR')")
    public ResponseEntity<AgendamentoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(agendamentoService.buscarPorId(id));
    }

    @GetMapping("/cliente/{idCliente}")
    @PreAuthorize("hasRole('GERENTE') or (hasRole('CLIENTE') and #idCliente == authentication.principal.id)")
    public ResponseEntity<List<AgendamentoResponse>> listarPorCliente(@PathVariable Long idCliente) {
        return ResponseEntity.ok(agendamentoService.listarPorCliente(idCliente));
    }
}