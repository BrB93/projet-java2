package farm;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import java.util.logging.Logger;
import java.util.logging.Level;

public class MainController {
    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

    @FXML
    private BorderPane root;

    @FXML
    private VBox centerVBox;

    @FXML
    private Button startButton;

    @FXML
    private DashboardController dashboardPaneController;

    @FXML
    private StoreController storePaneController;

    private Farm farm;
    private Timeline gameTimer;
    private int elapsedTimeInSeconds = 0;
    private Label timerLabel;
    private boolean gameStarted = false;
    private MainController mainController;

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    @FXML
    private void initialize() {
        try {
            // Création de l'écran de démarrage avec boutons
            createStartScreen();
            LOGGER.info("MainController initialisé avec succès");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'initialisation du MainController", e);
        }
    }

    /**
     * Crée l'écran de démarrage avec les boutons Nouvelle partie et Charger
     */
    private void createStartScreen() {
        VBox startBox = new VBox(20);
        startBox.setAlignment(javafx.geometry.Pos.CENTER);

        Label titleLabel = new Label("Ma Ferme");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        Button newGameButton = new Button("Nouvelle Partie");
        newGameButton.setPrefWidth(200);
        newGameButton.setOnAction(e -> handleNewGame());

        Button loadButton = new Button("Charger une partie");
        loadButton.setPrefWidth(200);
        loadButton.setOnAction(e -> handleLoad());

        startBox.getChildren().addAll(titleLabel, newGameButton, loadButton);

        // Remplacer le contenu central par notre écran de démarrage
        root.setCenter(startBox);
    }

    /**
     * Gère la création d'une nouvelle partie
     */
    @FXML
    public void handleNewGame() {
        try {
            // Initialisation de la ferme
            farm = new Farm("Ma ferme", 5);

            // Mise à jour des contrôleurs
            updateAllControllers();

            // Démarrer le jeu
            startGame();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création d'une nouvelle partie", e);
        }
    }

    @FXML
    private FieldController fieldPaneController;

    /**
     * Définit l'objet Farm et met à jour l'interface utilisateur
     * @param farm L'objet Farm à utiliser dans l'application
     */
    public void setFarm(Farm farm) {
        if (farm == null) {
            LOGGER.warning("Tentative de définir farm à null");
            return;
        }

        this.farm = farm;
        updateAllControllers();
    }

    /**
     * Met à jour tous les contrôleurs avec l'instance farm actuelle
     */
    private void updateAllControllers() {
        try {
            // Mise à jour du tableau de bord
            if (dashboardPaneController != null) {
                dashboardPaneController.updateFarmData(farm);
                LOGGER.fine("DashboardController mis à jour");
            } else {
                LOGGER.warning("dashboardPaneController est null, impossible de mettre à jour les données");
            }

            // Mise à jour du magasin
            if (storePaneController != null) {
                storePaneController.setFarm(farm);
                LOGGER.fine("StoreController mis à jour");
            } else {
                LOGGER.warning("storePaneController est null, impossible de mettre à jour les données");
            }

            // Mise à jour du terrain
            if (fieldPaneController != null) {
                fieldPaneController.updateField(farm);
                LOGGER.fine("FieldController mis à jour");
            } else {
                LOGGER.warning("fieldPaneController est null");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour des contrôleurs", e);
        }
    }

    /**
     * Démarre le jeu avec l'interface complète
     */
    private void startGame() {
        try {
            if (gameStarted) {
                LOGGER.info("Le jeu est déjà démarré");
                return;
            }

            // Remplacer les boutons par l'interface de jeu
            VBox gameInterface = createGameInterface();
            root.setCenter(gameInterface);

            // Démarrer le chronomètre
            startGameTimer();

            gameStarted = true;
            LOGGER.info("Jeu démarré avec succès");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du démarrage du jeu", e);
        }
    }

    private VBox createGameInterface() {
        VBox gameBox = new VBox(10);
        gameBox.setAlignment(javafx.geometry.Pos.CENTER);

        // Créer le chronomètre
        timerLabel = new Label("00:00:00");
        timerLabel.setId("timerLabel");
        timerLabel.getStyleClass().add("timer-label");

        // Ajouter les composants du jeu
        HBox statusBar = new HBox(10);
        statusBar.setAlignment(javafx.geometry.Pos.CENTER);
        statusBar.getChildren().addAll(new Label("Temps:"), timerLabel);

        // Ajouter la zone de jeu
        VBox gameArea = new VBox(10);
        gameArea.setAlignment(javafx.geometry.Pos.CENTER);

        // Ajout de composants du jeu
        Label gameTitle = new Label("Votre ferme");
        gameTitle.getStyleClass().add("game-title");
        gameArea.getChildren().addAll(gameTitle);

        // Boutons de contrôle du jeu
        HBox controlsBar = new HBox(10);
        controlsBar.setAlignment(javafx.geometry.Pos.CENTER);
        Button pauseButton = new Button("Pause");
        pauseButton.setOnAction(e -> togglePause());
        Button saveButton = new Button("Sauvegarder");
        saveButton.setOnAction(e -> handleSave());
        controlsBar.getChildren().addAll(pauseButton, saveButton);

        // Ajouter tous les composants
        gameBox.getChildren().addAll(statusBar, gameArea, controlsBar);

        return gameBox;
    }

    private void startGameTimer() {
        // Arrêter le timer existant s'il y en a un
        if (gameTimer != null) {
            gameTimer.stop();
        }

        gameTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            elapsedTimeInSeconds++;
            updateTimerDisplay();
            updateGameElements();
        }));
        gameTimer.setCycleCount(Animation.INDEFINITE);
        gameTimer.play();

        LOGGER.fine("Timer de jeu démarré");
    }



    private void togglePause() {
        if (gameTimer == null) {
            LOGGER.warning("Tentative de mettre en pause un timer null");
            return;
        }

        if (gameTimer.getStatus() == Animation.Status.RUNNING) {
            gameTimer.pause();
            LOGGER.info("Jeu mis en pause");
        } else {
            gameTimer.play();
            LOGGER.info("Jeu repris");
        }
    }

    private void updateTimerDisplay() {
        if (timerLabel == null) {
            return;
        }

        int hours = elapsedTimeInSeconds / 3600;
        int minutes = (elapsedTimeInSeconds % 3600) / 60;
        int seconds = elapsedTimeInSeconds % 60;

        timerLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    private void updateGameElements() {
        try {
            // Mettre à jour l'état des cultures et animaux
            if (farm == null) {
                LOGGER.warning("Tentative de mise à jour avec farm null");
                return;
            }

            // Mettre à jour les cultures
            for (Crop crop : farm.getPlantedCrops()) {
                if (crop != null) {
                    crop.updateGrowthStage();
                }
            }

            // Mettre à jour les animaux
            if (farm.getAnimals() != null) {
                for (Animal animal : farm.getAnimals()) {
                    if (animal != null) {
                        animal.updateProductionCycle(farm);
                    }
                }
            }

            // Mise à jour de tous les contrôleurs
            updateAllControllers();

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur lors de la mise à jour des éléments du jeu", e);
        }
    }

    @FXML
    private void handleSave() {
        try {
            if (farm == null) {
                LOGGER.warning("Impossible de sauvegarder: farm est null");
                return;
            }

            farm.saveFarmState();
            LOGGER.info("Sauvegarde effectuée avec succès");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la sauvegarde", e);
        }
    }

    @FXML
    private void handleLoad() {
        try {
            Farm loadedFarm = Farm.loadFarmState();
            if (loadedFarm == null) {
                LOGGER.warning("Aucune donnée n'a pu être chargée");
                return;
            }

            // Affecter la nouvelle ferme
            this.farm = loadedFarm;

            // Mettre à jour tous les contrôleurs
            updateAllControllers();

            // Si le jeu n'est pas démarré, démarrer l'interface
            if (!gameStarted) {
                startGame();
            }

            LOGGER.info("Chargement effectué avec succès");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement", e);
        }
    }

    @FXML
    private void handleSettings() {
        try {
            // Logique pour afficher les paramètres
            LOGGER.info("Affichage des paramètres");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'affichage des paramètres", e);
        }
    }

    /**
     * Méthode appelée lors de la fermeture de l'application
     * pour nettoyer les ressources
     */
    public void shutdown() {
        try {
            if (gameTimer != null) {
                gameTimer.stop();
            }

            // Sauvegarder l'état actuel si nécessaire
            if (farm != null && gameStarted) {
                handleSave();
            }

            LOGGER.info("Application fermée proprement");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la fermeture de l'application", e);
        }
    }

    public void updateDashboard() {
        // Met à jour le tableau de bord avec les données actuelles de la ferme
        if (dashboardPaneController != null && farm != null) {
            dashboardPaneController.refresh();
        }
    }

}