package br.com.william.autovistorbackend.controller;

import br.com.william.autovistorbackend.dto.FotoVistoriaResponse;
import br.com.william.autovistorbackend.dto.VistoriaCadastroRequest;
import br.com.william.autovistorbackend.dto.VistoriaResponse;
import br.com.william.autovistorbackend.security.AutenticadoPrincipal;
import br.com.william.autovistorbackend.service.VistoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(value = "/{idVistoria}/fotos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('VISTORIADOR')")
    public ResponseEntity<FotoVistoriaResponse> uploadFoto(
            @PathVariable Long idAgendamento,
            @PathVariable Long idVistoria,
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam(value = "descricao", required = false) String descricao) {
        FotoVistoriaResponse response = vistoriaService.uploadFoto(idVistoria, arquivo, descricao);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/fotos/{idFoto}/download")
    @PreAuthorize("hasAnyRole('CLIENTE', 'GERENTE', 'VISTORIADOR')")
    public ResponseEntity<Resource> downloadFoto(
            @PathVariable Long idAgendamento,
            @PathVariable Long idFoto) {
        byte[] foto = vistoriaService.baixarFoto(idFoto);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(new ByteArrayResource(foto));
    }
}