package br.com.william.autovistordesktop.view.gerente;

import br.com.william.autovistordesktop.client.ApiClient;
import br.com.william.autovistordesktop.client.ApiException;
import br.com.william.autovistordesktop.model.AgendamentoResponse;
import br.com.william.autovistordesktop.model.DesignarVistoriadorRequest;
import br.com.william.autovistordesktop.model.FuncionarioResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Arrays;
import java.util.Optional;

public class DesignarVistoriadorController {

    @FXML private TableView<AgendamentoResponse> tabelaAgendamentos;
    @FXML private TableColumn<AgendamentoResponse, Long> colunaId;
    @FXML private TableColumn<AgendamentoResponse, String> colunaData;
    @FXML private TableColumn<AgendamentoResponse, String> colunaHora;
    @FXML private TableColumn<AgendamentoResponse, String> colunaTipo;
    @FXML private TableColumn<AgendamentoResponse, Long> colunaCliente;
    @FXML private TableColumn<AgendamentoResponse, Long> colunaVeiculo;
    @FXML private Button botaoAtualizar;
    @FXML private Button botaoDesignar;

    private final ApiClient apiClient = new ApiClient();
    private final ObservableList<AgendamentoResponse> dados = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaData.setCellValueFactory(new PropertyValueFactory<>("dataAgendamento"));
        colunaHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colunaTipo.setCellValueFactory(new PropertyValueFactory<>("tipoVistoria"));
        colunaCliente.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        colunaVeiculo.setCellValueFactory(new PropertyValueFactory<>("idVeiculo"));

        tabelaAgendamentos.setItems(dados);

        tabelaAgendamentos.getSelectionModel().selectedItemProperty().addListener(
                (obs, antigo, novo) -> botaoDesignar.setDisable(novo == null));

        botaoAtualizar.setOnAction(e -> carregarPendentes());
        botaoDesignar.setOnAction(e -> abrirSelecaoVistoriador());

        carregarPendentes();
    }

    private void carregarPendentes() {
        try {
            AgendamentoResponse[] resposta = apiClient.get("/agendamentos/pendentes", AgendamentoResponse[].class);
            dados.setAll(resposta);
        } catch (ApiException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    private void abrirSelecaoVistoriador() {
        AgendamentoResponse selecionado = tabelaAgendamentos.getSelectionModel().getSelectedItem();
        if (selecionado == null) return;

        try {
            FuncionarioResponse[] todos = apiClient.get("/funcionarios", FuncionarioResponse[].class);
            ObservableList<FuncionarioResponse> vistoriadores = FXCollections.observableArrayList(
                    Arrays.stream(todos).filter(f -> "VISTORIADOR".equals(f.getCargo())).toList());

            if (vistoriadores.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Nenhum vistoriador cadastrado.").showAndWait();
                return;
            }

            ChoiceDialog<FuncionarioResponse> dialog = new ChoiceDialog<>(vistoriadores.get(0), vistoriadores);
            dialog.setTitle("Designar Vistoriador");
            dialog.setHeaderText("Agendamento #" + selecionado.getId() + " — " +
                    selecionado.getDataAgendamento() + " às " + selecionado.getHora());
            dialog.setContentText("Vistoriador:");

            // exibe nome + matrícula em vez do toString() padrão do objeto
            dialog.getItems().setAll(vistoriadores);
            dialog.setSelectedItem(vistoriadores.get(0));

            Optional<FuncionarioResponse> resultado = dialog.showAndWait();
            resultado.ifPresent(func -> designarVistoriador(selecionado.getId(), func.getId()));

        } catch (ApiException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    private void designarVistoriador(Long idAgendamento, Long idFuncionario) {
        try {
            apiClient.post("/agendamentos/" + idAgendamento + "/designar-vistoriador",
                    new DesignarVistoriadorRequest(idFuncionario), AgendamentoResponse.class, true);
            new Alert(Alert.AlertType.INFORMATION, "Vistoriador designado com sucesso!").showAndWait();
            carregarPendentes();
        } catch (ApiException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }
}