package br.com.william.autovistordesktop.view.vistoriador;

import br.com.william.autovistordesktop.client.ApiClient;
import br.com.william.autovistordesktop.client.ApiException;
import br.com.william.autovistordesktop.model.AgendamentoResponse;
import br.com.william.autovistordesktop.model.VistoriaResponse;
import br.com.william.autovistordesktop.session.SessaoUsuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;

public class VistoriasDesignadasController {

    @FXML private TableView<AgendamentoResponse> tabelaAgendamentos;
    @FXML private TableColumn<AgendamentoResponse, Long> colunaId;
    @FXML private TableColumn<AgendamentoResponse, String> colunaData;
    @FXML private TableColumn<AgendamentoResponse, String> colunaHora;
    @FXML private TableColumn<AgendamentoResponse, String> colunaTipo;
    @FXML private TableColumn<AgendamentoResponse, String> colunaStatus;
    @FXML private Button botaoAtualizar;
    @FXML private Button botaoRegistrarVistoria;
    @FXML private Button botaoBaixarLaudo;

    private final ApiClient apiClient = new ApiClient();
    private final ObservableList<AgendamentoResponse> dados = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaData.setCellValueFactory(new PropertyValueFactory<>("dataAgendamento"));
        colunaHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colunaTipo.setCellValueFactory(new PropertyValueFactory<>("tipoVistoria"));
        colunaStatus.setCellValueFactory(new PropertyValueFactory<>("statusAgendamento"));

        tabelaAgendamentos.setItems(dados);

        tabelaAgendamentos.getSelectionModel().selectedItemProperty().addListener(
                (obs, antigo, novo) -> {
                    boolean nenhumSelecionado = novo == null;
                    botaoRegistrarVistoria.setDisable(
                            nenhumSelecionado || !"CONFIRMADO".equals(novo.getStatusAgendamento()));
                    botaoBaixarLaudo.setDisable(
                            nenhumSelecionado || !"CONCLUIDO".equals(novo.getStatusAgendamento()));
                });

        botaoAtualizar.setOnAction(e -> carregarAgendamentos());
        botaoRegistrarVistoria.setOnAction(e -> abrirFormularioVistoria());
        botaoBaixarLaudo.setOnAction(e -> baixarLaudo());

        carregarAgendamentos();
    }

    private void carregarAgendamentos() {
        try {
            Long idFuncionario = SessaoUsuario.getInstancia().getId();
            AgendamentoResponse[] resposta = apiClient.get(
                    "/agendamentos/vistoriador/" + idFuncionario, AgendamentoResponse[].class);
            dados.setAll(resposta);
        } catch (ApiException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    private void abrirFormularioVistoria() {
        AgendamentoResponse selecionado = tabelaAgendamentos.getSelectionModel().getSelectedItem();
        if (selecionado == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/vistoriador/registrar_vistoria.fxml"));
            Parent root = loader.load();

            RegistrarVistoriaController controller = loader.getController();
            controller.inicializar(selecionado);

            Stage stage = new Stage();
            stage.setTitle("Registrar Vistoria");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            if (controller.isSalvouComSucesso()) {
                carregarAgendamentos();
            }

        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao abrir tela de vistoria: " + e.getMessage()).showAndWait();
        }
    }

    private void baixarLaudo() {
        AgendamentoResponse selecionado = tabelaAgendamentos.getSelectionModel().getSelectedItem();
        if (selecionado == null) return;

        try {
            VistoriaResponse vistoria = apiClient.get(
                    "/agendamentos/" + selecionado.getId() + "/vistoria", VistoriaResponse.class);

            byte[] pdf = apiClient.getArquivo("/vistorias/" + vistoria.getId() + "/laudo/download");

            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName("laudo-vistoria-" + vistoria.getId() + ".pdf");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Arquivo PDF", "*.pdf"));

            var arquivo = fileChooser.showSaveDialog(botaoBaixarLaudo.getScene().getWindow());
            if (arquivo != null) {
                Files.write(arquivo.toPath(), pdf);
                new Alert(Alert.AlertType.INFORMATION, "Laudo salvo com sucesso!").showAndWait();
            }

        } catch (ApiException | IOException e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao baixar laudo: " + e.getMessage()).showAndWait();
        }
    }
}