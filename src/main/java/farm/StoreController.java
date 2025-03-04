package farm;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class StoreController {
    private Farm farm;

    @FXML
    private VBox storeContainer;

    @FXML
    private Label messageLabel;

    public void setFarm(Farm farm) {
        this.farm = farm;
        initializeStore();
    }

    private void initializeStore() {
        // Sera implémenté plus tard avec les boutons d'achat
    }

    @FXML
    private void buySeed(String cropType) {
        double price = getCropPrice(cropType);
        if (farm.spendMoney(price)) {
            // Ajouter une graine à l'inventaire
            messageLabel.setText("Graine de " + cropType + " achetée !");
        } else {
            messageLabel.setText("Pas assez d'argent !");
        }
    }

    @FXML
    private void buyAnimal(String animalType) {
        double price = getAnimalPrice(animalType);
        if (farm.spendMoney(price)) {
            // Ajouter un animal à l'inventaire
            messageLabel.setText(animalType + " acheté !");
        } else {
            messageLabel.setText("Pas assez d'argent !");
        }
    }

    private double getCropPrice(String cropType) {
        switch (cropType) {
            case "blé": return 10.0;
            case "maïs": return 15.0;
            case "carotte": return 8.0;
            default: return 0;
        }
    }

    private double getAnimalPrice(String animalType) {
        switch (animalType) {
            case "poule": return 50.0;
            case "vache": return 200.0;
            case "cochon": return 150.0;
            default: return 0;
        }
    }

    @FXML
    private void buyWheat() {
        buySeed("blé");
    }

    @FXML
    private void buyCorn() {
        buySeed("maïs");
    }

    @FXML
    private void buyCarrot() {
        buySeed("carotte");
    }

    @FXML
    private void buyChicken() {
        buyAnimal("poule");
    }

    @FXML
    private void buyCow() {
        buyAnimal("vache");
    }

    @FXML
    private void buyPig() {
        buyAnimal("cochon");
    }

}