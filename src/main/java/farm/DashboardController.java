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
    private void updateResourcesDisplay() {
        if (resourcesGrid == null) return;

        // Nettoyer la grille existante
        resourcesGrid.getChildren().clear();

        // Si la ferme possède des ressources, les afficher
        if (farm != null && farm.getResources() != null) {
            int row = 0;
            for (String resourceName : farm.getResources().keySet()) {
                int quantity = farm.getResources().get(resourceName);

                Label nameLabel = new Label(resourceName);
                Label quantityLabel = new Label(String.valueOf(quantity));

                resourcesGrid.add(nameLabel, 0, row);
                resourcesGrid.add(quantityLabel, 1, row);

                row++;
            }
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