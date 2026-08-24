package br.com.william.autovistordesktop.view.gerente;

import br.com.william.autovistordesktop.view.DashboardBaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class DashboardGerenteController extends DashboardBaseController {

    @FXML
    private Button botaoFuncionarios;
    @FXML
    private Button botaoDesignarVistoriador;
    @FXML
    private Button botaoFinanceiro;
    @FXML
    private Button botaoRelatorios;
    @FXML
    private Button botaoClientesVeiculos;

    @FXML
    private void initialize() {
        configurarLogout();

        botaoFuncionarios.setOnAction(e -> selecionar(botaoFuncionarios, "/fxml/gerente/gestao_funcionarios.fxml"));
        botaoDesignarVistoriador.setOnAction(e -> selecionar(botaoDesignarVistoriador, "/fxml/gerente/designar_vistoriador.fxml"));
        botaoFinanceiro.setOnAction(e -> selecionar(botaoFinanceiro, "/fxml/gerente/financeiro.fxml"));
        botaoRelatorios.setOnAction(e -> selecionar(botaoRelatorios, "/fxml/gerente/relatorios.fxml"));
        botaoClientesVeiculos.setOnAction(e -> selecionar(botaoClientesVeiculos, "/fxml/gerente/clientes_veiculos.fxml"));

        selecionar(botaoFuncionarios, "/fxml/gerente/gestao_funcionarios.fxml");
    }

    private void selecionar(Button botao, String caminhoFxml) {
        marcarBotaoAtivo(botao, botaoFuncionarios, botaoDesignarVistoriador, botaoFinanceiro, botaoRelatorios, botaoClientesVeiculos);
        carregarTela(caminhoFxml);
    }
}