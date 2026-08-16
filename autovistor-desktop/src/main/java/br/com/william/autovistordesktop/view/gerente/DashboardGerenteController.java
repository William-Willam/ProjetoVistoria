package br.com.william.autovistordesktop.view.gerente;

import br.com.william.autovistordesktop.view.DashboardBaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class DashboardGerenteController extends DashboardBaseController {

    @FXML private Button botaoFuncionarios;
    @FXML private Button botaoDesignarVistoriador;
    @FXML private Button botaoFinanceiro;
    @FXML private Button botaoRelatorios;
    @FXML private Button botaoClientesVeiculos;

    @FXML
    private void initialize() {
        configurarLogout();

        botaoFuncionarios.setOnAction(e -> carregarTela("/fxml/gerente/gestao_funcionarios.fxml"));
        botaoDesignarVistoriador.setOnAction(e -> carregarTela("/fxml/gerente/designar_vistoriador.fxml"));
        botaoFinanceiro.setOnAction(e -> carregarTela("/fxml/gerente/financeiro.fxml"));
        botaoRelatorios.setOnAction(e -> carregarTela("/fxml/gerente/relatorios.fxml"));
        botaoClientesVeiculos.setOnAction(e -> carregarTela("/fxml/gerente/clientes_veiculos.fxml"));

        carregarTela("/fxml/gerente/gestao_funcionarios.fxml");
    }
}