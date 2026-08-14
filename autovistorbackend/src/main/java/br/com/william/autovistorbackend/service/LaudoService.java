package br.com.william.autovistorbackend.service;

import br.com.william.autovistorbackend.dto.LaudoResponse;
import br.com.william.autovistorbackend.entity.Laudo;
import br.com.william.autovistorbackend.entity.Vistoria;
import br.com.william.autovistorbackend.exception.RecursoDuplicadoException;
import br.com.william.autovistorbackend.exception.RecursoNaoEncontradoException;
import br.com.william.autovistorbackend.repository.LaudoRepository;
import br.com.william.autovistorbackend.repository.VistoriaRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class LaudoService {

    private final LaudoRepository laudoRepository;
    private final VistoriaRepository vistoriaRepository;

    @Value("${app.laudos.diretorio}")
    private String diretorioLaudos;

    @Transactional
    public LaudoResponse gerar(Long idVistoria) {

        Vistoria vistoria = vistoriaRepository.findById(idVistoria)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Vistoria não encontrada."));

        if (laudoRepository.findByVistoriaId(idVistoria).isPresent()) {
            throw new RecursoDuplicadoException("Esta vistoria já possui um laudo gerado.");
        }

        String nomeArquivo = "laudo-vistoria-" + idVistoria + ".pdf";
        Path caminhoCompleto = Path.of(diretorioLaudos, nomeArquivo);

        criarPdf(vistoria, caminhoCompleto);

        Laudo laudo = new Laudo();
        laudo.setCaminhoArquivo(nomeArquivo); // caminho RELATIVO, nunca absoluto
        laudo.setVistoria(vistoria);

        Laudo salvo = laudoRepository.save(laudo);
        return toResponse(salvo);
    }

    public Path resolverCaminhoArquivo(Long idVistoria) {
        Laudo laudo = laudoRepository.findByVistoriaId(idVistoria)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Laudo não encontrado para esta vistoria."));
        return Path.of(diretorioLaudos, laudo.getCaminhoArquivo());
    }

    private void criarPdf(Vistoria vistoria, Path caminhoCompleto) {
        try {
            Files.createDirectories(caminhoCompleto.getParent());

            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage();
                document.addPage(page);

                var fonteTitulo = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                var fonteTexto = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
                float margem = 50;
                float y = page.getMediaBox().getHeight() - margem;

                try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                    cs.beginText();
                    cs.setFont(fonteTitulo, 18);
                    cs.newLineAtOffset(margem, y);
                    cs.showText("RELATÓRIO DE VISTORIA");
                    cs.endText();
                    y -= 40;

                    y = escreverLinha(cs, fonteTexto, margem, y, "Data da vistoria: " +
                            vistoria.getDataVistoria().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    y = escreverLinha(cs, fonteTexto, margem, y, "Resultado: " + vistoria.getResultado());
                    y = escreverLinha(cs, fonteTexto, margem, y, "Vistoriador: " + vistoria.getFuncionario().getNome());
                    y = escreverLinha(cs, fonteTexto, margem, y, "Cliente: " + vistoria.getAgendamento().getCliente().getNome());
                    y = escreverLinha(cs, fonteTexto, margem, y, "Veículo: " + vistoria.getAgendamento().getVeiculo().getNomeVeiculo()
                            + " - Placa: " + vistoria.getAgendamento().getVeiculo().getPlaca());
                    y -= 10;
                    escreverLinha(cs, fonteTexto, margem, y, "Observações: " + vistoria.getObservacoes());
                }

                document.save(caminhoCompleto.toFile());
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao gerar PDF do laudo.", e);
        }
    }

    private float escreverLinha(PDPageContentStream cs, PDType1Font fonte, float x, float y, String texto) throws IOException {
        cs.beginText();
        cs.setFont(fonte, 12);
        cs.newLineAtOffset(x, y);
        cs.showText(texto);
        cs.endText();
        return y - 20;
    }

    private LaudoResponse toResponse(Laudo l) {
        return new LaudoResponse(l.getId(), l.getCaminhoArquivo(), l.getDataGeracao(), l.getVistoria().getId());
    }
}