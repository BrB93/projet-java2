package farm;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.util.logging.Logger;
import java.util.logging.Level;

public class StoreController {
    private static final Logger LOGGER = Logger.getLogger(StoreController.class.getName());

    private Farm farm;

    @FXML
    private Label messageLabel;

    @FXML
    private void initialize() {
        LOGGER.info("Initialisation du StoreController");
    }

    public void setFarm(Farm farm) {
        this.farm = farm;
        LOGGER.info("Farm définie dans StoreController");
    }

    private void buySeed(String type, double price) {
        if (farm == null) {
            LOGGER.severe("Tentative d'achat de graines alors que farm est null");
            messageLabel.setText("Erreur: Le jeu n'est pas démarré");
            return;
        }

        if (farm.spendMoney(price)) {
            // Ajouter directement à l'inventaire
            farm.getInventory().put(type, farm.getInventory().getOrDefault(type, 0) + 1);
            messageLabel.setText("Achat de " + type + " réussi !");
            LOGGER.info("Achat de " + type + " réussi");
        } else {
            messageLabel.setText("Pas assez d'argent !");
            LOGGER.warning("Tentative d'achat de " + type + " sans argent suffisant");
        }
    }

    @FXML
    private void buyWheat() {
        buySeed("ble", 10.0);
    }

    @FXML
    private void buyCorn() {
        buySeed("mais", 15.0);
    }

    @FXML
    private void buyCarrot() {
        buySeed("carotte", 8.0);
    }

    private void buyAnimal(String type, double price) {
        if (farm == null) {
            LOGGER.severe("Tentative d'achat d'un animal alors que farm est null");
            messageLabel.setText("Erreur: Le jeu n'est pas démarré");
            return;
        }

        if (farm.spendMoney(price)) {
            // Ajouter directement à l'inventaire
            farm.getInventory().put(type, farm.getInventory().getOrDefault(type, 0) + 1);
            messageLabel.setText("Achat d'un " + type + " réussi !");
            LOGGER.info("Achat d'un " + type + " réussi");
        } else {
            messageLabel.setText("Pas assez d'argent !");
            LOGGER.warning("Tentative d'achat d'un " + type + " sans argent suffisant");
        }
    }

    @FXML
    private void buyChicken() {
        buyAnimal("poule", 50.0);
    }

    @FXML
    private void buyCow() {
        buyAnimal("vache", 200.0);
    }

    @FXML
    private void buyPig() {
        buyAnimal("cochon", 150.0);
    }
}