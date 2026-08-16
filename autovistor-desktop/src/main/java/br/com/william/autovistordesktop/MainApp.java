package br.com.william.autovistordesktop;

import br.com.william.autovistordesktop.client.NavigationService;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        NavigationService.getInstancia().registrarStage(stage);
        NavigationService.getInstancia().navegarPara("/fxml/login/login.fxml", "AutoVistor — Login");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}