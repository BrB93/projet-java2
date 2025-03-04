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
            // Chargement du fichier main.fxml qui est votre interface principale
            URL mainUrl = getClass().getResource("/fxml/main.fxml");

            if (mainUrl == null) {
                System.err.println("Fichier main.fxml non trouvé ! Vérifiez le chemin.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(mainUrl);
            Parent root = loader.load();

            // Récupération du contrôleur principal
            MainController controller = loader.getController();

            // Création d'une ferme (si nécessaire)
            Farm farm = new Farm(1000.0, 5); // Exemple de valeurs: 1000.0 d'argent initial et 5 pour une autre propriété
            controller.setFarm(farm);

            // Configuration de la fenêtre principale
            primaryStage.setTitle("Farm Game");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'interface :");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}