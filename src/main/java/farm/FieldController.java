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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Glow;import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.Arrays;
import java.util.List;


import java.util.Map;
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
    private String selectedItemToSell;

    // Référence au MainController
    private MainController mainController;

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    @FXML
    private void initialize() {
        // Initialisation de la grille
        setupFieldGrid();

        // Configuration des colonnes
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        itemColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        // Configuration de l'inventaire
        setupInventoryTable();

        // Configuration de la boucle de jeu
        setupGameLoop();

        updateInventoryDisplay();
    }

    @FXML
    private void handleSellButtonClick() {
        if (selectedItemType == null || selectedItemType.isEmpty()) {
            selectedItemLabel.setText("Sélectionnez un article à vendre !");
            return;
        }

        selectedItemToSell = selectedItemType;
        showSellDialog();

        // Notifier le contrôleur principal de mettre à jour le tableau de bord
        if (mainController != null) {
            mainController.updateDashboard();
        }
    }

    private void showSellDialog() {
        int availableAmount = farm.getInventory().getOrDefault(selectedItemType, 0);
        if (availableAmount <= 0) {
            selectedItemLabel.setText("Stock insuffisant pour vendre " + selectedItemType);
            return;
        }

        // Dans une application réelle, vous pourriez créer une boîte de dialogue pour choisir la quantité
        // Ici, nous vendons simplement 1 unité pour simplifier
        int quantityToSell = 1;
        int pricePerUnit = getPricePerUnit(selectedItemType);

        farm.sellResource(selectedItemType, quantityToSell, pricePerUnit);
        updateInventoryDisplay();
        updateBalanceDisplay();

        String displayName = selectedItemType.replace("_recolte", "");
        selectedItemLabel.setText("Vendu: " + quantityToSell + " " + displayName +
                " pour " + (quantityToSell * pricePerUnit) + " €");
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

    private void updateBalanceDisplay() {
        balanceLabel.setText("Solde: " + farm.getMoney() + " €");
    }

    private void updateInventoryDisplay() {
        if (farm == null || inventoryTableView == null) {
            return;
        }

        Map<String, Integer> inventory = farm.getInventory();
        ObservableList<InventoryItem> items = FXCollections.observableArrayList();

        // Afficher les animaux
        for (String type : Arrays.asList("poule", "vache", "mouton")) {
            if (inventory.containsKey(type) && inventory.get(type) > 0) {
                items.add(new InventoryItem("Animaux", type,
                        inventory.get(type), getPriceForAnimal(type)));
            }
        }

        // Afficher les graines
        for (String type : Arrays.asList("ble", "mais", "carotte")) {
            if (inventory.containsKey(type) && inventory.get(type) > 0) {
                items.add(new InventoryItem("Graines", type,
                        inventory.get(type), getPriceForSeed(type)));
            }
        }

        // Afficher les récoltes
        for (String type : Arrays.asList("ble_recolte", "mais_recolte", "carotte_recolte")) {
            if (inventory.containsKey(type) && inventory.get(type) > 0) {
                String displayName = type.replace("_recolte", "");
                items.add(new InventoryItem("Récoltes", displayName,
                        inventory.get(type), getPriceForCrop(type)));
            }
        }

        // Afficher les productions animales
        for (String type : Arrays.asList("oeuf", "lait", "laine")) {
            if (inventory.containsKey(type) && inventory.get(type) > 0) {
                items.add(new InventoryItem("Productions", type,
                        inventory.get(type), getPriceForProduct(type)));
            }
        }

        inventoryTableView.setItems(items);

        // Mise à jour de l'affichage du solde
        if (balanceLabel != null) {
            balanceLabel.setText("Solde: " + farm.getMoney() + " €");        }
    }

    // Méthodes auxiliaires pour obtenir les prix
    private int getPriceForSeed(String type) {
        switch (type.toLowerCase()) {
            case "ble": return 5;
            case "mais": return 8;
            case "carotte": return 3;
            default: return 0;
        }
    }

    private int getPriceForCrop(String type) {
        switch (type.toLowerCase()) {
            case "ble_recolte": return 15;
            case "mais_recolte": return 20;
            case "carotte_recolte": return 10;
            default: return 0;
        }
    }

    private int getPriceForAnimal(String type) {
        switch (type.toLowerCase()) {
            case "poule":
                return 50;
            case "vache":
                return 300;
            case "mouton":
                return 200;
            default:
                return 0;
        }
    }

    private int getPriceForProduct(String type) {
        switch (type.toLowerCase()) {
            case "oeuf": return 5;
            case "lait": return 12;
            case "laine": return 15;
            default: return 0;
        }
    }
    // Cette méthode doit être appelée dans votre gameLoop
    private void updateAnimals() {
        if (farm == null) return;

        List<Animal> animals = farm.getAnimals();
        if (animals == null || animals.isEmpty()) return;

        boolean productionMade = false;

        for (Animal animal : animals) {
            if (animal.canProduce()) {
                animal.produceResource(farm);
                productionMade = true;
            }
        }

        // Ne mettre à jour l'affichage que si une production a eu lieu
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

        // Configuration des colonnes du tableau d'inventaire
        itemColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        quantityColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());

        // Ajout d'un gestionnaire de sélection
        inventoryTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // Utiliser le nom d'origine pour les récoltes
                String itemName = newVal.getName();
                if (newVal.getCategory().equals("Récoltes")) {
                    itemName = itemName + "_recolte";
                }

                selectedItemType = itemName;
                selectedAction = determineAction(selectedItemType);
                selectedItemLabel.setText("Sélectionné: " + newVal.getName() + " (" +
                        newVal.getQuantity() + " unités à " + newVal.getValue() + "€)");
                LOGGER.info("Élément sélectionné: " + selectedItemType);
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
                type.equalsIgnoreCase("mouton");  // Remplacé "cochon" par "mouton"
    }

    private void handleCellClick(int row, int col, Button cellButton) {
        if (farm == null) {
            return;
        }

        String position = row + "," + col;

        // Vérifier si une culture est prête à être récoltée à cette position
        Crop cropToHarvest = farm.getPlantedCrops().stream()
                .filter(crop -> crop.getPosition().equals(position) && crop.isReadyToHarvest())
                .findFirst()
                .orElse(null);

        if (cropToHarvest != null) {
            harvestCrop(cropToHarvest, cellButton);
            return;
        }

        // Si aucune culture prête à récolter, continuer avec le comportement existant
        if (selectedItemType == null || selectedItemType.isEmpty()) {
            return;
        }

        // Vérification si la case est déjà occupée
        if (!cellButton.getText().isEmpty()) {
            selectedItemLabel.setText("Case déjà occupée !");
            LOGGER.warning("Tentative de placement sur case occupée en " + row + "," + col);
            return;
        }

        if ("plant".equals(selectedAction) && farm.getInventory().containsKey(selectedItemType)) {
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
        LOGGER.info("Culture " + cropType + " placée en " + row + "," + col);
    }

    private void harvestCrop(Crop cropToHarvest, Button cellButton) {
        // Ajouter le produit récolté à l'inventaire
        farm.addToInventory(cropToHarvest.getType(), cropToHarvest.getYield());

        // Retirer la culture de la liste
        farm.getPlantedCrops().removeIf(crop -> crop.getPosition().equals(cropToHarvest.getPosition()));

        // Réinitialiser la cellule
        cellButton.setText("");
        cellButton.getStyleClass().removeAll("crop-cell", "seed-stage", "growing-stage", "ready-stage", "harvestable");
        cellButton.getStyleClass().add("empty-cell");

        // Mise à jour de l'affichage
        updateInventoryDisplay();
        selectedItemLabel.setText("Récolté: " + cropToHarvest.getType() + " x" + cropToHarvest.getYield());
        LOGGER.info("Culture " + cropToHarvest.getType() + " récoltée avec " + cropToHarvest.getYield() + " unités");
    }

    private void placeAnimal(String animalType, int row, int col, Button cellButton) {
        Animal animal = new Animal(animalType);
        animal.setPosition(row + "," + col);
        farm.getInventory().put(animalType, farm.getInventory().getOrDefault(animalType, 1) - 1);
        farm.getAnimals().add(animal);

        // Utiliser la nouvelle méthode d'affichage
        updateCellWithStage(cellButton, animalType, animal.getDisplayText(),
                "animal-cell", animal.getStageStyleClass());

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

    public void loadStylesheet() {
        try {
            String cssPath = getClass().getResource("/css/styles.css").toExternalForm();
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
            updateAnimals(); // Ajouter cette ligne pour mettre à jour les animaux
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

        // Nettoyer d'abord le terrain
        clearField();

        // Mettre à jour les cultures
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

    private Animal createAnimal(String type) {
        switch (type.toLowerCase()) {
            case "vache":
                return new Animal("Vache");
            case "poule":
                return new Animal("Poule");
            case "mouton":
                return new Animal("Mouton");
            default:
                return null;
        }
    }

    private void updateCellWithStage(Button cellButton, String type, String text,
                                     String baseStyle, String stageStyle) {
        cellButton.setText(text);
        cellButton.getStyleClass().removeAll("empty-cell", "crop-cell", "animal-cell");
        cellButton.getStyleClass().add(baseStyle);

        if (stageStyle != null && !stageStyle.isEmpty()) {
            cellButton.getStyleClass().add(stageStyle);
        }

        Tooltip tooltip = new Tooltip(type + "\n" + text);
        Tooltip.install(cellButton, tooltip);
    }

    private void checkAndHarvestCrop(int row, int col, Button cellButton) {
        String position = row + "," + col;

        // Vérifier les cultures
        for (int i = 0; i < farm.getPlantedCrops().size(); i++) {
            Crop crop = farm.getPlantedCrops().get(i);
            if (position.equals(crop.getPosition()) && crop.isReadyToHarvest()) {
                // Récolter la culture
                farm.harvestCrop(i);

                // Mettre à jour l'affichage
                cellButton.setText("");
                cellButton.getStyleClass().removeAll("crop-cell", "stage-1", "stage-2", "stage-3");
                cellButton.getStyleClass().add("empty-cell");
                Tooltip.uninstall(cellButton, null);

                updateInventoryDisplay();
                LOGGER.info("Culture récoltée en " + position);
                return;
            }
        }
    }

    private void updateGrowthStages() {
        if (farm == null || farm.getPlantedCrops() == null) return;

        for (Crop crop : farm.getPlantedCrops()) {
            crop.updateGrowthStage(); // Cette méthode retourne void, pas boolean
            // Mettre à jour l'affichage de la cellule
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

            // Trouver le bouton dans la grille
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
            LOGGER.warning("Format de position invalide: " + position);
        }
    }

    @FXML
    private void selectWheat() {
        selectedItemType = "ble";
        selectedAction = "plant";
        selectedItemLabel.setText("Sélectionné: Blé");
        LOGGER.info("Blé sélectionné pour plantation");
    }

    @FXML
    private void selectCorn() {
        selectedItemType = "mais";
        selectedAction = "plant";
        selectedItemLabel.setText("Sélectionné: Maïs");
        LOGGER.info("Maïs sélectionné pour plantation");
    }

    @FXML
    private void selectCarrot() {
        selectedItemType = "carotte";
        selectedAction = "plant";
        selectedItemLabel.setText("Sélectionné: Carotte");
        LOGGER.info("Carotte sélectionnée pour plantation");
    }

    @FXML
    private void selectChicken() {
        selectedItemType = "poule";
        selectedAction = "place";
        selectedItemLabel.setText("Sélectionné: Poule");
        LOGGER.info("Poule sélectionnée pour placement");
    }

    @FXML
    private void selectCow() {
        selectedItemType = "vache";
        selectedAction = "place";
        selectedItemLabel.setText("Sélectionné: Vache");
        LOGGER.info("Vache sélectionnée pour placement");
    }

    @FXML
    private void selectSheep() {
        selectedItemType = "mouton";
        selectedAction = "place";
        selectedItemLabel.setText("Sélectionné: Mouton");
        LOGGER.info("Mouton sélectionné pour placement");
    }

    private long lastUpdateTime = System.currentTimeMillis();

    public void resetTimer() {
        // Réinitialiser le compteur de temps pour la mise à jour des animaux et cultures
        lastUpdateTime = System.currentTimeMillis();
    }

}