package farm;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.scene.control.Tooltip;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.*;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.util.Pair;
import java.util.List;
import java.util.logging.Logger;

public class FieldController {
    private static final Logger LOGGER = Logger.getLogger(FieldController.class.getName());
    private static final int GRID_SIZE = 5;

    @FXML
    private GridPane fieldGrid;
    @FXML
    private VBox inventoryPanel;
    @FXML
    private Label selectedItemLabel;
    @FXML
    private Label inventoryLabel;
    @FXML
    private TableView<InventoryItem> inventoryTableView;
    @FXML
    private TableColumn<InventoryItem, String> categoryColumn;
    @FXML
    private TableColumn<InventoryItem, String> itemColumn;
    @FXML
    private TableColumn<InventoryItem, Integer> quantityColumn;
    @FXML
    private TableColumn<InventoryItem, Integer> valueColumn;

    @FXML
    private Label balanceLabel;

    private Farm farm;
    private String selectedItemType;
    private String selectedAction;
    private MainController mainController;
    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    @FXML
    private void initialize() {
        setupFieldGrid();
        itemColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        setupInventoryTable();
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        setupGameLoop();
        updateInventoryDisplay();
    }

    @FXML
    private void handleSellButtonClick() {
        Dialog<Pair<String, Integer>> dialog = new Dialog<>();
        dialog.setTitle("Vendre des produits");
        dialog.setHeaderText("Choisissez un produit à vendre");
        ButtonType buttonTypeOk = new ButtonType("Vendre", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        Map<String, Integer> inventory = farm.getInventory();
        ComboBox<String> productComboBox = new ComboBox<>();
        addProductIfAvailable(inventory, productComboBox, "oeuf");
        addProductIfAvailable(inventory, productComboBox, "lait");
        addProductIfAvailable(inventory, productComboBox, "laine");
        addProductIfAvailable(inventory, productComboBox, "ble_recolte");
        addProductIfAvailable(inventory, productComboBox, "mais_recolte");
        addProductIfAvailable(inventory, productComboBox, "carotte_recolte");
        addProductIfAvailable(inventory, productComboBox, "ble");
        addProductIfAvailable(inventory, productComboBox, "mais");
        addProductIfAvailable(inventory, productComboBox, "carotte");

        if (productComboBox.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Aucun produit disponible à vendre.");
            alert.showAndWait();
            return;
        }

        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1, 1);
        Spinner<Integer> quantitySpinner = new Spinner<>();
        quantitySpinner.setValueFactory(valueFactory);

        Label priceLabel = new Label();
        productComboBox.getSelectionModel().selectFirst();
        String initialProduct = productComboBox.getValue();
        int initialMaxQuantity = getMaxQuantity(initialProduct, inventory);
        valueFactory.setMax(initialMaxQuantity);
        updatePriceLabel(priceLabel, initialProduct);
        productComboBox.setOnAction(e -> {
            String selectedProduct = productComboBox.getValue();
            int maxQuantity = getMaxQuantity(selectedProduct, inventory);
            valueFactory.setMax(maxQuantity);
            valueFactory.setValue(1);
            updatePriceLabel(priceLabel, selectedProduct);
        });
        grid.add(new Label("Produit:"), 0, 0);
        grid.add(productComboBox, 1, 0);
        grid.add(new Label("Quantité:"), 0, 1);
        grid.add(quantitySpinner, 1, 1);
        grid.add(new Label("Prix unitaire:"), 0, 2);
        grid.add(priceLabel, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == buttonTypeOk) {
                return new Pair<>(productComboBox.getValue(), quantitySpinner.getValue());
            }
            return null;
        });

        Optional<Pair<String, Integer>> result = dialog.showAndWait();
        result.ifPresent(productQuantity -> {
            String product = productQuantity.getKey();
            int quantity = productQuantity.getValue();
            int pricePerUnit = getPriceForItem(product);

            boolean success = farm.sellResource(product, quantity, pricePerUnit);
            if (success) {
                updateUI();

                String displayName = getResourceDisplayName(product, quantity);
                int totalPrice = quantity * pricePerUnit;

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Vente réussie");
                alert.setHeaderText(null);
                alert.setContentText("Vous avez vendu " + quantity + " " + displayName + " pour " + totalPrice + " €");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Échec de la vente");
                alert.setHeaderText(null);
                alert.setContentText("La vente a échoué. Vérifiez votre inventaire.");
                alert.showAndWait();
            }
        });
    }

    private void addProductIfAvailable(Map<String, Integer> inventory, ComboBox<String> comboBox, String productKey) {
        if (inventory.containsKey(productKey) && inventory.get(productKey) > 0) {
            comboBox.getItems().add(productKey);
        }
    }

    private void updatePriceLabel(Label priceLabel, String product) {
        int price = getPriceForItem(product);
        priceLabel.setText(price + " €");
    }

    private int getPriceForItem(String product) {
        if (product.endsWith("_recolte"))
            return getPrice(product, "crop");
        else if (Arrays.asList("poule", "vache", "mouton").contains(product))
            return getPrice(product, "animal");
        else if (Arrays.asList("oeuf", "lait", "laine").contains(product))
            return getPrice(product, "product");
        else if (Arrays.asList("ble", "mais", "carotte").contains(product))
            return getPrice(product, "seed");
        return 0;
    }

    private String getResourceDisplayName(String resource, int amount) {
        switch (resource) {
            case "oeuf":
                return amount > 1 ? "œufs" : "œuf";
            case "lait":
                return "litre" + (amount > 1 ? "s" : "") + " de lait";
            case "laine":
                return "ballot" + (amount > 1 ? "s" : "") + " de laine";
            case "ble_recolte":
                return "sac" + (amount > 1 ? "s" : "") + " de blé";
            case "mais_recolte":
                return "épi" + (amount > 1 ? "s" : "") + " de maïs";
            case "carotte_recolte":
                return "carotte" + (amount > 1 ? "s" : "");
            default:
                return resource;
        }
    }

    private int getMaxQuantity(String product, Map<String, Integer> inventory) {
        return inventory.getOrDefault(product, 0);
    }

    private void updateUI() {
        updateInventoryDisplay();
        if (balanceLabel != null) {
            balanceLabel.setText("Solde: " + farm.getMoney() + " €");
        }
    }

    private int getPricePerUnit(String resource) {
        switch (resource.toLowerCase()) {
            case "ble":
                return 5;
            case "mais":
                return 7;
            case "carotte":
                return 10;
            case "oeuf":
                return 5;
            case "lait":
                return 15;
            case "laine":
                return 20;
            default:
                return 1;
        }
    }

    private String getDisplayNameForProduct(String type) {
        switch (type.toLowerCase()) {
            case "oeuf": return "Œuf de poule";
            case "lait": return "Lait de vache";
            case "laine": return "Laine de mouton";
            default: return type;
        }
    }

    private void updateInventoryDisplay() {
        if (farm == null || inventoryTableView == null) {
            return;
        }

        Map<String, Integer> inventory = farm.getInventory();
        ObservableList<InventoryItem> items = FXCollections.observableArrayList();
        for (String type : Arrays.asList("poule", "vache", "mouton")) {
            if (inventory.containsKey(type) && inventory.get(type) > 0) {
                items.add(new InventoryItem("Animaux", type,
                        inventory.get(type), getPrice(type, "animal")));
            }
        }
        for (String type : Arrays.asList("ble", "mais", "carotte")) {
            if (inventory.containsKey(type) && inventory.get(type) > 0) {
                items.add(new InventoryItem("Graines", type,
                        inventory.get(type), getPrice(type, "seed")));
            }
        }

        for (String type : Arrays.asList("ble_recolte", "mais_recolte", "carotte_recolte")) {
            if (inventory.containsKey(type) && inventory.get(type) > 0) {
                String displayName = type.replace("_recolte", "");
                items.add(new InventoryItem("Récoltes", displayName,
                        inventory.get(type), getPrice(type, "crop")));
            }
        }
        for (String type : Arrays.asList("oeuf", "lait", "laine")) {
            if (inventory.containsKey(type) && inventory.get(type) > 0) {
                String category = "Productions";
                String displayName = getDisplayNameForProduct(type);
                int quantity = inventory.get(type);
                int value = getPrice(type, "product");

                items.add(new InventoryItem(category, displayName, quantity, value));
            }
        }
        inventoryTableView.setItems(items);
        if (balanceLabel != null) {
            balanceLabel.setText("Solde: " + farm.getMoney() + " €");        }
    }
    private int getPrice(String item, String category) {
        switch(category) {
            case "seed":
                switch(item.toLowerCase()) {
                    case "ble": return 5;
                    case "mais": return 8;
                    case "carotte": return 3;
                    default: return 0;
                }
            case "crop":
                switch(item.toLowerCase()) {
                    case "ble_recolte": return 15;
                    case "mais_recolte": return 20;
                    case "carotte_recolte": return 10;
                    default: return 0;
                }
            case "animal":
                switch(item.toLowerCase()) {
                    case "poule": return 50;
                    case "vache": return 300;
                    case "mouton": return 200;
                    default: return 0;
                }
            case "product":
                switch(item.toLowerCase()) {
                    case "oeuf": return 5;
                    case "lait": return 12;
                    case "laine": return 15;
                    default: return 0;
                }
            default: return 0;
        }
    }
    private void updateAnimals() {
        if (farm == null) return;

        List<Animal> animals = farm.getAnimals();
        if (animals == null || animals.isEmpty()) return;
        animals.removeIf(animal -> {
            if (animal.isStarving()) {
                // Récupérer la position pour nettoyer la cellule
                String position = animal.getPosition();
                String[] coords = position.split(",");
                int row = Integer.parseInt(coords[0]);
                int col = Integer.parseInt(coords[1]);
                clearCell(row, col);

                selectedItemLabel.setText("Un animal est mort de faim à la position " + position);
                return true;
            }
            return false;
        });
        boolean productionMade = false;
        for (Animal animal : animals) {
            if (animal.canProduce()) {
                animal.produceResource(farm);
                productionMade = true;
            }
        }

        if (productionMade) {
            updateInventoryDisplay();
        }
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
        cellButton.setPrefSize(100, 100);
        cellButton.getStyleClass().add("empty-cell"); // Remplacer "field-cell" par "empty-cell"
        cellButton.setUserData(row + "," + col);
        cellButton.setOnAction(e -> handleCellClick(row, col, cellButton));
        return cellButton;
    }

    private void setupInventoryTable() {
        if (inventoryTableView == null) {
            LOGGER.warning("inventoryTableView est null");
            return;
        }
        itemColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        quantityColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        inventoryTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String itemName = newVal.getName();
                if (newVal.getCategory().equals("Récoltes")) {
                    itemName = itemName + "_recolte";
                }

                selectedItemType = itemName;
                selectedAction = determineAction(selectedItemType);
                selectedItemLabel.setText("Sélectionné: " + newVal.getName() + " (" +
                        newVal.getQuantity() + " unités à " + newVal.getValue() + "€)");
            }
        });
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
                type.equalsIgnoreCase("mouton");
    }

    private void handleCellClick(int row, int col, Button cellButton) {
        if (farm == null) {
            return;
        }

        String position = row + "," + col;
        Crop cropToHarvest = farm.getPlantedCrops().stream()
                .filter(crop -> crop.getPosition().equals(position) && crop.isReadyToHarvest())
                .findFirst()
                .orElse(null);

        if (cropToHarvest != null) {
            harvestCrop(cropToHarvest, cellButton);
            return;
        }

        if ("feed".equals(selectedAction)) {
            feedAnimalAt(row, col);
            return;
        }
        if (selectedItemType == null || selectedItemType.isEmpty()) {
            return;
        }
        if (!cellButton.getText().isEmpty()) {
            if (selectedItemLabel != null) {
                selectedItemLabel.setText("Case déjà occupée !");
            }
            return;
        }
        if ("plant".equals(selectedAction) && farm.getInventory().containsKey(selectedItemType)) {
            placeCrop(selectedItemType, row, col, cellButton);
        } else if ("place".equals(selectedAction) && hasAnimalInInventory(selectedItemType)) {
            placeAnimal(selectedItemType, row, col, cellButton);
        } else {
            if (selectedItemLabel != null) {
                selectedItemLabel.setText("Achetez d'abord " +
                        ("plant".equals(selectedAction) ? "des graines de " : "un ") + selectedItemType);
            }
        }
    }

    private void feedAnimalAt(int row, int col) {
        String position = row + "," + col;
        Animal animal = farm.getAnimals().stream()
                .filter(a -> position.equals(a.getPosition()))
                .findFirst().orElse(null);

        if (animal == null) {
            selectedItemLabel.setText("Aucun animal à cette position");
            return;
        }
        String foodType = animal.getType().equals("poule") ? "mais" :
                (animal.getType().equals("vache") ? "ble" : "carotte");
        if (farm.getInventory().getOrDefault(foodType, 0) > 0) {
            farm.getInventory().put(foodType, farm.getInventory().get(foodType) - 1);
            animal.feed();
            updateCellDisplay(animal);
            updateInventoryDisplay();
            selectedItemLabel.setText("Animal nourri avec " + foodType + " - Croissance accélérée!");
        } else {
            selectedItemLabel.setText("Pas de " + foodType + " disponible - Récoltez plus de cultures!");
        }
    }

    private void placeCrop(String cropType, int row, int col, Button cellButton) {
        Crop crop = new Crop(cropType);
        crop.setPosition(row + "," + col);

        // Retirer une unité de l'inventaire
        int currentQuantity = farm.getInventory().getOrDefault(cropType, 0);
        if (currentQuantity > 0) {
            farm.getInventory().put(cropType, currentQuantity - 1);
            if (farm.getInventory().get(cropType) == 0) {
                farm.getInventory().remove(cropType);
            }
        }

        farm.getPlantedCrops().add(crop);

        updateCellWithStage(cellButton, cropType, crop.getDisplayText(),
                "crop-cell", crop.getStageStyleClass());

        updateInventoryDisplay();
    }
    private void harvestCrop(Crop cropToHarvest, Button cellButton) {
        farm.addToInventory(cropToHarvest.getType(), cropToHarvest.getYield());
        farm.getPlantedCrops().removeIf(crop -> crop.getPosition().equals(cropToHarvest.getPosition()));

        cellButton.setText("");
        cellButton.getStyleClass().clear();
        cellButton.getStyleClass().add("empty-cell");
        Tooltip.uninstall(cellButton, null);
        updateInventoryDisplay();
        selectedItemLabel.setText("Récolté: " + cropToHarvest.getType() + " x" + cropToHarvest.getYield());
    }

    private void placeAnimal(String animalType, int row, int col, Button cellButton) {
        Animal animal = new Animal(animalType);
        animal.setPosition(row + "," + col);
        farm.getInventory().put(animalType, farm.getInventory().getOrDefault(animalType, 1) - 1);
        farm.getAnimals().add(animal);
        updateCellWithStage(cellButton, animalType, animal.getDisplayText(),
                "animal-cell", animal.getStageStyleClass());
        updateInventoryDisplay();
    }

    private boolean hasAnimalInInventory(String animalType) {
        return farm.getInventory().getOrDefault(animalType, 0) > 0;
    }

    public void loadStylesheet() {
        try {
            String cssPath = Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm();
            fieldGrid.getScene().getStylesheets().add(cssPath);
            LOGGER.info("Feuille de style chargée: " + cssPath);
        } catch (Exception e) {
            LOGGER.severe("Erreur lors du chargement de la feuille de style: " + e.getMessage());
        }
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

    private void setupGameLoop() {
        Timeline gameLoop = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            updateFieldDisplay();
            updateGrowthStages();
            updateAnimals();
            checkAnimalStatus();
        }));
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        gameLoop.play();
    }

    private Button getCellButton(int row, int col) {
        for (javafx.scene.Node node : fieldGrid.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                String userData = (String) button.getUserData();
                if (userData != null && userData.equals(row + "," + col)) {
                    return button;
                }
            }
        }
        return null;
    }

    private void updateFieldDisplay() {
        if (farm == null) return;
        clearField();
        for (Crop crop : farm.getPlantedCrops()) {
            crop.updateGrowthStage();
            String position = crop.getPosition();
            if (position != null) {
                String[] coordinates = position.split(",");
                int row = Integer.parseInt(coordinates[0]);
                int col = Integer.parseInt(coordinates[1]);
                Button cellButton = getCellButton(row, col);

                if (cellButton != null) {
                    updateCellWithStage(cellButton, crop.getType(), crop.getDisplayText(),
                            "crop-cell", crop.getStageStyleClass());
                }
            }
        }

        // Mettre à jour les animaux
        for (Animal animal : farm.getAnimals()) {
            animal.updateDevelopmentStage();
            String position = animal.getPosition();
            if (position != null) {
                String[] coordinates = position.split(",");
                int row = Integer.parseInt(coordinates[0]);
                int col = Integer.parseInt(coordinates[1]);
                Button cellButton = getCellButton(row, col);

                if (cellButton != null) {
                    updateCellWithStage(cellButton, animal.getType(), animal.getDisplayText(),
                            "animal-cell", animal.getStageStyleClass());
                }
            }
        }
    }

    private void clearField() {
        fieldGrid.getChildren().stream()
                .filter(node -> node instanceof Button)
                .map(node -> (Button) node)
                .forEach(button -> {
                    button.setText("");
                    button.getStyleClass().removeAll("crop-cell", "animal-cell",
                            "seed-stage", "growing-stage", "ready-stage",
                            "baby-stage", "young-stage", "mature-stage");
                    button.getStyleClass().add("empty-cell");
                    Tooltip.uninstall(button, null);
                });
    }

    private void updateCellWithStage(Button cellButton, String type, String text,
                                     String baseStyle, String stageStyle) {
        String displayText = text;
        if (baseStyle.equals("crop-cell")) {
            if (stageStyle.contains("seed-stage")) {
                displayText = "🌱\n" + text;
            } else if (stageStyle.contains("growing-stage")) {
                displayText = "🌿\n" + text;
            } else if (stageStyle.contains("ready-stage")) {
                if (type.contains("ble")) {
                    displayText = "🌾\n" + text;
                } else if (type.contains("mais")) {
                    displayText = "🌽\n" + text;
                } else if (type.contains("carotte")) {
                    displayText = "🥕\n" + text;
                }
            }
        }
        if (baseStyle.equals("animal-cell")) {
            if (type.equals("poule")) {
                displayText = (stageStyle.contains("baby-stage")) ? "🐤\n" + text : "🐓\n" + text;
            } else if (type.equals("vache")) {
                displayText = (stageStyle.contains("baby-stage")) ? "🐄\n" + text : "🐮\n" + text;
            } else if (type.equals("mouton")) {
                displayText = (stageStyle.contains("baby-stage")) ? "🐑\n" + text : "🐏\n" + text;
            }
            Animal animal = getAnimalAtPosition(cellButton.getUserData().toString());
            if (animal != null) {
                if (animal.isStarving()) {
                    displayText = "⚠️" + displayText;
                } else if (animal.needsFeeding()) {
                    displayText = "⚠️" + displayText;
                }
            }
        }

        cellButton.setText(displayText);
        cellButton.getStyleClass().removeAll("hungry-animal", "starving-animal");        cellButton.getStyleClass().add(baseStyle);

        if (stageStyle != null && !stageStyle.isEmpty()) {
            cellButton.getStyleClass().add(stageStyle);
        }
        String tooltipText = type + "\n" + text;
        if (baseStyle.equals("animal-cell")) {
            Animal animal = getAnimalAtPosition(cellButton.getUserData().toString());
            if (animal != null) {
                long currentTime = System.currentTimeMillis();
                long timeSinceLastFeed = currentTime - animal.getLastFeedTime();
                long secondsRemaining = 60 - (timeSinceLastFeed / 1000);

                if (animal.isStarving()) {
                    tooltipText += "\n⚠️ DANGER: Animal affamé!";
                    tooltipText += "\nVa mourir prochainement";
                    cellButton.getStyleClass().add("starving-animal");
                } else if (animal.needsFeeding()) {
                    tooltipText += "\n⚠️ A besoin d'être nourri!";
                    tooltipText += "\nDeviendra affamé dans " + (120 - timeSinceLastFeed/1000) + " secondes";
                    cellButton.getStyleClass().add("hungry-animal");
                } else {
                    tooltipText += "\n✓ Bien nourri";
                    tooltipText += "\nA nourrir dans " + secondsRemaining + " secondes";
                }

                String foodType = animal.getType().equals("poule") ? "maïs" :
                        (animal.getType().equals("vache") ? "blé" : "carotte");
                tooltipText += "\n\nNourriture: " + foodType;
            }
        }

        Tooltip tooltip = new Tooltip(tooltipText);
        Tooltip.install(cellButton, tooltip);
    }

    private void updateGrowthStages() {
        if (farm == null || farm.getPlantedCrops() == null) return;

        for (Crop crop : farm.getPlantedCrops()) {
            crop.updateGrowthStage();
            updateCellDisplay(crop);
        }
    }

    private void updateCellDisplay(Crop crop) {
        String position = crop.getPosition();
        if (position == null) return;

        String[] coords = position.split(",");
        if (coords.length != 2) return;

        try {
            int row = Integer.parseInt(coords[0]);
            int col = Integer.parseInt(coords[1]);
            for (javafx.scene.Node node : fieldGrid.getChildren()) {
                if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                    if (node instanceof Button) {
                        Button cellButton = (Button) node;
                        updateCellWithStage(cellButton, crop.getType(), crop.getDisplayText(),
                                "crop-cell", crop.getStageStyleClass());
                    }
                    break;
                }
            }
        } catch (NumberFormatException e) {
        }
    }

    private void selectItem(String type, String action, String displayName) {
        selectedItemType = type;
        selectedAction = action;
        selectedItemLabel.setText("Sélectionné: " + displayName);
        LOGGER.info(displayName + " sélectionné pour " + (action.equals("plant") ? "plantation" : "placement"));
    }

    @FXML private void selectWheat() { selectItem("ble", "plant", "Blé"); }
    @FXML private void selectCorn() { selectItem("mais", "plant", "Maïs"); }
    @FXML private void selectCarrot() { selectItem("carotte", "plant", "Carotte"); }
    @FXML private void selectChicken() { selectItem("poule", "place", "Poule"); }
    @FXML private void selectCow() { selectItem("vache", "place", "Vache"); }
    @FXML private void selectSheep() { selectItem("mouton", "place", "Mouton"); }

    private long lastUpdateTime = System.currentTimeMillis();

    public void resetTimer() {
        lastUpdateTime = System.currentTimeMillis();
    }

    @FXML
    private void handleFeedAction() {
        selectItem("nourriture", "feed", "Nourrir animal");
        selectedItemLabel.setText("Sélectionné: Nourrir animal - Cliquez sur un animal pour le nourrir");
    }

    private void updateCellDisplay(Animal animal) {
        String position = animal.getPosition();
        if (position == null) return;

        String[] coords = position.split(",");
        int row = Integer.parseInt(coords[0]);
        int col = Integer.parseInt(coords[1]);
        Button cellButton = getCellButton(row, col);

        if (cellButton != null) {
            updateCellWithStage(cellButton, animal.getType(), animal.getDisplayText(),
                    "animal-cell", animal.getStageStyleClass());
        }
    }

    private Animal getAnimalAtPosition(String position) {
        if (farm == null || farm.getAnimals() == null) return null;

        return farm.getAnimals().stream()
                .filter(a -> position.equals(a.getPosition()))
                .findFirst()
                .orElse(null);
    }

    public void checkStarvingAnimals() {
        if (farm == null || farm.getAnimals() == null) return;

        List<Animal> animalsToRemove = new ArrayList<>();

        for (Animal animal : farm.getAnimals()) {
            if (animal.isStarving()) {
                animalsToRemove.add(animal);
                String[] coords = animal.getPosition().split(",");
                int row = Integer.parseInt(coords[0]);
                int col = Integer.parseInt(coords[1]);
                clearCell(row, col);
                selectedItemLabel.setText("Un " + animal.getType() + " est mort de faim à la position " + animal.getPosition());
            }
        }
        farm.getAnimals().removeAll(animalsToRemove);
    }

    private void clearCell(int row, int col) {
        Button cellButton = getCellButton(row, col);
        if (cellButton != null) {
            cellButton.setText("");
            cellButton.getStyleClass().removeAll("animal-cell", "crop-cell",
                    "baby-stage", "young-stage", "mature-stage",
                    "hungry-animal", "starving-animal",
                    "seed-stage", "growing-stage", "ready-stage");
            cellButton.getStyleClass().add("empty-cell");
            Tooltip.uninstall(cellButton, null);
        }
    }
    @FXML
    private Label notificationLabel;

    public void checkAnimalStatus() {
        if (farm == null || farm.getAnimals() == null || farm.getAnimals().isEmpty()) {
            notificationLabel.setText("");
            return;
        }

        long hungryCount = farm.getAnimals().stream().filter(Animal::needsFeeding).count();
        long starvingCount = farm.getAnimals().stream().filter(Animal::isStarving).count();

        if (starvingCount > 0) {
            notificationLabel.setText("🚨 URGENCE: " + starvingCount + " animal(aux) risque(nt) de mourir de faim!");
            notificationLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 14;");
        } else if (hungryCount > 0) {
            notificationLabel.setText("🔔 Attention: " + hungryCount + " animal(aux) ont besoin d'être nourri(s)!");
            notificationLabel.setStyle("-fx-text-fill: orange; -fx-font-weight: bold; -fx-font-size: 14;");
        } else {
            notificationLabel.setText("");
        }
    }

    public void onSceneReady() {
        try {
            String cssPath = getClass().getResource("/css/styles.css").toExternalForm();
            fieldGrid.getScene().getStylesheets().add(cssPath);
            System.out.println("Feuille de style chargée : " + cssPath);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de la feuille de style : " + e.getMessage());
        }
        checkAnimalStatus();
    }

}