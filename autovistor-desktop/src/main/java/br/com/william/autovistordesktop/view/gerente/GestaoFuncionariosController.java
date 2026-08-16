package br.com.william.autovistordesktop.view.gerente;

import br.com.william.autovistordesktop.client.ApiClient;
import br.com.william.autovistordesktop.client.ApiException;
import br.com.william.autovistordesktop.model.DesligamentoRequest;
import br.com.william.autovistordesktop.model.FuncionarioAtualizacaoRequest;
import br.com.william.autovistordesktop.model.FuncionarioCadastroRequest;
import br.com.william.autovistordesktop.model.FuncionarioResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public class GestaoFuncionariosController {

    @FXML private TableView<FuncionarioResponse> tabelaFuncionarios;
    @FXML private TableColumn<FuncionarioResponse, Long> colunaId;
    @FXML private TableColumn<FuncionarioResponse, String> colunaNome;
    @FXML private TableColumn<FuncionarioResponse, String> colunaEmail;
    @FXML private TableColumn<FuncionarioResponse, String> colunaMatricula;
    @FXML private TableColumn<FuncionarioResponse, String> colunaCargo;
    @FXML private Button botaoAtualizar;
    @FXML private Button botaoNovoFuncionario;
    @FXML private Button botaoEditar;
    @FXML private Button botaoDesligar;

    private final ApiClient apiClient = new ApiClient();
    private final ObservableList<FuncionarioResponse> dados = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colunaMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));
        colunaCargo.setCellValueFactory(new PropertyValueFactory<>("cargo"));

        tabelaFuncionarios.setItems(dados);

        tabelaFuncionarios.getSelectionModel().selectedItemProperty().addListener(
                (obs, antigo, novo) -> {
                    botaoDesligar.setDisable(novo == null);
                    botaoEditar.setDisable(novo == null);
                });

        botaoAtualizar.setOnAction(e -> carregarFuncionarios());
        botaoNovoFuncionario.setOnAction(e -> abrirFormularioCadastro());
        botaoEditar.setOnAction(e -> abrirFormularioEdicao());
        botaoDesligar.setOnAction(e -> abrirFormularioDesligamento());

        carregarFuncionarios();
    }

    private void carregarFuncionarios() {
        try {
            FuncionarioResponse[] resposta = apiClient.get("/funcionarios", FuncionarioResponse[].class);
            dados.setAll(resposta);
        } catch (ApiException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    private void abrirFormularioCadastro() {
        Dialog<FuncionarioCadastroRequest> dialog = new Dialog<>();
        dialog.setTitle("Novo Funcionário");

        ButtonType botaoSalvar = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(botaoSalvar, ButtonType.CANCEL);

        TextField campoNome = new TextField();
        TextField campoEmail = new TextField();
        TextField campoMatricula = new TextField();
        PasswordField campoSenha = new PasswordField();
        ComboBox<String> comboCargo = new ComboBox<>(
                FXCollections.observableArrayList("VISTORIADOR", "GERENTE"));
        comboCargo.setValue("VISTORIADOR");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.addRow(0, new Label("Nome:"), campoNome);
        grid.addRow(1, new Label("E-mail:"), campoEmail);
        grid.addRow(2, new Label("Matrícula:"), campoMatricula);
        grid.addRow(3, new Label("Senha:"), campoSenha);
        grid.addRow(4, new Label("Cargo:"), comboCargo);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(botaoClicado -> {
            if (botaoClicado == botaoSalvar) {
                return new FuncionarioCadastroRequest(
                        campoNome.getText(), campoEmail.getText(), campoMatricula.getText(),
                        campoSenha.getText(), comboCargo.getValue());
            }
            return null;
        });

        Optional<FuncionarioCadastroRequest> resultado = dialog.showAndWait();
        resultado.ifPresent(this::cadastrarFuncionario);
    }

    private void cadastrarFuncionario(FuncionarioCadastroRequest request) {
        if (request.getNome().isBlank() || request.getEmail().isBlank()
                || request.getMatricula().isBlank() || request.getSenha().isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Todos os campos são obrigatórios.").showAndWait();
            return;
        }

        try {
            apiClient.post("/funcionarios", request, FuncionarioResponse.class, true);
            new Alert(Alert.AlertType.INFORMATION, "Funcionário cadastrado com sucesso!").showAndWait();
            carregarFuncionarios();
        } catch (ApiException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    private void abrirFormularioEdicao() {
        FuncionarioResponse selecionado = tabelaFuncionarios.getSelectionModel().getSelectedItem();
        if (selecionado == null) return;

        Dialog<FuncionarioAtualizacaoRequest> dialog = new Dialog<>();
        dialog.setTitle("Editar Funcionário");

        ButtonType botaoSalvar = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(botaoSalvar, ButtonType.CANCEL);

        TextField campoNome = new TextField(selecionado.getNome());
        TextField campoEmail = new TextField(selecionado.getEmail());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.addRow(0, new Label("Nome:"), campoNome);
        grid.addRow(1, new Label("E-mail:"), campoEmail);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(botaoClicado -> {
            if (botaoClicado == botaoSalvar) {
                return new FuncionarioAtualizacaoRequest(campoNome.getText(), campoEmail.getText());
            }
            return null;
        });

        Optional<FuncionarioAtualizacaoRequest> resultado = dialog.showAndWait();
        resultado.ifPresent(request -> atualizarFuncionario(selecionado.getId(), request));
    }

    private void atualizarFuncionario(Long id, FuncionarioAtualizacaoRequest request) {
        if (request.getNome().isBlank() || request.getEmail().isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Nome e e-mail são obrigatórios.").showAndWait();
            return;
        }

        try {
            apiClient.put("/funcionarios/" + id, request, FuncionarioResponse.class);
            new Alert(Alert.AlertType.INFORMATION, "Funcionário atualizado com sucesso!").showAndWait();
            carregarFuncionarios();
        } catch (ApiException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    private void abrirFormularioDesligamento() {
        FuncionarioResponse selecionado = tabelaFuncionarios.getSelectionModel().getSelectedItem();
        if (selecionado == null) return;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Desligar Funcionário");
        dialog.setHeaderText("Desligar " + selecionado.getNome() + " (matrícula " + selecionado.getMatricula() + ")");
        dialog.setContentText("Motivo do desligamento:");

        Optional<String> motivo = dialog.showAndWait();
        motivo.ifPresent(m -> {
            if (m.isBlank()) {
                new Alert(Alert.AlertType.WARNING, "O motivo é obrigatório.").showAndWait();
                return;
            }
            desligarFuncionario(selecionado.getId(), m);
        });
    }

    private void desligarFuncionario(Long id, String motivo) {
        try {
            apiClient.post("/funcionarios/" + id + "/desligamento",
                    new DesligamentoRequest(motivo), Void.class, true);
            new Alert(Alert.AlertType.INFORMATION, "Funcionário desligado com sucesso!").showAndWait();
            carregarFuncionarios();
        } catch (ApiException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }
}