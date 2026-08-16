package br.com.william.autovistordesktop.client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NavigationService {

    private static NavigationService instancia;
    private Stage stagePrincipal;

    private NavigationService() {}

    public static NavigationService getInstancia() {
        if (instancia == null) {
            instancia = new NavigationService();
        }
        return instancia;
    }

    public void registrarStage(Stage stage) {
        this.stagePrincipal = stage;
    }

    public void navegarPara(String caminhoFxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoFxml));
            Parent root = loader.load();

            Scene scene = stagePrincipal.getScene();
            if (scene == null) {
                scene = new Scene(root);
                stagePrincipal.setScene(scene);
            } else {
                scene.setRoot(root);
            }

            stagePrincipal.setTitle(titulo);
            stagePrincipal.sizeToScene();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar tela: " + caminhoFxml, e);
        }
    }
}