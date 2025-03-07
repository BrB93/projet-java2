package farm;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Contrôleur pour la boutique du jeu
 */
public class StoreController {
    private static final Logger LOGGER = Logger.getLogger(StoreController.class.getName());

    private Farm farm;

    @FXML
    private Label messageLabel;

    @FXML
    private void initialize() {
        LOGGER.info("Initialisation du StoreController");
    }

    /**
     * Définit l'instance de la ferme utilisée par ce contrôleur
     * @param farm la ferme du joueur
     */
    public void setFarm(Farm farm) {
        this.farm = farm;
        LOGGER.info("Farm définie dans StoreController");
    }

    /**
     * Méthode générique pour acheter des graines
     * @param type le type de graine
     * @param price le prix d'achat
     */
    private void buySeed(String type, double price) {
        if (farm == null) {
            LOGGER.severe("Tentative d'achat de graines alors que farm est null");
            messageLabel.setText("Erreur: Le jeu n'est pas démarré");
            return;
        }

        if (farm.spendMoney((int)price)) {
            // Ajouter directement à l'inventaire
            farm.getInventory().put(type, farm.getInventory().getOrDefault(type, 0) + 1);
            messageLabel.setText("Achat de " + type + " réussi !");
        } else {
            messageLabel.setText("Pas assez d'argent !");
        }
    }

    /**
     * Achète des graines de blé
     */
    @FXML
    private void buyWheat() {
        buySeed("ble", 10.0);
    }

    /**
     * Achète des graines de maïs
     */
    @FXML
    private void buyCorn() {
        buySeed("mais", 15.0);
    }

    /**
     * Achète des graines de carotte
     */
    @FXML
    private void buyCarrot() {
        buySeed("carotte", 8.0);
    }

    /**
     * Méthode générique pour acheter des animaux
     * @param type le type d'animal
     * @param price le prix d'achat
     */
    private void buyAnimal(String type, double price) {
        if (farm == null) {
            LOGGER.severe("Tentative d'achat d'un animal alors que farm est null");
            messageLabel.setText("Erreur: Le jeu n'est pas démarré");
            return;
        }

        if (farm.spendMoney((int)price)) {
            // Ajouter directement à l'inventaire
            farm.getInventory().put(type, farm.getInventory().getOrDefault(type, 0) + 1);
            messageLabel.setText("Achat d'un " + type + " réussi !");
            LOGGER.info("Achat d'un " + type + " réussi");
        } else {
            messageLabel.setText("Pas assez d'argent !");
            LOGGER.warning("Tentative d'achat d'un " + type + " sans argent suffisant");
        }
    }

    /**
     * Achète une poule
     */
    @FXML
    private void buyChicken() {
        buyAnimal("poule", 50.0);
    }

    /**
     * Achète une vache
     */
    @FXML
    private void buyCow() {
        buyAnimal("vache", 200.0);
    }

    /**
     * Achète un mouton
     */
    @FXML
    private void buySheep() {
        buyAnimal("mouton", 150.0);
    }

    /**
     * Vérifie si la ferme a assez d'argent pour un achat
     * @param amount le montant à vérifier
     * @return true si la ferme a assez d'argent
     */
    private boolean hasEnoughMoney(int amount) {
        return farm != null && farm.getMoney() >= amount;
    }

    /**
     * Affiche un message dans le label de la boutique
     * @param message le message à afficher
     */
    public void displayMessage(String message) {
        messageLabel.setText(message);
    }

    /**
     * Réinitialise le message affiché
     */
    public void clearMessage() {
        messageLabel.setText("");
    }
}