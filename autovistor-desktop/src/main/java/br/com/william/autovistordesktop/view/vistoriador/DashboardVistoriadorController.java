package br.com.william.autovistordesktop.view.vistoriador;

import br.com.william.autovistordesktop.view.DashboardBaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class DashboardVistoriadorController extends DashboardBaseController {

    @FXML private Button botaoVistoriasDesignadas;

    @FXML
    private void initialize() {
        configurarLogout();

        botaoVistoriasDesignadas.setOnAction(e -> carregarTela("/fxml/vistoriador/vistorias_designadas.fxml"));

        carregarTela("/fxml/vistoriador/vistorias_designadas.fxml");
    }
}