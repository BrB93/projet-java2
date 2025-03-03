package farm;

import farm.Farm;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML
    private Label moneyLabel;

    private Farm farm;

    public void setFarm(Farm farm) {
        this.farm = farm;
    }

    public void refresh() {
        moneyLabel.setText("Argent : " + farm.getMoney());
    }
}