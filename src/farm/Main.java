package farm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            URL fxmlUrl = getClass().getClassLoader().getResource("fxml/main.fxml");

            if (fxmlUrl == null) {
                System.err.println("Fichier FXML non trouvé ! Vérifiez le chemin.");
                return;
            }

            Parent root = FXMLLoader.load(fxmlUrl);
            primaryStage.setTitle("Farm Application");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}