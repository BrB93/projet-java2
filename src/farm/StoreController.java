package farm;

import farm.Crop;
import farm.Animal;
import farm.Farm;
import javafx.fxml.FXML;

public class StoreController {

    private Farm farm;

    public void setFarm(Farm farm) {
        this.farm = farm;
    }

    @FXML
    private void buyWheat() {
        Crop wheat = new Crop("Blé", 10, 20, 5000); // 5 sec pour pousser
        farm.buyCrop(wheat);
    }

    @FXML
    private void buyCorn() {
        Crop corn = new Crop("Maïs", 15, 30, 8000); // 8 sec
        farm.buyCrop(corn);
    }

    @FXML
    private void buyCow() {
        Animal cow = new Animal("Vache", 100, 5); // ex: 10 sec pour produire du lait
        farm.buyAnimal(cow);
    }

    @FXML
    private void sellCrops() {
        // Vendre toutes les cultures matures
        farm.getCrops().stream()
                .filter(Crop::isMature)
                .forEach(crop -> farm.sellCrop(crop));
    }
}
