package farm;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.control.ProgressBar;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Map;
import java.util.Arrays;

public class DashboardController {
    private static final Logger LOGGER = Logger.getLogger(DashboardController.class.getName());
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.FRANCE);

    private Farm farm;

    @FXML
    private Label moneyLabel;

    @FXML
    private Label farmSizeLabel;

    @FXML
    private Label cropsCountLabel;

    @FXML
    private Label animalsCountLabel;

    @FXML
    private VBox dashboardContainer;

    @FXML
    private ProgressBar farmProgressBar;

    @FXML
    private Label farmLevelLabel;

    @FXML
    private GridPane resourcesGrid;

    @FXML
    private void initialize() {
        try {
            LOGGER.info("Initialisation du DashboardController");
            resetLabels();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'initialisation du DashboardController", e);
        }
    }

    /**
     * Définit la ferme à afficher et met à jour l'interface
     * @param farm L'objet Farm à afficher
     */
    public void setFarm(Farm farm) {
        if (farm == null) {
            LOGGER.warning("Tentative de définir farm à null");
            return;
        }

        this.farm = farm;
        refresh();
        LOGGER.fine("Ferme définie et tableau de bord mis à jour");
    }

    /**
     * Méthode alternative pour la compatibilité avec MainController
     * @param farm L'objet Farm à afficher
     */
    public void updateFarmData(Farm farm) {
        setFarm(farm);
    }

    /**
     * Rafraîchit toutes les données affichées dans le tableau de bord
     */
    private void updateResourcesDisplay() {
        if (farm != null) {
            updateResources(farm);
        }
    }

    public void refresh() {
        if (farm == null) {
            LOGGER.warning("Tentative de rafraîchir avec farm null");
            resetLabels();
            return;
        }

        try {
            Platform.runLater(() -> {
                try {
                    // Mise à jour de l'argent avec formattage monétaire
                    moneyLabel.setText(currencyFormat.format(farm.getMoney()));

                    // Mise à jour de la taille de la ferme
                    if (farmSizeLabel != null) {
                        farmSizeLabel.setText("Taille : " + farm.getSize() + " hectares");
                    }

                    // Mise à jour du nombre de cultures
                    if (cropsCountLabel != null) {
                        int cropsCount = farm.getPlantedCrops() != null ? farm.getPlantedCrops().size() : 0;
                        cropsCountLabel.setText("Cultures : " + cropsCount);
                    }

                    // Mise à jour du nombre d'animaux
                    if (animalsCountLabel != null) {
                        int animalsCount = farm.getAnimals() != null ? farm.getAnimals().size() : 0;
                        animalsCountLabel.setText("Animaux : " + animalsCount);
                    }

                    // Mise à jour de la progression de la ferme
                    updateFarmProgress();

                    // Mise à jour des ressources
                    updateResourcesDisplay();

                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Erreur lors du rafraîchissement de l'interface", e);
                }
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la planification du rafraîchissement", e);
        }
    }

    /**
     * Met à jour l'affichage de la progression de la ferme
     */
    private void updateFarmProgress() {
        if (farmProgressBar == null || farmLevelLabel == null) return;

        // Calcul du niveau de la ferme basé sur l'argent
        int farmLevel = calculateFarmLevel();

        // Progression vers le niveau suivant (0.0 à 1.0)
        double progress = calculateLevelProgress();

        // Mise à jour des composants d'interface
        farmProgressBar.setProgress(progress);
        farmLevelLabel.setText("Niveau : " + farmLevel);
    }

    /**
     * Met à jour l'affichage des ressources dans la grille
     */
    public void updateResources(Farm farm) {
        if (farm == null || resourcesGrid == null) return;

        // Vider la grille actuelle
        resourcesGrid.getChildren().clear();

        Map<String, Integer> inventory = farm.getInventory();
        if (inventory == null) return;

        int row = 0;

        // Afficher les productions animales
        for (String type : Arrays.asList("oeuf", "lait", "laine")) {
            if (inventory.containsKey(type) && inventory.get(type) > 0) {
                Label nameLabel = new Label(type + ":");
                nameLabel.getStyleClass().add("resource-name");
                Label quantityLabel = new Label(inventory.get(type).toString());
                quantityLabel.getStyleClass().add("resource-quantity");
                resourcesGrid.add(nameLabel, 0, row);
                resourcesGrid.add(quantityLabel, 1, row);
                row++;
            }
        }

        // Afficher les récoltes
        for (String type : Arrays.asList("ble_recolte", "mais_recolte", "carotte_recolte")) {
            if (inventory.containsKey(type) && inventory.get(type) > 0) {
                String displayName = type.replace("_recolte", "");
                Label nameLabel = new Label(displayName + ":");
                nameLabel.getStyleClass().add("resource-name");
                Label quantityLabel = new Label(inventory.get(type).toString());
                quantityLabel.getStyleClass().add("resource-quantity");
                resourcesGrid.add(nameLabel, 0, row);
                resourcesGrid.add(quantityLabel, 1, row);
                row++;
            }
        }

        // Ajouter une ligne indiquant l'absence de ressources si nécessaire
        if (row == 0) {
            Label emptyLabel = new Label("Aucune ressource disponible");
            emptyLabel.getStyleClass().add("empty-resource");
            resourcesGrid.add(emptyLabel, 0, 0, 2, 1);
        }
    }

    /**
     * Calcule le niveau actuel de la ferme
     * @return niveau de la ferme
     */
    private int calculateFarmLevel() {
        if (farm == null) return 1;

        // Formule simple: niveau = 1 + (argent / 1000)
        return 1 + (int)(farm.getMoney() / 1000);
    }

    /**
     * Calcule la progression vers le prochain niveau
     * @return progression de 0.0 à 1.0
     */
    private double calculateLevelProgress() {
        if (farm == null) return 0.0;

        // Argent actuel modulo 1000, divisé par 1000
        return (farm.getMoney() % 1000) / 1000.0;
    }

    /**
     * Réinitialise tous les labels à des valeurs par défaut
     */
    private void resetLabels() {
        Platform.runLater(() -> {
            if (moneyLabel != null) moneyLabel.setText(currencyFormat.format(0));
            if (farmSizeLabel != null) farmSizeLabel.setText("Taille : 0 hectares");
            if (cropsCountLabel != null) cropsCountLabel.setText("Cultures : 0");
            if (animalsCountLabel != null) animalsCountLabel.setText("Animaux : 0");
            if (farmProgressBar != null) farmProgressBar.setProgress(0);
            if (farmLevelLabel != null) farmLevelLabel.setText("Niveau : 1");
            if (resourcesGrid != null) resourcesGrid.getChildren().clear();
        });
    }

}