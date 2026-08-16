package br.com.william.autovistordesktop.view.gerente;

import br.com.william.autovistordesktop.client.ApiClient;
import br.com.william.autovistordesktop.client.ApiException;
import br.com.william.autovistordesktop.model.BoletoDetalhadoResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class FinanceiroController {

    @FXML private TableView<BoletoDetalhadoResponse> tabelaBoletos;
    @FXML private TableColumn<BoletoDetalhadoResponse, Long> colunaIdBoleto;
    @FXML private TableColumn<BoletoDetalhadoResponse, String> colunaCodigoBarras;
    @FXML private TableColumn<BoletoDetalhadoResponse, String> colunaVencimento;
    @FXML private TableColumn<BoletoDetalhadoResponse, String> colunaValor;
    @FXML private Button botaoAtualizar;
    @FXML private Button botaoConfirmar;

    private final ApiClient apiClient = new ApiClient();
    private final ObservableList<BoletoDetalhadoResponse> dados = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colunaIdBoleto.setCellValueFactory(new PropertyValueFactory<>("idBoleto"));
        colunaCodigoBarras.setCellValueFactory(new PropertyValueFactory<>("codigoBarras"));
        colunaVencimento.setCellValueFactory(new PropertyValueFactory<>("dataVencimento"));
        colunaValor.setCellValueFactory(new PropertyValueFactory<>("valor"));

        tabelaBoletos.setItems(dados);

        tabelaBoletos.getSelectionModel().selectedItemProperty().addListener(
                (obs, antigo, novo) -> botaoConfirmar.setDisable(novo == null));

        botaoAtualizar.setOnAction(e -> carregarBoletos());
        botaoConfirmar.setOnAction(e -> confirmarPagamento());

        carregarBoletos();
    }

    private void carregarBoletos() {
        try {
            BoletoDetalhadoResponse[] resposta = apiClient.get(
                    "/pagamentos/boletos-pendentes", BoletoDetalhadoResponse[].class);
            dados.setAll(resposta);
        } catch (ApiException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    private void confirmarPagamento() {
        BoletoDetalhadoResponse selecionado = tabelaBoletos.getSelectionModel().getSelectedItem();
        if (selecionado == null) return;

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                "Confirmar recebimento do boleto no valor de R$ " + selecionado.getValor() + "?");
        confirmacao.showAndWait().filter(botao -> botao == ButtonType.OK).ifPresent(botao -> {
            try {
                apiClient.post("/pagamentos/" + selecionado.getIdPagamento() + "/confirmar-boleto",
                        null, Void.class, true);
                new Alert(Alert.AlertType.INFORMATION, "Pagamento confirmado com sucesso!").showAndWait();
                carregarBoletos();
            } catch (ApiException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            }
        });
    }
}