package farm;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Code d'initialisation ici
        System.out.println("Interface initialisée !");
    }

    public void handleStart(ActionEvent event) {
        System.out.println("Bouton Démarrer cliqué");
        // Ajoutez votre logique ici
    }

    public void handleSettings(ActionEvent event) {
        System.out.println("Bouton Paramètres cliqué");
        // Ajoutez votre logique ici
    }
}