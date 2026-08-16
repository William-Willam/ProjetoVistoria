package br.com.william.autovistorbackend.service;

import br.com.william.autovistorbackend.dto.*;
import br.com.william.autovistorbackend.entity.*;
import br.com.william.autovistorbackend.exception.RecursoDuplicadoException;
import br.com.william.autovistorbackend.exception.RecursoNaoEncontradoException;
import br.com.william.autovistorbackend.repository.AgendamentoRepository;
import br.com.william.autovistorbackend.repository.FotoVistoriaRepository;
import br.com.william.autovistorbackend.repository.FuncionarioRepository;
import br.com.william.autovistorbackend.repository.VistoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VistoriaService {

    private final VistoriaRepository vistoriaRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final FotoVistoriaRepository fotoVistoriaRepository;

    @Value("${app.fotos.diretorio}")
    private String diretorioFotos;

    @Transactional
    public VistoriaResponse registrar(Long idAgendamento, Long idFuncionarioLogado, VistoriaCadastroRequest request) {

        Agendamento agendamento = agendamentoRepository.findById(idAgendamento)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento não encontrado."));

        if (agendamento.getStatusAgendamento() != Agendamento.StatusAgendamento.CONFIRMADO) {
            throw new IllegalArgumentException("Só é possível registrar vistoria de um agendamento confirmado.");
        }
        if (agendamento.getFuncionario() == null) {
            throw new IllegalArgumentException("Este agendamento não tem vistoriador designado.");
        }
        if (!agendamento.getFuncionario().getId().equals(idFuncionarioLogado)) {
            throw new IllegalArgumentException("Você não é o vistoriador designado para este agendamento.");
        }
        if (vistoriaRepository.existsByAgendamentoId(idAgendamento)) {
            throw new RecursoDuplicadoException("Este agendamento já possui uma vistoria registrada.");
        }

        Funcionario funcionario = funcionarioRepository.findById(idFuncionarioLogado)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Funcionário não encontrado."));

        Vistoria vistoria = new Vistoria();
        vistoria.setDataVistoria(LocalDate.now());
        vistoria.setResultado(request.resultado());
        vistoria.setObservacoes(request.observacoes());
        vistoria.setAgendamento(agendamento);
        vistoria.setFuncionario(funcionario);

        for (ItemVistoriaRequest itemRequest : request.itens()) {
            ItemVistoria item = new ItemVistoria();
            item.setNomeItem(itemRequest.nomeItem());
            item.setSituacao(itemRequest.situacao());
            item.setObservacao(itemRequest.observacao());
            item.setVistoria(vistoria);
            vistoria.getItens().add(item);
        }

        Vistoria salva = vistoriaRepository.save(vistoria);

        agendamento.setStatusAgendamento(Agendamento.StatusAgendamento.CONCLUIDO);

        return toResponse(salva);
    }

    @Transactional(readOnly = true)
    public VistoriaResponse buscarPorAgendamento(Long idAgendamento) {
        Vistoria vistoria = vistoriaRepository.findByAgendamentoId(idAgendamento)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Vistoria não encontrada para este agendamento."));
        return toResponse(vistoria);
    }

    @Transactional
    public FotoVistoriaResponse uploadFoto(Long idVistoria, MultipartFile arquivo, String descricao) {
        Vistoria vistoria = vistoriaRepository.findById(idVistoria)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Vistoria não encontrada."));

        String extensao = extrairExtensao(arquivo.getOriginalFilename());
        String nomeArquivo = "vistoria-" + idVistoria + "-" + UUID.randomUUID() + extensao;
        Path caminhoCompleto = Path.of(diretorioFotos, nomeArquivo);

        try {
            Files.createDirectories(caminhoCompleto.getParent());
            Files.write(caminhoCompleto, arquivo.getBytes());
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao salvar foto.", e);
        }

        FotoVistoria foto = new FotoVistoria();
        foto.setCaminhoArquivo(nomeArquivo);
        foto.setDescricao(descricao);
        foto.setVistoria(vistoria);

        FotoVistoria salva = fotoVistoriaRepository.save(foto);
        return new FotoVistoriaResponse(salva.getId(), salva.getCaminhoArquivo(), salva.getDescricao(), salva.getDataUpload());
    }

    public byte[] baixarFoto(Long idFoto) {
        FotoVistoria foto = fotoVistoriaRepository.findById(idFoto)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Foto não encontrada."));
        try {
            return Files.readAllBytes(Path.of(diretorioFotos, foto.getCaminhoArquivo()));
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao ler foto.", e);
        }
    }

    private String extrairExtensao(String nomeOriginal) {
        if (nomeOriginal == null || !nomeOriginal.contains(".")) return "";
        return nomeOriginal.substring(nomeOriginal.lastIndexOf('.'));
    }

    private VistoriaResponse toResponse(Vistoria v) {
        List<ItemVistoriaResponse> itens = v.getItens().stream()
                .map(i -> new ItemVistoriaResponse(i.getId(), i.getNomeItem(), i.getSituacao(), i.getObservacao()))
                .toList();

        List<FotoVistoriaResponse> fotos = v.getFotos().stream()
                .map(f -> new FotoVistoriaResponse(f.getId(), f.getCaminhoArquivo(), f.getDescricao(), f.getDataUpload()))
                .toList();

        return new VistoriaResponse(
                v.getId(), v.getDataVistoria(), v.getResultado(), v.getObservacoes(),
                v.getAgendamento().getId(), v.getFuncionario().getId(), itens, fotos
        );
    }
}