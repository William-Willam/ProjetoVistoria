package br.com.william.autovistorbackend.service;

import br.com.william.autovistorbackend.dto.RelatorioFinanceiroResponse;
import br.com.william.autovistorbackend.dto.RelatorioOperacionalResponse;
import br.com.william.autovistorbackend.entity.Agendamento;
import br.com.william.autovistorbackend.repository.AgendamentoRepository;
import br.com.william.autovistorbackend.repository.PagamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final AgendamentoRepository agendamentoRepository;
    private final PagamentoRepository pagamentoRepository;

    public RelatorioOperacionalResponse gerarOperacional(LocalDate inicio, LocalDate fim) {

        Map<String, Long> porStatus = new LinkedHashMap<>();
        for (Agendamento.StatusAgendamento status : Agendamento.StatusAgendamento.values()) {
            long quantidade = agendamentoRepository
                    .countByStatusAgendamentoAndDataAgendamentoBetween(status, inicio, fim);
            porStatus.put(status.name(), quantidade);
        }

        return new RelatorioOperacionalResponse(inicio, fim, porStatus);
    }

    public RelatorioFinanceiroResponse gerarFinanceiro(LocalDate inicio, LocalDate fim) {

        BigDecimal totalRecebido = pagamentoRepository.somarTotalRecebido(inicio, fim);

        Map<String, BigDecimal> porForma = new LinkedHashMap<>();
        pagamentoRepository.somarPorFormaPagamento(inicio, fim)
                .forEach(item -> porForma.put(item.getForma().name(), item.getTotal()));

        return new RelatorioFinanceiroResponse(inicio, fim, totalRecebido, porForma);
    }
}