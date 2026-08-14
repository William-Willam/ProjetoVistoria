package br.com.william.autovistorbackend.controller;

import br.com.william.autovistorbackend.dto.LaudoResponse;
import br.com.william.autovistorbackend.service.LaudoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;

@RestController
@RequestMapping("/vistorias/{idVistoria}/laudo")
@RequiredArgsConstructor
public class LaudoController {

    private final LaudoService laudoService;

    @PostMapping
    @PreAuthorize("hasRole('VISTORIADOR')")
    public ResponseEntity<LaudoResponse> gerar(@PathVariable Long idVistoria) {
        LaudoResponse response = laudoService.gerar(idVistoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/download")
    @PreAuthorize("hasAnyRole('CLIENTE', 'GERENTE', 'VISTORIADOR')")
    public ResponseEntity<Resource> download(@PathVariable Long idVistoria) {
        Path caminho = laudoService.resolverCaminhoArquivo(idVistoria);
        Resource recurso = new FileSystemResource(caminho);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + caminho.getFileName() + "\"")
                .body(recurso);
    }
}