package br.com.william.autovistordesktop.view;

import br.com.william.autovistordesktop.client.NavigationService;
import br.com.william.autovistordesktop.session.SessaoUsuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public abstract class DashboardBaseController {

    @FXML protected StackPane areaConteudo;
    @FXML protected Button botaoSair;

    protected void configurarLogout() {
        botaoSair.setOnAction(e -> fazerLogout());
    }

    protected void carregarTela(String caminhoFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoFxml));
            Parent tela = loader.load();
            areaConteudo.getChildren().setAll(tela);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar tela: " + caminhoFxml, e);
        }
    }

    protected void marcarBotaoAtivo(Button botaoAtivo, Button... todosOsBotoes) {
        for (Button botao : todosOsBotoes) {
            botao.getStyleClass().remove("sidebar-botao-ativo");
        }
        botaoAtivo.getStyleClass().add("sidebar-botao-ativo");
    }

    private void fazerLogout() {
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION, "Deseja realmente sair?");
        confirmacao.showAndWait().filter(botao -> botao == ButtonType.OK).ifPresent(botao -> {
            SessaoUsuario.getInstancia().encerrar();
            NavigationService.getInstancia().navegarPara("/fxml/login/login.fxml", "AutoVistor — Login");
        });
    }
}