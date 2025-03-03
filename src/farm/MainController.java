package farm;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

public class MainController {

    @FXML
    private BorderPane root;

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
}
