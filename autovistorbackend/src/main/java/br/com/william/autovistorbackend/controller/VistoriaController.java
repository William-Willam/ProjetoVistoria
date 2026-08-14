package br.com.william.autovistorbackend.controller;

import br.com.william.autovistorbackend.dto.VistoriaCadastroRequest;
import br.com.william.autovistorbackend.dto.VistoriaResponse;
import br.com.william.autovistorbackend.security.AutenticadoPrincipal;
import br.com.william.autovistorbackend.service.VistoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/agendamentos/{idAgendamento}/vistoria")
@RequiredArgsConstructor
public class VistoriaController {

    private final VistoriaService vistoriaService;

    @PostMapping
    @PreAuthorize("hasRole('VISTORIADOR')")
    public ResponseEntity<VistoriaResponse> registrar(
            @PathVariable Long idAgendamento,
            @Valid @RequestBody VistoriaCadastroRequest request,
            @AuthenticationPrincipal AutenticadoPrincipal principal) {
        VistoriaResponse response = vistoriaService.registrar(idAgendamento, principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'GERENTE', 'VISTORIADOR')")
    public ResponseEntity<VistoriaResponse> buscarPorAgendamento(@PathVariable Long idAgendamento) {
        return ResponseEntity.ok(vistoriaService.buscarPorAgendamento(idAgendamento));
    }
}