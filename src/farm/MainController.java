package farm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;



import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private BorderPane root;

    private Farm farm;

    @FXML
    public void initialize() {
        // Initialiser la ferme avec 500 pièces et 5 parcelles par exemple
        farm = new Farm(500, 5);

        // Créer un Timeline qui se répète indéfiniment
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    farm.updateAll();
                    // plus tard : rafraîchir l’interface
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    @FXML
    private void handleSave() {
        System.out.println("Sauvegarde...");
        // On implémentera la logique de sauvegarde plus tard
    }

    @FXML
    private void handleLoad() {
        System.out.println("Chargement...");
        // On implémentera la logique de chargement plus tard
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Code d'initialisation ici
        System.out.println("Interface initialisée !");
    }

    @FXML
    public void handleStart(ActionEvent event) {
        System.out.println("Bouton Démarrer cliqué");
        // Ajoutez votre logique ici
    }

    @FXML
    public void handleSettings(ActionEvent event) {
        System.out.println("Bouton Paramètres cliqué");
        // Ajoutez votre logique ici
    }
}