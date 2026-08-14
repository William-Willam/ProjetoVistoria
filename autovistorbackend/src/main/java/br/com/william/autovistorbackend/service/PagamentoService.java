package br.com.william.autovistorbackend.service;

import br.com.william.autovistorbackend.dto.PagamentoCadastroRequest;
import br.com.william.autovistorbackend.dto.PagamentoResponse;
import br.com.william.autovistorbackend.entity.*;
import br.com.william.autovistorbackend.exception.RecursoDuplicadoException;
import br.com.william.autovistorbackend.exception.RecursoNaoEncontradoException;
import br.com.william.autovistorbackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final VistoriaRepository vistoriaRepository;
    private final BoletoRepository boletoRepository;
    private final NotaFiscalRepository notaFiscalRepository;
    private final LancamentoCaixaRepository lancamentoCaixaRepository;

    @Transactional
    public PagamentoResponse registrar(Long idVistoria, PagamentoCadastroRequest request) {

        Vistoria vistoria = vistoriaRepository.findById(idVistoria)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Vistoria não encontrada."));

        if (pagamentoRepository.findByVistoriaId(idVistoria).isPresent()) {
            throw new RecursoDuplicadoException("Esta vistoria já possui um pagamento registrado.");
        }

        Pagamento pagamento = new Pagamento();
        pagamento.setFormaPagamento(request.formaPagamento());
        pagamento.setValor(request.valor());
        pagamento.setVistoria(vistoria);

        if (request.formaPagamento() == Pagamento.FormaPagamento.BOLETO) {
            pagamento = pagamentoRepository.save(pagamento);
            gerarBoleto(pagamento);
        } else {
            // formas de pagamento "instantâneas" já nascem confirmadas
            pagamento.setStatusPagamento(Pagamento.StatusPagamento.PAGO);
            pagamento.setDataPagamento(LocalDate.now());
            pagamento = pagamentoRepository.save(pagamento);
            confirmarPagamento(pagamento);
        }

        return toResponse(pagamento);
    }

    @Transactional
    public PagamentoResponse confirmarPagamentoBoleto(Long idPagamento) {
        Pagamento pagamento = pagamentoRepository.findById(idPagamento)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pagamento não encontrado."));

        if (pagamento.getStatusPagamento() == Pagamento.StatusPagamento.PAGO) {
            throw new IllegalArgumentException("Este pagamento já está confirmado.");
        }

        pagamento.setStatusPagamento(Pagamento.StatusPagamento.PAGO);
        pagamento.setDataPagamento(LocalDate.now());

        boletoRepository.findByPagamentoId(idPagamento).ifPresent(boleto ->
                boleto.setStatus(Boleto.Status.PAGO)
        );

        confirmarPagamento(pagamento);

        return toResponse(pagamento);
    }

    // ================== ORQUESTRAÇÃO DOS EFEITOS DO PAGAMENTO ==================

    private void gerarBoleto(Pagamento pagamento) {
        Boleto boleto = new Boleto();
        boleto.setCodigoBarras(gerarCodigoBarrasFicticio());
        boleto.setDataVencimento(LocalDate.now().plusDays(3));
        boleto.setPagamento(pagamento);
        boletoRepository.save(boleto);
    }

    private void confirmarPagamento(Pagamento pagamento) {
        if (!notaFiscalRepository.existsByPagamentoId(pagamento.getId())) {
            gerarNotaFiscal(pagamento);
        }
        gerarLancamentoCaixa(pagamento);
    }

    private void gerarNotaFiscal(Pagamento pagamento) {
        NotaFiscal notaFiscal = new NotaFiscal();
        notaFiscal.setNumero(gerarNumeroNotaFiscal());
        notaFiscal.setPagamento(pagamento);
        notaFiscalRepository.save(notaFiscal);
    }

    private void gerarLancamentoCaixa(Pagamento pagamento) {
        LancamentoCaixa lancamento = new LancamentoCaixa();
        lancamento.setTipo(LancamentoCaixa.Tipo.ENTRADA);
        lancamento.setValor(pagamento.getValor());
        lancamento.setDescricao("Pagamento de vistoria #" + pagamento.getVistoria().getId());
        lancamento.setPagamento(pagamento);
        lancamentoCaixaRepository.save(lancamento);
    }

    private String gerarCodigoBarrasFicticio() {
        return "23793.38128 60082.216012 00000.000000 1 " + System.currentTimeMillis();
    }

    private String gerarNumeroNotaFiscal() {
        return "NF-" + LocalDateTime.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private PagamentoResponse toResponse(Pagamento p) {
        return new PagamentoResponse(
                p.getId(), p.getFormaPagamento(), p.getStatusPagamento(),
                p.getValor(), p.getDataPagamento(), p.getVistoria().getId()
        );
    }
}