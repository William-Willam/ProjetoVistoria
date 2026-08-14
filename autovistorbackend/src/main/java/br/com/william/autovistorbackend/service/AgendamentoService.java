package br.com.william.autovistorbackend.service;

import br.com.william.autovistorbackend.dto.*;
import br.com.william.autovistorbackend.entity.Agendamento;
import br.com.william.autovistorbackend.entity.Cliente;
import br.com.william.autovistorbackend.entity.Funcionario;
import br.com.william.autovistorbackend.entity.Veiculo;
import br.com.william.autovistorbackend.exception.RecursoNaoEncontradoException;
import br.com.william.autovistorbackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;
    private final FuncionarioRepository funcionarioRepository;

    // status que NÃO bloqueiam um novo agendamento no mesmo horário
    private static final List<Agendamento.StatusAgendamento> STATUS_IRRELEVANTES = List.of(
            Agendamento.StatusAgendamento.CANCELADO,
            Agendamento.StatusAgendamento.REAGENDADO
    );

    @Transactional
    public AgendamentoResponse cadastrar(AgendamentoCadastroRequest request, Long idClienteLogado) {
        Cliente cliente = clienteRepository.findById(idClienteLogado)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado."));

        Veiculo veiculo = veiculoRepository.findById(request.idVeiculo())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veículo não encontrado."));

        Agendamento agendamento = new Agendamento();
        agendamento.setDataAgendamento(request.dataAgendamento());
        agendamento.setHora(request.hora());
        agendamento.setTipoVistoria(request.tipoVistoria());
        agendamento.setCliente(cliente);
        agendamento.setVeiculo(veiculo);
        // funcionario fica null por enquanto — designado depois pelo Gerente

        Agendamento salvo = agendamentoRepository.save(agendamento);
        return toResponse(salvo);
    }

    @Transactional
    public AgendamentoResponse designarVistoriador(Long idAgendamento, DesignarVistoriadorRequest request) {
        Agendamento agendamento = buscarEntidadePorId(idAgendamento);

        Funcionario funcionario = funcionarioRepository.findById(request.idFuncionario())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Funcionário não encontrado."));

        validarDisponibilidade(agendamento.getDataAgendamento(), agendamento.getHora(), funcionario.getId(), null);

        agendamento.setFuncionario(funcionario);
        agendamento.setStatusAgendamento(Agendamento.StatusAgendamento.CONFIRMADO);

        return toResponse(agendamento);
    }

    @Transactional
    public AgendamentoResponse reagendar(Long idAgendamento, Long idClienteLogado, ReagendamentoRequest request) {
        Agendamento agendamento = agendamentoRepository.findByIdAndClienteId(idAgendamento, idClienteLogado)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento não encontrado."));

        if (agendamento.getFuncionario() != null) {
            validarDisponibilidade(request.novaData(), request.novaHora(), agendamento.getFuncionario().getId(), idAgendamento);
        }

        agendamento.setDataAgendamento(request.novaData());
        agendamento.setHora(request.novaHora());
        agendamento.setStatusAgendamento(Agendamento.StatusAgendamento.REAGENDADO);

        return toResponse(agendamento);
    }

    @Transactional
    public void cancelar(Long idAgendamento, Long idClienteLogado) {
        Agendamento agendamento = agendamentoRepository.findByIdAndClienteId(idAgendamento, idClienteLogado)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento não encontrado."));

        agendamento.setStatusAgendamento(Agendamento.StatusAgendamento.CANCELADO);
    }

    public AgendamentoResponse buscarPorId(Long id) {
        return toResponse(buscarEntidadePorId(id));
    }

    public List<AgendamentoResponse> listarPorCliente(Long idCliente) {
        return agendamentoRepository.findByClienteId(idCliente).stream().map(this::toResponse).toList();
    }

    // ================== O CORAÇÃO DA CORREÇÃO ==================
    private void validarDisponibilidade(LocalDate data, LocalTime hora, Long idFuncionario, Long idAgendamentoIgnorar) {
        boolean conflito = agendamentoRepository
                .existsByDataAgendamentoAndHoraAndFuncionarioIdAndStatusAgendamentoNotIn(
                        data, hora, idFuncionario, STATUS_IRRELEVANTES);

        if (conflito) {
            throw new IllegalArgumentException(
                    "Este vistoriador já possui um agendamento em " + data + " às " + hora + "."
            );
        }
    }

    private Agendamento buscarEntidadePorId(Long id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento não encontrado."));
    }

    private AgendamentoResponse toResponse(Agendamento a) {
        return new AgendamentoResponse(
                a.getId(), a.getDataAgendamento(), a.getHora(), a.getTipoVistoria(), a.getStatusAgendamento(),
                a.getCliente().getId(), a.getVeiculo().getId(),
                a.getFuncionario() != null ? a.getFuncionario().getId() : null
        );
    }
}