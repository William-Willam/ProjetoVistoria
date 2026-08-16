package br.com.william.autovistordesktop.view.gerente;

import br.com.william.autovistordesktop.client.ApiClient;
import br.com.william.autovistordesktop.client.ApiException;
import br.com.william.autovistordesktop.model.RelatorioFinanceiroResponse;
import br.com.william.autovistordesktop.model.RelatorioOperacionalResponse;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;

import java.time.LocalDate;
import java.util.Map;

public class RelatoriosController {

    @FXML private DatePicker campoDataInicio;
    @FXML private DatePicker campoDataFim;
    @FXML private Button botaoOperacional;
    @FXML private Button botaoFinanceiro;
    @FXML private TextArea areaResultado;

    private final ApiClient apiClient = new ApiClient();

    @FXML
    private void initialize() {
        campoDataInicio.setValue(LocalDate.now().withDayOfMonth(1));
        campoDataFim.setValue(LocalDate.now());

        botaoOperacional.setOnAction(e -> gerarOperacional());
        botaoFinanceiro.setOnAction(e -> gerarFinanceiro());
    }

    private boolean datasValidas() {
        LocalDate inicio = campoDataInicio.getValue();
        LocalDate fim = campoDataFim.getValue();

        if (inicio == null || fim == null) {
            new Alert(Alert.AlertType.WARNING, "Selecione as duas datas.").showAndWait();
            return false;
        }
        if (inicio.isAfter(fim)) {
            new Alert(Alert.AlertType.WARNING, "A data inicial não pode ser depois da data final.").showAndWait();
            return false;
        }
        return true;
    }

    private void gerarOperacional() {
        if (!datasValidas()) return;

        try {
            String path = "/relatorios/operacional?inicio=" + campoDataInicio.getValue()
                    + "&fim=" + campoDataFim.getValue();
            RelatorioOperacionalResponse resposta = apiClient.get(path, RelatorioOperacionalResponse.class);

            StringBuilder sb = new StringBuilder();
            sb.append("RELATÓRIO OPERACIONAL\n");
            sb.append("Período: ").append(resposta.getInicio()).append(" a ").append(resposta.getFim()).append("\n\n");
            sb.append("Agendamentos por status:\n");
            for (Map.Entry<String, Long> entry : resposta.getAgendamentosPorStatus().entrySet()) {
                sb.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }

            areaResultado.setText(sb.toString());

        } catch (ApiException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    private void gerarFinanceiro() {
        if (!datasValidas()) return;

        try {
            String path = "/relatorios/financeiro?inicio=" + campoDataInicio.getValue()
                    + "&fim=" + campoDataFim.getValue();
            RelatorioFinanceiroResponse resposta = apiClient.get(path, RelatorioFinanceiroResponse.class);

            StringBuilder sb = new StringBuilder();
            sb.append("RELATÓRIO FINANCEIRO\n");
            sb.append("Período: ").append(resposta.getInicio()).append(" a ").append(resposta.getFim()).append("\n\n");
            sb.append("Total recebido: R$ ").append(resposta.getTotalRecebido()).append("\n\n");
            sb.append("Por forma de pagamento:\n");
            for (Map.Entry<String, java.math.BigDecimal> entry : resposta.getTotalPorFormaPagamento().entrySet()) {
                sb.append("  ").append(entry.getKey()).append(": R$ ").append(entry.getValue()).append("\n");
            }

            areaResultado.setText(sb.toString());

        } catch (ApiException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }
}