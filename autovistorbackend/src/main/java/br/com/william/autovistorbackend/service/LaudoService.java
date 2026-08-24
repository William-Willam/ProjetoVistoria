package br.com.william.autovistorbackend.service;

import br.com.william.autovistorbackend.dto.LaudoResponse;
import br.com.william.autovistorbackend.entity.FotoVistoria;
import br.com.william.autovistorbackend.entity.ItemVistoria;
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
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class LaudoService {

    private final LaudoRepository laudoRepository;
    private final VistoriaRepository vistoriaRepository;

    @Value("${app.laudos.diretorio}")
    private String diretorioLaudos;

    @Value("${app.fotos.diretorio}")
    private String diretorioFotos;

    private static final float MARGEM = 45;
    private static final float ALTURA_A4 = PDRectangle.A4.getHeight();
    private static final float LARGURA_UTIL = PDRectangle.A4.getWidth() - (MARGEM * 2);

    private static final float[] COR_PRIMARIA = rgb("#2c3e50");
    private static final float[] COR_SUCESSO = rgb("#27ae60");
    private static final float[] COR_PERIGO = rgb("#e74c3c");
    private static final float[] COR_ALERTA = rgb("#f39c12");
    private static final float[] COR_FUNDO_CLARO = rgb("#f5f6fa");
    private static final float[] COR_BORDA = rgb("#dfe4ea");
    private static final float[] COR_TEXTO_SECUNDARIO = rgb("#7f8c8d");

    private static float[] rgb(String hex) {
        int r = Integer.valueOf(hex.substring(1, 3), 16);
        int g = Integer.valueOf(hex.substring(3, 5), 16);
        int b = Integer.valueOf(hex.substring(5, 7), 16);
        return new float[]{r / 255f, g / 255f, b / 255f};
    }

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
        laudo.setCaminhoArquivo(nomeArquivo);
        laudo.setVistoria(vistoria);

        Laudo salvo = laudoRepository.save(laudo);
        return toResponse(salvo);
    }

    public Path resolverCaminhoArquivo(Long idVistoria) {
        Laudo laudo = laudoRepository.findByVistoriaId(idVistoria)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Laudo não encontrado para esta vistoria."));
        return Path.of(diretorioLaudos, laudo.getCaminhoArquivo());
    }

    private static class Cursor {
        PDPageContentStream cs;
        float y;
        int numeroPagina;
    }

    private void criarPdf(Vistoria vistoria, Path caminhoCompleto) {
        var fonteTituloEmpresa = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        var fonteTituloDocumento = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        var fonteSecao = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        var fonteTexto = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        var fonteTextoBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

        try (PDDocument document = new PDDocument()) {
            Files.createDirectories(caminhoCompleto.getParent());

            Cursor cursor = abrirPagina(document, 1);

            cursor = escreverCabecalho(document, cursor, fonteTituloEmpresa, fonteTexto, vistoria);
            cursor = escreverTitulo(document, cursor, fonteTituloDocumento, "LAUDO DE VISTORIA VEICULAR");

            cursor = escreverCaixaDados(document, cursor, fonteSecao, fonteTexto, vistoria);
            cursor = escreverParecer(document, cursor, fonteSecao, fonteTextoBold, vistoria);

            cursor = escreverSecao(document, cursor, fonteSecao, "Checklist de Itens Verificados");
            cursor = escreverChecklistTabela(document, cursor, fonteTextoBold, fonteTexto, vistoria);

            cursor = escreverSecao(document, cursor, fonteSecao, "Observações Gerais");
            cursor = escreverParagrafo(document, cursor, fonteTexto, vistoria.getObservacoes());

            if (!vistoria.getFotos().isEmpty()) {
                cursor = escreverSecao(document, cursor, fonteSecao, "Registro Fotográfico");
                for (FotoVistoria foto : vistoria.getFotos()) {
                    cursor = escreverFoto(document, cursor, fonteTexto, foto);
                }
            }

            cursor.cs.close();

            Files.deleteIfExists(caminhoCompleto);
            document.save(caminhoCompleto.toFile());

        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao gerar PDF do laudo.", e);
        }
    }

    // ================== CABEÇALHO ==================

    private Cursor escreverCabecalho(PDDocument document, Cursor cursor,
                                     PDType1Font fonteEmpresa, PDType1Font fonteTexto, Vistoria vistoria) throws IOException {

        float alturaCabecalho = 70;
        cursor = garantirEspaco(document, cursor, alturaCabecalho);

        float topoY = cursor.y;
        float textoInicioX = MARGEM + 65; // valor padrão, usado caso não haja logo

        try (InputStream is = new ClassPathResource("images/logo.png").getInputStream()) {
            byte[] bytes = is.readAllBytes();
            PDImageXObject logo = PDImageXObject.createFromByteArray(document, bytes, "logo");
            float alturaLogo = 45;
            float escalaLogo = alturaLogo / logo.getHeight();
            float larguraLogo = logo.getWidth() * escalaLogo;
            cursor.cs.drawImage(logo, MARGEM, topoY - alturaLogo, larguraLogo, alturaLogo);

            textoInicioX = MARGEM + larguraLogo + 15;
        } catch (IOException e) {
            // sem logo cadastrada — segue sem ela, texto usa a posição padrão
        }

        cursor.cs.beginText();
        cursor.cs.setFont(fonteEmpresa, 18);
        setCor(cursor.cs, COR_PRIMARIA, true);
        cursor.cs.newLineAtOffset(textoInicioX, topoY - 20);
        cursor.cs.showText("AutoVistor");
        cursor.cs.endText();

        cursor.cs.beginText();
        cursor.cs.setFont(fonteTexto, 9);
        setCor(cursor.cs, COR_TEXTO_SECUNDARIO, true);
        cursor.cs.newLineAtOffset(textoInicioX, topoY - 34);
        cursor.cs.showText("Sistema de Vistoria Veicular");
        cursor.cs.endText();

        String numeroLaudo = "Laudo Nº " + String.format("%06d", vistoria.getId());
        String dataEmissao = "Emitido em: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        cursor.cs.beginText();
        cursor.cs.setFont(fonteTexto, 10);
        setCor(cursor.cs, COR_PRIMARIA, true);
        float larguraNumero = fonteTexto.getStringWidth(numeroLaudo) / 1000 * 10;
        cursor.cs.newLineAtOffset(MARGEM + LARGURA_UTIL - larguraNumero, topoY - 12);
        cursor.cs.showText(numeroLaudo);
        cursor.cs.endText();

        cursor.cs.beginText();
        cursor.cs.setFont(fonteTexto, 8);
        setCor(cursor.cs, COR_TEXTO_SECUNDARIO, true);
        float larguraData = fonteTexto.getStringWidth(dataEmissao) / 1000 * 8;
        cursor.cs.newLineAtOffset(MARGEM + LARGURA_UTIL - larguraData, topoY - 26);
        cursor.cs.showText(dataEmissao);
        cursor.cs.endText();

        cursor.y -= alturaCabecalho;

        cursor.cs.setLineWidth(1.2f);
        setCor(cursor.cs, COR_PRIMARIA, false);
        cursor.cs.moveTo(MARGEM, cursor.y);
        cursor.cs.lineTo(MARGEM + LARGURA_UTIL, cursor.y);
        cursor.cs.stroke();
        cursor.y -= 20;

        return cursor;
    }

    private Cursor escreverTitulo(PDDocument document, Cursor cursor, PDType1Font fonte, String texto) throws IOException {
        cursor = garantirEspaco(document, cursor, 30);
        cursor.cs.beginText();
        cursor.cs.setFont(fonte, 15);
        setCor(cursor.cs, COR_PRIMARIA, true);
        cursor.cs.newLineAtOffset(MARGEM, cursor.y);
        cursor.cs.showText(texto);
        cursor.cs.endText();
        cursor.y -= 28;
        return cursor;
    }

    // ================== CAIXA DE DADOS GERAIS ==================

    private Cursor escreverCaixaDados(PDDocument document, Cursor cursor, PDType1Font fonteLabel,
                                      PDType1Font fonteValor, Vistoria vistoria) throws IOException {

        float alturaCaixa = 90;
        cursor = garantirEspaco(document, cursor, alturaCaixa + 15);

        float topoY = cursor.y;
        float baseY = topoY - alturaCaixa;

        setCor(cursor.cs, COR_FUNDO_CLARO, true);
        cursor.cs.addRect(MARGEM, baseY, LARGURA_UTIL, alturaCaixa);
        cursor.cs.fill();
        setCor(cursor.cs, COR_BORDA, false);
        cursor.cs.setLineWidth(0.8f);
        cursor.cs.addRect(MARGEM, baseY, LARGURA_UTIL, alturaCaixa);
        cursor.cs.stroke();

        float colunaEsquerdaX = MARGEM + 15;
        float colunaDireitaX = MARGEM + (LARGURA_UTIL / 2) + 10;
        float linhaY = topoY - 20;

        var veiculo = vistoria.getAgendamento().getVeiculo();
        var cliente = vistoria.getAgendamento().getCliente();

        linhaY = escreverParDados(cursor.cs, fonteLabel, fonteValor, colunaEsquerdaX, linhaY, "Cliente:", cliente.getNome());
        linhaY = escreverParDados(cursor.cs, fonteLabel, fonteValor, colunaEsquerdaX, linhaY, "Veículo:", veiculo.getNomeVeiculo() + " (" + veiculo.getModelo() + ")");
        linhaY = escreverParDados(cursor.cs, fonteLabel, fonteValor, colunaEsquerdaX, linhaY, "Placa:", veiculo.getPlaca());
        escreverParDados(cursor.cs, fonteLabel, fonteValor, colunaEsquerdaX, linhaY, "Chassi:", veiculo.getChassi());

        float linhaY2 = topoY - 20;
        linhaY2 = escreverParDados(cursor.cs, fonteLabel, fonteValor, colunaDireitaX, linhaY2, "Vistoriador:", vistoria.getFuncionario().getNome());
        linhaY2 = escreverParDados(cursor.cs, fonteLabel, fonteValor, colunaDireitaX, linhaY2, "Data:",
                vistoria.getDataVistoria().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        escreverParDados(cursor.cs, fonteLabel, fonteValor, colunaDireitaX, linhaY2, "Ano do veículo:", String.valueOf(veiculo.getAnoVeiculo()));

        cursor.y = baseY - 20;
        return cursor;
    }

    private float escreverParDados(PDPageContentStream cs, PDType1Font fonteLabel, PDType1Font fonteValor,
                                   float x, float y, String label, String valor) throws IOException {
        cs.beginText();
        cs.setFont(fonteLabel, 9);
        setCor(cs, COR_TEXTO_SECUNDARIO, true);
        cs.newLineAtOffset(x, y);
        cs.showText(label);
        cs.endText();

        cs.beginText();
        cs.setFont(fonteValor, 10);
        setCor(cs, COR_PRIMARIA, true);
        cs.newLineAtOffset(x + 75, y);
        cs.showText(valor != null ? valor : "—");
        cs.endText();

        return y - 17;
    }

    // ================== PARECER FINAL ==================

    private Cursor escreverParecer(PDDocument document, Cursor cursor, PDType1Font fonteLabel,
                                   PDType1Font fonteResultado, Vistoria vistoria) throws IOException {

        float altura = 40;
        cursor = garantirEspaco(document, cursor, altura + 15);

        float[] cor = switch (vistoria.getResultado()) {
            case APROVADO -> COR_SUCESSO;
            case REPROVADO -> COR_PERIGO;
            case APROVADO_COM_RESSALVAS -> COR_ALERTA;
        };

        float baseY = cursor.y - altura;

        setCor(cursor.cs, cor, true);
        cursor.cs.addRect(MARGEM, baseY, LARGURA_UTIL, altura);
        cursor.cs.fill();

        cursor.cs.beginText();
        cursor.cs.setFont(fonteLabel, 10);
        setCor(cursor.cs, new float[]{1, 1, 1}, true);
        cursor.cs.newLineAtOffset(MARGEM + 15, baseY + 24);
        cursor.cs.showText("PARECER FINAL");
        cursor.cs.endText();

        cursor.cs.beginText();
        cursor.cs.setFont(fonteResultado, 14);
        setCor(cursor.cs, new float[]{1, 1, 1}, true);
        cursor.cs.newLineAtOffset(MARGEM + 15, baseY + 8);
        cursor.cs.showText(traduzirResultado(vistoria.getResultado().name()));
        cursor.cs.endText();

        cursor.y = baseY - 20;
        return cursor;
    }

    // ================== SEÇÕES E CHECKLIST EM TABELA ==================

    private Cursor escreverSecao(PDDocument document, Cursor cursor, PDType1Font fonte, String texto) throws IOException {
        cursor = garantirEspaco(document, cursor, 26);
        cursor.cs.beginText();
        cursor.cs.setFont(fonte, 12);
        setCor(cursor.cs, COR_PRIMARIA, true);
        cursor.cs.newLineAtOffset(MARGEM, cursor.y);
        cursor.cs.showText(texto);
        cursor.cs.endText();
        cursor.y -= 20;
        return cursor;
    }

    private Cursor escreverChecklistTabela(PDDocument document, Cursor cursor, PDType1Font fonteHeader,
                                           PDType1Font fonteTexto, Vistoria vistoria) throws IOException {

        float larguraItem = LARGURA_UTIL * 0.28f;
        float larguraSituacao = LARGURA_UTIL * 0.15f;
        float alturaLinha = 20;

        cursor = garantirEspaco(document, cursor, alturaLinha + 5);

        float y = cursor.y;
        setCor(cursor.cs, COR_PRIMARIA, true);
        cursor.cs.addRect(MARGEM, y - alturaLinha, LARGURA_UTIL, alturaLinha);
        cursor.cs.fill();

        escreverCelula(cursor.cs, fonteHeader, "Item", MARGEM + 8, y - 14, new float[]{1, 1, 1});
        escreverCelula(cursor.cs, fonteHeader, "Situação", MARGEM + larguraItem + 8, y - 14, new float[]{1, 1, 1});
        escreverCelula(cursor.cs, fonteHeader, "Observação", MARGEM + larguraItem + larguraSituacao + 8, y - 14, new float[]{1, 1, 1});

        cursor.y -= alturaLinha;

        boolean linhaClara = true;
        for (ItemVistoria item : vistoria.getItens()) {
            cursor = garantirEspaco(document, cursor, alturaLinha);
            y = cursor.y;

            if (linhaClara) {
                setCor(cursor.cs, COR_FUNDO_CLARO, true);
                cursor.cs.addRect(MARGEM, y - alturaLinha, LARGURA_UTIL, alturaLinha);
                cursor.cs.fill();
            }
            linhaClara = !linhaClara;

            escreverCelula(cursor.cs, fonteTexto, item.getNomeItem(), MARGEM + 8, y - 14, COR_PRIMARIA);

            float[] corSituacao = item.getSituacao().name().equals("OK") ? COR_SUCESSO : COR_PERIGO;
            escreverCelula(cursor.cs, fonteTexto, traduzirSituacao(item.getSituacao().name()),
                    MARGEM + larguraItem + 8, y - 14, corSituacao);

            String obs = item.getObservacao() != null && !item.getObservacao().isBlank() ? item.getObservacao() : "—";
            String obsCortada = obs.length() > 55 ? obs.substring(0, 52) + "..." : obs;
            escreverCelula(cursor.cs, fonteTexto, obsCortada, MARGEM + larguraItem + larguraSituacao + 8, y - 14, COR_PRIMARIA);

            cursor.y -= alturaLinha;
        }

        cursor.y -= 15;
        return cursor;
    }

    private void escreverCelula(PDPageContentStream cs, PDType1Font fonte, String texto, float x, float y, float[] cor) throws IOException {
        cs.beginText();
        cs.setFont(fonte, 9);
        setCor(cs, cor, true);
        cs.newLineAtOffset(x, y);
        cs.showText(texto);
        cs.endText();
    }

    // ================== PARÁGRAFO, FOTOS, RODAPÉ ==================

    private Cursor escreverParagrafo(PDDocument document, Cursor cursor, PDType1Font fonte, String texto) throws IOException {
        if (texto == null || texto.isBlank()) return cursor;

        int larguraMaximaChars = 100;
        String[] palavras = texto.split(" ");
        StringBuilder linha = new StringBuilder();

        for (String palavra : palavras) {
            if (linha.length() + palavra.length() + 1 > larguraMaximaChars) {
                cursor = escreverLinhaTexto(document, cursor, fonte, linha.toString());
                linha = new StringBuilder();
            }
            linha.append(linha.isEmpty() ? "" : " ").append(palavra);
        }
        if (!linha.isEmpty()) {
            cursor = escreverLinhaTexto(document, cursor, fonte, linha.toString());
        }
        cursor.y -= 10;
        return cursor;
    }

    private Cursor escreverLinhaTexto(PDDocument document, Cursor cursor, PDType1Font fonte, String texto) throws IOException {
        cursor = garantirEspaco(document, cursor, 16);
        cursor.cs.beginText();
        cursor.cs.setFont(fonte, 10);
        setCor(cursor.cs, COR_PRIMARIA, true);
        cursor.cs.newLineAtOffset(MARGEM, cursor.y);
        cursor.cs.showText(texto);
        cursor.cs.endText();
        cursor.y -= 16;
        return cursor;
    }

    private Cursor escreverFoto(PDDocument document, Cursor cursor, PDType1Font fonte, FotoVistoria foto) throws IOException {
        Path caminhoFoto = Path.of(diretorioFotos, foto.getCaminhoArquivo());

        if (!Files.exists(caminhoFoto)) {
            return escreverLinhaTexto(document, cursor, fonte, "[Foto não encontrada: " + foto.getCaminhoArquivo() + "]");
        }

        PDImageXObject imagem = PDImageXObject.createFromFile(caminhoFoto.toString(), document);

        float escala = LARGURA_UTIL / imagem.getWidth();
        float alturaDesenhada = imagem.getHeight() * escala;

        if (alturaDesenhada > 280) {
            escala = 280f / imagem.getHeight();
            alturaDesenhada = 280;
        }
        float larguraDesenhada = imagem.getWidth() * escala;

        cursor = garantirEspaco(document, cursor, alturaDesenhada + 30);

        float baseY = cursor.y - alturaDesenhada;

        setCor(cursor.cs, COR_BORDA, false);
        cursor.cs.setLineWidth(1f);
        cursor.cs.addRect(MARGEM - 2, baseY - 2, larguraDesenhada + 4, alturaDesenhada + 4);
        cursor.cs.stroke();

        cursor.cs.drawImage(imagem, MARGEM, baseY, larguraDesenhada, alturaDesenhada);
        cursor.y = baseY - 10;

        if (foto.getDescricao() != null && !foto.getDescricao().isBlank()) {
            cursor = escreverLinhaTexto(document, cursor, fonte, foto.getDescricao());
        }
        cursor.y -= 10;

        return cursor;
    }

    // ================== ESTRUTURA DE PÁGINA ==================

    private Cursor abrirPagina(PDDocument document, int numero) throws IOException {
        PDPage pagina = new PDPage(PDRectangle.A4);
        document.addPage(pagina);

        Cursor cursor = new Cursor();
        cursor.cs = new PDPageContentStream(document, pagina);
        cursor.y = ALTURA_A4 - MARGEM;
        cursor.numeroPagina = numero;

        escreverRodape(cursor, numero);

        return cursor;
    }

    private void escreverRodape(Cursor cursor, int numero) throws IOException {
        var fonteRodape = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);
        cursor.cs.beginText();
        cursor.cs.setFont(fonteRodape, 7);
        setCor(cursor.cs, COR_TEXTO_SECUNDARIO, true);
        cursor.cs.newLineAtOffset(MARGEM, 20);
        cursor.cs.showText("Documento gerado eletronicamente pelo sistema AutoVistor — Página " + numero);
        cursor.cs.endText();
    }

    private Cursor garantirEspaco(PDDocument document, Cursor cursor, float alturaNecessaria) throws IOException {
        if (cursor.y - alturaNecessaria < MARGEM + 15) {
            cursor.cs.close();
            return abrirPagina(document, cursor.numeroPagina + 1);
        }
        return cursor;
    }

    private void setCor(PDPageContentStream cs, float[] cor, boolean preenchimento) throws IOException {
        if (preenchimento) {
            cs.setNonStrokingColor(cor[0], cor[1], cor[2]);
        } else {
            cs.setStrokingColor(cor[0], cor[1], cor[2]);
        }
    }

    private String traduzirResultado(String resultado) {
        return switch (resultado) {
            case "APROVADO" -> "APROVADO";
            case "REPROVADO" -> "REPROVADO";
            case "APROVADO_COM_RESSALVAS" -> "APROVADO COM RESSALVAS";
            default -> resultado;
        };
    }

    private String traduzirSituacao(String situacao) {
        return "OK".equals(situacao) ? "OK" : "Avaria";
    }

    private LaudoResponse toResponse(Laudo l) {
        return new LaudoResponse(l.getId(), l.getCaminhoArquivo(), l.getDataGeracao(), l.getVistoria().getId());
    }
}