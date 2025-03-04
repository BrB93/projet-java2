package farm;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

public class MainController {
    // Ajouter cette déclaration
    private Farm farm;

    @FXML
    private BorderPane root;

    @FXML
    private Node storePane;

    @FXML
    private DashboardController dashboardPaneController;

    @FXML
    private StoreController storePaneController;

    public void setFarm(Farm farm) {
        this.farm = farm;

        // Initialiser les sous-contrôleurs
        if (dashboardPaneController != null) {
            dashboardPaneController.setFarm(farm);
            dashboardPaneController.refresh();
        }

        if (storePaneController != null) {
            storePaneController.setFarm(farm);
        }
    }

    @FXML
    private void handleStart() {
        System.out.println("Jeu démarré");
        // Utiliser dashboardPaneController au lieu de dashboardController
        dashboardPaneController.refresh();
    }

    @FXML
    private void handleSettings() {
        System.out.println("Paramètres ouverts");
    }

    @FXML
    private void handleSave() {
        farm.saveFarmState();
    }

    @FXML
    private void handleLoad() {
        Farm loadedFarm = Farm.loadFarmState();
        if (loadedFarm != null) {
            this.farm = loadedFarm;
            // Utiliser dashboardPaneController au lieu de dashboardController
            dashboardPaneController.setFarm(farm);
            dashboardPaneController.refresh();
            System.out.println("Ferme chargée et interface mise à jour");
        }
    }
}