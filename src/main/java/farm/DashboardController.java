package farm;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {
    private Farm farm;

    @FXML
    private Label moneyLabel;

    public void setFarm(Farm farm) {
        this.farm = farm;
        refresh();
    }

    public void refresh() {
        if (farm != null) {
            Platform.runLater(() -> {
                moneyLabel.setText("Argent : " + farm.getMoney() + " €");
            });
        }
    }
}