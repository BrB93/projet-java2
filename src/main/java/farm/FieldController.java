package farm;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.ListView;

import java.util.Map;
import java.util.logging.Logger;

public class FieldController {
    private static final Logger LOGGER = Logger.getLogger(FieldController.class.getName());
    private static final int GRID_SIZE = 5;

    @FXML private GridPane fieldGrid;
    @FXML private VBox inventoryPanel;
    @FXML private ListView<String> inventoryListView;
    @FXML private Label selectedItemLabel;
    @FXML private Label inventoryLabel;

    private Farm farm;
    private String selectedItemType;
    private String selectedAction;

    @FXML
    private void initialize() {
        LOGGER.info("Initialisation du FieldController");
        setupFieldGrid();
        setupInventoryList();
        updateInventoryDisplay();
    }

    private void setupFieldGrid() {
        if (fieldGrid == null) {
            LOGGER.warning("fieldGrid est null");
            return;
        }

        fieldGrid.getChildren().clear();

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Button cellButton = createCellButton(row, col);
                fieldGrid.add(cellButton, col, row);
            }
        }
    }

    private Button createCellButton(int row, int col) {
        Button cellButton = new Button();
        cellButton.setPrefSize(60, 60);
        cellButton.getStyleClass().add("field-cell");
        cellButton.setUserData(row + "," + col);
        cellButton.setOnAction(e -> handleCellClick(row, col, cellButton));
        return cellButton;
    }

    private void setupInventoryList() {
        if (inventoryListView == null) {
            LOGGER.warning("inventoryListView est null");
            return;
        }

        inventoryListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedItemType = extractItemType(newVal);
                selectedAction = determineAction(selectedItemType);
                selectedItemLabel.setText("Sélectionné: " + newVal);
                LOGGER.info("Élément sélectionné: " + newVal);
            }
        });
    }

    private String extractItemType(String displayText) {
        if (displayText == null) return "";
        int bracketIndex = displayText.indexOf(" (");
        return bracketIndex > 0 ? displayText.substring(0, bracketIndex) : displayText;
    }

    private String determineAction(String type) {
        return isCropType(type) ? "plant" : isAnimalType(type) ? "place" : "";
    }

    private boolean isCropType(String type) {
        return type.equalsIgnoreCase("ble") ||
                type.equalsIgnoreCase("mais") ||
                type.equalsIgnoreCase("carotte");
    }

    private boolean isAnimalType(String type) {
        return type.equalsIgnoreCase("vache") ||
                type.equalsIgnoreCase("poule") ||
                type.equalsIgnoreCase("cochon");
    }

    private void handleCellClick(int row, int col, Button cellButton) {
        if (farm == null || selectedItemType == null || selectedItemType.isEmpty()) {
            return;
        }

        if ("plant".equals(selectedAction) && farm.hasCropInInventory(selectedItemType)) {
            placeCrop(selectedItemType, row, col, cellButton);
        } else if ("place".equals(selectedAction) && hasAnimalInInventory(selectedItemType)) {
            placeAnimal(selectedItemType, row, col, cellButton);
        } else {
            selectedItemLabel.setText("Achetez d'abord " +
                    ("plant".equals(selectedAction) ? "des graines de " : "un ") + selectedItemType);
        }
    }

    private void placeCrop(String cropType, int row, int col, Button cellButton) {
        Crop crop = new Crop(cropType);
        crop.setPosition(row + "," + col);
        farm.removeCropFromInventory(cropType);
        farm.getPlantedCrops().add(crop);
        updateCell(cellButton, cropType, "crop-cell");
        updateInventoryDisplay();
        LOGGER.info("Culture " + cropType + " placée en " + row + "," + col);
    }

    private void placeAnimal(String animalType, int row, int col, Button cellButton) {
        Animal animal = new Animal(animalType);
        animal.setPosition(row + "," + col);
        farm.getInventory().put(animalType, farm.getInventory().getOrDefault(animalType, 1) - 1);
        farm.getAnimals().add(animal);
        updateCell(cellButton, animalType, "animal-cell");
        updateInventoryDisplay();
        LOGGER.info("Animal " + animalType + " placé en " + row + "," + col);
    }

    private void updateCell(Button cellButton, String text, String styleClass) {
        cellButton.setText(text);
        cellButton.getStyleClass().add(styleClass);
    }

    private boolean hasAnimalInInventory(String animalType) {
        return farm.getInventory().getOrDefault(animalType, 0) > 0;
    }

    public void updateInventoryDisplay() {
        if (farm == null || farm.getInventory() == null) return;

        StringBuilder inventory = new StringBuilder("Stock: ");
        farm.getInventory().forEach((item, quantity) ->
                inventory.append(item).append("(").append(quantity).append(") "));
        inventoryLabel.setText(inventory.toString());

        updateInventoryListView();
    }

    private void updateInventoryListView() {
        if (inventoryListView == null) return;

        inventoryListView.getItems().clear();
        farm.getInventory().entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .forEach(entry -> inventoryListView.getItems().add(
                        entry.getKey() + " (Qté: " + entry.getValue() + ")"));
    }

    public void updateField(Farm farm) {
        if (farm == null) {
            LOGGER.warning("Tentative de mise à jour avec farm null");
            return;
        }

        this.farm = farm;
        updateInventoryDisplay();
        updateFieldDisplay();
    }

    private void updateFieldDisplay() {
        if (fieldGrid == null || farm == null) return;
        clearField();
        updateCropsDisplay();
        updateAnimalsDisplay();
    }

    private void clearField() {
        fieldGrid.getChildren().stream()
                .filter(node -> node instanceof Button)
                .map(node -> (Button) node)
                .forEach(button -> {
                    button.setText("");
                    button.getStyleClass().removeAll("crop-cell", "animal-cell");
                });
    }

    private void updateCropsDisplay() {
        if (farm.getPlantedCrops() != null) {
            farm.getPlantedCrops().stream()
                    .filter(crop -> crop.getPosition() != null)
                    .forEach(this::displayCrop);
        }
    }

    private void updateAnimalsDisplay() {
        if (farm.getAnimals() != null) {
            farm.getAnimals().stream()
                    .filter(animal -> animal.getPosition() != null)
                    .forEach(this::displayAnimal);
        }
    }

    private void displayCrop(Crop crop) {
        displayGridItem(crop.getPosition(), crop.getName(), "crop-cell");
    }

    private void displayAnimal(Animal animal) {
        displayGridItem(animal.getPosition(), animal.getType(), "animal-cell");
    }

    private void displayGridItem(String position, String text, String styleClass) {
        if (position == null) return;

        String[] coords = position.split(",");
        if (coords.length != 2) return;

        try {
            int row = Integer.parseInt(coords[0]);
            int col = Integer.parseInt(coords[1]);
            Button cellButton = getCellButton(row, col);
            if (cellButton != null) {
                updateCell(cellButton, text, styleClass);
            }
        } catch (NumberFormatException e) {
            LOGGER.warning("Position invalide: " + position);
        }
    }

    private Button getCellButton(int row, int col) {
        return (Button) fieldGrid.getChildren().stream()
                .filter(node -> node instanceof Button &&
                        GridPane.getRowIndex(node) == row &&
                        GridPane.getColumnIndex(node) == col)
                .findFirst()
                .orElse(null);
    }

    @FXML private void selectWheat() { selectItem("ble", "plant", "Blé"); }
    @FXML private void selectCorn() { selectItem("mais", "plant", "Maïs"); }
    @FXML private void selectCarrot() { selectItem("carotte", "plant", "Carotte"); }
    @FXML private void selectCow() { selectItem("vache", "place", "Vache"); }
    @FXML private void selectChicken() { selectItem("poule", "place", "Poule"); }
    @FXML private void selectPig() { selectItem("cochon", "place", "Cochon"); }

    private void selectItem(String type, String action, String displayName) {
        selectedItemType = type;
        selectedAction = action;
        selectedItemLabel.setText("Sélectionné: " + displayName);
        LOGGER.info(displayName + " sélectionné pour " +
                (action.equals("plant") ? "plantation" : "placement"));
    }

    public void setFarm(Farm farm) {
        this.farm = farm;
        updateInventoryDisplay();
    }
}