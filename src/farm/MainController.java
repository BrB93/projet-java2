package farm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private BorderPane root;

    private Farm farm;

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Interface initialisée !");

        farm = new Farm(500, 5);

        // Charger store.fxml
        try {
            FXMLLoader loaderStore = new FXMLLoader(getClass().getResource("/fxml/store.fxml"));
            Node storeNode = loaderStore.load();
            StoreController storeCtrl = loaderStore.getController();
            storeCtrl.setFarm(farm);
            root.setRight(storeNode);

            // Charger dashboard.fxml
            FXMLLoader loaderDashboard = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Node dashboardNode = loaderDashboard.load();
            DashboardController dashCtrl = loaderDashboard.getController();
            dashCtrl.setFarm(farm);
            root.setBottom(dashboardNode);

            // Timeline
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(1), event -> {
                        farm.updateAll();
                        // rafraîchir le dashboard
                        dashCtrl.refresh();
                    })
            );
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
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