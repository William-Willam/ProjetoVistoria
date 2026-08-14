package br.com.william.autovistorbackend.service;

import br.com.william.autovistorbackend.dto.VistoriaCadastroRequest;
import br.com.william.autovistorbackend.dto.VistoriaResponse;
import br.com.william.autovistorbackend.entity.Agendamento;
import br.com.william.autovistorbackend.entity.Funcionario;
import br.com.william.autovistorbackend.entity.Vistoria;
import br.com.william.autovistorbackend.exception.RecursoDuplicadoException;
import br.com.william.autovistorbackend.exception.RecursoNaoEncontradoException;
import br.com.william.autovistorbackend.repository.AgendamentoRepository;
import br.com.william.autovistorbackend.repository.FuncionarioRepository;
import br.com.william.autovistorbackend.repository.VistoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class VistoriaService {

    private final VistoriaRepository vistoriaRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final FuncionarioRepository funcionarioRepository;

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

        Vistoria salva = vistoriaRepository.save(vistoria);

        agendamento.setStatusAgendamento(Agendamento.StatusAgendamento.CONCLUIDO);

        return toResponse(salva);
    }

    public VistoriaResponse buscarPorAgendamento(Long idAgendamento) {
        Vistoria vistoria = vistoriaRepository.findByAgendamentoId(idAgendamento)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Vistoria não encontrada para este agendamento."));
        return toResponse(vistoria);
    }

    private VistoriaResponse toResponse(Vistoria v) {
        return new VistoriaResponse(
                v.getId(), v.getDataVistoria(), v.getResultado(), v.getObservacoes(),
                v.getAgendamento().getId(), v.getFuncionario().getId()
        );
    }
}