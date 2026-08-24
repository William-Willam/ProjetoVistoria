package br.com.william.autovistordesktop.view.vistoriador;

import br.com.william.autovistordesktop.view.DashboardBaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class DashboardVistoriadorController extends DashboardBaseController {

    @FXML private Button botaoVistoriasDesignadas;

    @FXML
    private void initialize() {
        configurarLogout();

        botaoVistoriasDesignadas.setOnAction(e -> selecionar());

        selecionar();
    }

    private void selecionar() {
        marcarBotaoAtivo(botaoVistoriasDesignadas, botaoVistoriasDesignadas);
        carregarTela("/fxml/vistoriador/vistorias_designadas.fxml");
    }
}