package br.com.william.autovistorbackend.controller;

import br.com.william.autovistorbackend.dto.RelatorioFinanceiroResponse;
import br.com.william.autovistorbackend.dto.RelatorioOperacionalResponse;
import br.com.william.autovistorbackend.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/relatorios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('GERENTE')")
public class RelatorioController {

    private final RelatorioService relatorioService;

    @GetMapping("/operacional")
    public ResponseEntity<RelatorioOperacionalResponse> operacional(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(relatorioService.gerarOperacional(inicio, fim));
    }

    @GetMapping("/financeiro")
    public ResponseEntity<RelatorioFinanceiroResponse> financeiro(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(relatorioService.gerarFinanceiro(inicio, fim));
    }
}