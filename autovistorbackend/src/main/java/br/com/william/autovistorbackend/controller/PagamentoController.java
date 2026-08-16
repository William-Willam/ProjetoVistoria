package br.com.william.autovistorbackend.controller;

import br.com.william.autovistorbackend.dto.BoletoDetalhadoResponse;
import br.com.william.autovistorbackend.dto.PagamentoCadastroRequest;
import br.com.william.autovistorbackend.dto.PagamentoResponse;
import br.com.william.autovistorbackend.service.PagamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PagamentoController {

    private final PagamentoService pagamentoService;

    @PostMapping("/vistorias/{idVistoria}/pagamento")
    @PreAuthorize("hasAnyRole('VISTORIADOR', 'CLIENTE')")
    public ResponseEntity<PagamentoResponse> registrar(
            @PathVariable Long idVistoria,
            @Valid @RequestBody PagamentoCadastroRequest request) {
        PagamentoResponse response = pagamentoService.registrar(idVistoria, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/pagamentos/{idPagamento}/confirmar-boleto")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<PagamentoResponse> confirmarPagamentoBoleto(@PathVariable Long idPagamento) {
        return ResponseEntity.ok(pagamentoService.confirmarPagamentoBoleto(idPagamento));
    }

    @GetMapping("/pagamentos/boletos-pendentes")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<List<BoletoDetalhadoResponse>> listarBoletosPendentes() {
        return ResponseEntity.ok(pagamentoService.listarBoletosPendentes());
    }
}