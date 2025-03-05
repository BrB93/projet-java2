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
import javafx.scene.effect.Glow;

import java.util.Map;
import java.util.logging.Logger;

public class FieldController {
    private static final Logger LOGGER = Logger.getLogger(FieldController.class.getName());
    private static final int GRID_SIZE = 5;

    @FXML private GridPane fieldGrid;
    @FXML private VBox inventoryPanel;
    @FXML private Label selectedItemLabel;
    @FXML private Label inventoryLabel;
    @FXML private TableView<Map.Entry<String, Integer>> inventoryTableView;
    @FXML private TableColumn<Map.Entry<String, Integer>, String> itemColumn;
    @FXML private TableColumn<Map.Entry<String, Integer>, Integer> quantityColumn;

    private Farm farm;
    private String selectedItemType;
    private String selectedAction;

    @FXML
    private void initialize() {
        LOGGER.info("Initialisation du FieldController");
        setupFieldGrid();
        setupInventoryTable();
        setupGameLoop();
        updateInventoryDisplay();

        // Attendre que la scène soit disponible
        fieldGrid.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                loadStylesheet();
            }
        });
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
                new SimpleStringProperty(cellData.getValue().getKey()));

        quantityColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getValue()).asObject());

        // Ajout d'un gestionnaire de sélection
        inventoryTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedItemType = newVal.getKey();
                selectedAction = determineAction(selectedItemType);
                selectedItemLabel.setText("Sélectionné: " + selectedItemType);
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
                type.equalsIgnoreCase("cochon");
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

        // Utiliser la nouvelle méthode d'affichage
        updateCellWithStage(cellButton, cropType, crop.getDisplayText(),
                "crop-cell", crop.getStageStyleClass());

        updateInventoryDisplay();
        LOGGER.info("Culture " + cropType + " placée en " + row + "," + col);
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

    public void updateInventoryDisplay() {
        if (farm == null || farm.getInventory() == null) return;

        // Affichage du résumé dans le label
        inventoryLabel.setText("Stock: " + farm.getInventory().size() + " type(s) d'articles");

        // Mise à jour du tableau d'inventaire
        if (inventoryTableView != null) {
            inventoryTableView.getItems().clear();
            inventoryTableView.setItems(FXCollections.observableArrayList(farm.getInventory().entrySet()));
        }
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
            LOGGER.info("Mise à jour du terrain à " + System.currentTimeMillis());
            updateFieldDisplay();
        }));
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        gameLoop.play();
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
                            "baby-stage", "young-stage", "adult-stage");
                    // Ajouter la classe pour les cellules vides
                    button.getStyleClass().add("empty-cell");
                });
    }

    private void updateCellWithStage(Button cellButton, String itemType, String displayText,
                                     String baseStyleClass, String stageStyleClass) {
        cellButton.setText(displayText);

        // Supprimer tous les styles précédents
        cellButton.getStyleClass().removeIf(style ->
                style.equals("crop-cell") ||
                        style.equals("animal-cell") ||
                        style.equals("empty-cell") ||
                        style.contains("-stage") ||
                        style.equals("harvestable")); // Ajouter cette ligne

        // Ajouter les nouveaux styles
        cellButton.getStyleClass().add(baseStyleClass);
        if (stageStyleClass != null && !stageStyleClass.isEmpty()) {
            cellButton.getStyleClass().add(stageStyleClass);

            // Ajouter un style spécial pour les cultures prêtes à être récoltées
            if (stageStyleClass.equals("ready-stage") && baseStyleClass.equals("crop-cell")) {
                cellButton.getStyleClass().add("harvestable");
            }
        }

        // Ajouter une infobulle avec instruction de récolte si applicable
        String tooltipText = displayText;
        if (stageStyleClass.equals("ready-stage") && baseStyleClass.equals("crop-cell")) {
            tooltipText += " - Cliquez pour récolter";
        }
        Tooltip tooltip = new Tooltip(tooltipText);
        cellButton.setTooltip(tooltip);

        // Effet de survol
        cellButton.setOnMouseEntered(e -> cellButton.setEffect(new Glow(0.3)));
        cellButton.setOnMouseExited(e -> cellButton.setEffect(null));
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
    @FXML private void selectPig() { selectItem("mouton", "place", "Mouton"); }

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

    private void harvestCrop(Crop crop, Button cellButton) {
        String type = crop.getType();
        int harvestedAmount = getHarvestAmount(type);

        // Ajouter les produits récoltés à l'inventaire
        String harvestedResource = type; // Le nom du type est aussi le nom du produit récolté
        farm.addToInventory(harvestedResource, harvestedAmount);  // Correction ici

        // Retirer la culture du terrain
        farm.getPlantedCrops().remove(crop);

        // Effacer la cellule
        cellButton.setText("");
        cellButton.getStyleClass().removeAll("crop-cell", "seed-stage", "growing-stage", "ready-stage", "harvestable");
        cellButton.getStyleClass().add("empty-cell");

        // Mettre à jour l'affichage
        updateInventoryDisplay();

        LOGGER.info("Récolté " + harvestedAmount + " " + harvestedResource + " de " + type);
        selectedItemLabel.setText("Récolté: " + harvestedAmount + " " + harvestedResource);
    }

    private int getHarvestAmount(String cropType) {
        switch(cropType.toLowerCase()) {
            case "ble": return 3;
            case "mais": return 5;
            case "carotte": return 7;
            default: return 1;
        }
    }

}