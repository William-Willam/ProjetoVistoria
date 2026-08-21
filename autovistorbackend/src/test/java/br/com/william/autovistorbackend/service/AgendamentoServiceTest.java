package br.com.william.autovistorbackend.service;

import br.com.william.autovistorbackend.dto.DesignarVistoriadorRequest;
import br.com.william.autovistorbackend.entity.*;
import br.com.william.autovistorbackend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgendamentoServiceTest {

    @Mock private AgendamentoRepository agendamentoRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private VeiculoRepository veiculoRepository;
    @Mock private FuncionarioRepository funcionarioRepository;

    @InjectMocks
    private AgendamentoService agendamentoService;

    private Agendamento agendamento;
    private Funcionario funcionario;

    @BeforeEach
    void setUp() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Veiculo veiculo = new Veiculo();
        veiculo.setId(1L);

        agendamento = new Agendamento();
        agendamento.setId(10L);
        agendamento.setDataAgendamento(LocalDate.of(2026, 9, 15));
        agendamento.setHora(LocalTime.of(10, 30));
        agendamento.setCliente(cliente);
        agendamento.setVeiculo(veiculo);
        agendamento.setStatusAgendamento(Agendamento.StatusAgendamento.PENDENTE);

        funcionario = new Funcionario();
        funcionario.setId(5L);
    }

    @Test
    void designarVistoriador_deveLancarExcecao_quandoJaExisteConflitoDeHorario() {
        when(agendamentoRepository.findById(10L)).thenReturn(Optional.of(agendamento));
        when(funcionarioRepository.findById(5L)).thenReturn(Optional.of(funcionario));
        when(agendamentoRepository.existsByDataAgendamentoAndHoraAndFuncionarioIdAndStatusAgendamentoNotIn(
                any(), any(), eq(5L), anyList())).thenReturn(true);

        DesignarVistoriadorRequest request = new DesignarVistoriadorRequest(5L);

        assertThatThrownBy(() -> agendamentoService.designarVistoriador(10L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("já possui um agendamento");

        assertThat(agendamento.getFuncionario()).isNull();
        assertThat(agendamento.getStatusAgendamento()).isEqualTo(Agendamento.StatusAgendamento.PENDENTE);
    }

    @Test
    void designarVistoriador_deveConfirmarAgendamento_quandoNaoHaConflito() {
        when(agendamentoRepository.findById(10L)).thenReturn(Optional.of(agendamento));
        when(funcionarioRepository.findById(5L)).thenReturn(Optional.of(funcionario));
        when(agendamentoRepository.existsByDataAgendamentoAndHoraAndFuncionarioIdAndStatusAgendamentoNotIn(
                any(), any(), eq(5L), anyList())).thenReturn(false);

        DesignarVistoriadorRequest request = new DesignarVistoriadorRequest(5L);

        var response = agendamentoService.designarVistoriador(10L, request);

        assertThat(response.statusAgendamento()).isEqualTo(Agendamento.StatusAgendamento.CONFIRMADO);
        assertThat(response.idFuncionario()).isEqualTo(5L);
        assertThat(agendamento.getFuncionario()).isEqualTo(funcionario);
    }
}