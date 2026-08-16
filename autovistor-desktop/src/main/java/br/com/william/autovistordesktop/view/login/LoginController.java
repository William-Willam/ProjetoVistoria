package br.com.william.autovistordesktop.view.login;

import br.com.william.autovistordesktop.client.ApiClient;
import br.com.william.autovistordesktop.client.ApiException;
import br.com.william.autovistordesktop.client.JwtUtil;
import br.com.william.autovistordesktop.client.NavigationService;
import br.com.william.autovistordesktop.model.LoginRequest;
import br.com.william.autovistordesktop.model.LoginResponse;
import br.com.william.autovistordesktop.session.SessaoUsuario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField campoEmail;
    @FXML private PasswordField campoSenha;
    @FXML private Button botaoEntrar;
    @FXML private Label labelErro;

    private final ApiClient apiClient = new ApiClient();

    @FXML
    private void handleLogin() {
        labelErro.setText("");

        String email = campoEmail.getText().trim();
        String senha = campoSenha.getText();

        if (email.isBlank() || senha.isBlank()) {
            labelErro.setText("Preencha e-mail e senha.");
            return;
        }

        botaoEntrar.setDisable(true);

        try {
            LoginRequest request = new LoginRequest(email, senha);
            LoginResponse response = apiClient.post("/auth/login", request, LoginResponse.class, false);

            Long id = JwtUtil.extrairId(response.getToken());
            SessaoUsuario.getInstancia().iniciar(response.getToken(), id, response.getTipo(), response.getRole());

            if ("ROLE_GERENTE".equals(response.getRole())) {
                NavigationService.getInstancia().navegarPara(
                        "/fxml/gerente/dashboard_gerente.fxml", "AutoVistor — Gerente");
            } else if ("ROLE_VISTORIADOR".equals(response.getRole())) {
                NavigationService.getInstancia().navegarPara(
                        "/fxml/vistoriador/dashboard_vistoriador.fxml", "AutoVistor — Vistoriador");
            } else {
                labelErro.setText("Perfil sem tela desktop disponível.");
            }

        } catch (ApiException e) {
            labelErro.setText(e.getMessage());
        } finally {
            botaoEntrar.setDisable(false);
        }
    }
}