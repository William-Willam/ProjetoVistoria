package br.com.william.autovistordesktop.view.gerente;

import br.com.william.autovistordesktop.client.ApiClient;
import br.com.william.autovistordesktop.client.ApiException;
import br.com.william.autovistordesktop.model.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Arrays;

public class ClientesVeiculosController {

    @FXML private TextField campoNomeCliente;
    @FXML private TextField campoCpfCliente;
    @FXML private TextField campoTelefoneCliente;
    @FXML private TextField campoEmailCliente;
    @FXML private PasswordField campoSenhaCliente;
    @FXML private Button botaoSalvarCliente;

    @FXML private ComboBox<ClienteResponse> comboClienteVeiculo;
    @FXML private Button botaoAtualizarClientes;
    @FXML private TextField campoPlaca;
    @FXML private TextField campoTipoVeiculo;
    @FXML private TextField campoNomeVeiculo;
    @FXML private TextField campoModelo;
    @FXML private TextField campoAno;
    @FXML private TextField campoChassi;
    @FXML private TextArea campoObservacoesVeiculo;
    @FXML private Button botaoSalvarVeiculo;

    private final ApiClient apiClient = new ApiClient();

    @FXML
    private void initialize() {
        botaoSalvarCliente.setOnAction(e -> cadastrarCliente());
        botaoAtualizarClientes.setOnAction(e -> carregarClientes());
        botaoSalvarVeiculo.setOnAction(e -> cadastrarVeiculo());

        carregarClientes();
    }

    private void carregarClientes() {
        try {
            ClienteResponse[] clientes = apiClient.get("/clientes/todos", ClienteResponse[].class);
            comboClienteVeiculo.setItems(FXCollections.observableArrayList(Arrays.asList(clientes)));
        } catch (ApiException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    private void cadastrarCliente() {
        String nome = campoNomeCliente.getText();
        String cpf = campoCpfCliente.getText();
        String telefone = campoTelefoneCliente.getText();
        String email = campoEmailCliente.getText();
        String senha = campoSenhaCliente.getText();

        if (nome.isBlank() || cpf.isBlank() || telefone.isBlank() || email.isBlank() || senha.isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Todos os campos do cliente são obrigatórios.").showAndWait();
            return;
        }

        try {
            apiClient.post("/clientes", new ClienteCadastroRequest(nome, cpf, telefone, email, senha),
                    ClienteResponse.class, false);
            new Alert(Alert.AlertType.INFORMATION, "Cliente cadastrado com sucesso!").showAndWait();
            limparFormularioCliente();
            carregarClientes();
        } catch (ApiException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    private void cadastrarVeiculo() {
        ClienteResponse clienteSelecionado = comboClienteVeiculo.getValue();
        if (clienteSelecionado == null) {
            new Alert(Alert.AlertType.WARNING, "Selecione o cliente dono do veículo.").showAndWait();
            return;
        }

        Integer ano;
        try {
            ano = Integer.parseInt(campoAno.getText());
        } catch (NumberFormatException ex) {
            new Alert(Alert.AlertType.WARNING, "Ano inválido.").showAndWait();
            return;
        }

        String placa = campoPlaca.getText();
        String tipo = campoTipoVeiculo.getText();
        String nomeVeiculo = campoNomeVeiculo.getText();
        String modelo = campoModelo.getText();
        String chassi = campoChassi.getText();

        if (placa.isBlank() || tipo.isBlank() || nomeVeiculo.isBlank() || modelo.isBlank() || chassi.isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Todos os campos do veículo (exceto observações) são obrigatórios.").showAndWait();
            return;
        }

        try {
            VeiculoCadastroRequest request = new VeiculoCadastroRequest(
                    placa, tipo, nomeVeiculo, modelo, ano, chassi,
                    campoObservacoesVeiculo.getText(), clienteSelecionado.getId());

            apiClient.post("/veiculos", request, Object.class, true);
            new Alert(Alert.AlertType.INFORMATION, "Veículo cadastrado com sucesso!").showAndWait();
            limparFormularioVeiculo();
        } catch (ApiException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    private void limparFormularioCliente() {
        campoNomeCliente.clear();
        campoCpfCliente.clear();
        campoTelefoneCliente.clear();
        campoEmailCliente.clear();
        campoSenhaCliente.clear();
    }

    private void limparFormularioVeiculo() {
        campoPlaca.clear();
        campoTipoVeiculo.clear();
        campoNomeVeiculo.clear();
        campoModelo.clear();
        campoAno.clear();
        campoChassi.clear();
        campoObservacoesVeiculo.clear();
    }
}