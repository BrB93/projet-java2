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
import java.util.Map;
import java.util.Arrays;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import javafx.scene.control.Alert;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;

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

            // Configurez la callback pour quand la scène est prête
            root.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null && fieldPaneController != null) {
                    fieldPaneController.onSceneReady();
                }
            });

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'initialisation du MainController", e);
        }
    }

    private void executeWithErrorHandling(Runnable action, String errorMessage) {
        try {
            action.run();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, errorMessage, e);
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
        executeWithErrorHandling(() -> {
            farm = new Farm("Ma ferme", 5);
            updateAllControllers();
            startGame();
        }, "Erreur lors de la création d'une nouvelle partie");
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private FieldController fieldPaneController;

    /**
     * Définit l'objet Farm et met à jour l'interface utilisateur
     *
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
        if (farm == null) {
            LOGGER.warning("Farm est null, impossible de mettre à jour les contrôleurs");
            return;
        }

        if (dashboardPaneController != null) dashboardPaneController.updateFarmData(farm);
        else LOGGER.warning("dashboardPaneController est null");

        if (storePaneController != null) storePaneController.setFarm(farm);
        else LOGGER.warning("storePaneController est null");

        if (fieldPaneController != null) fieldPaneController.updateField(farm);
        else LOGGER.warning("fieldPaneController est null");
    }

    /**
     * Démarre le jeu avec l'interface complète
     */
    private void startGame() {
        if (gameStarted) {
            LOGGER.info("Le jeu est déjà démarré");
            return;
        }

        // Remplacer les boutons par l'interface de jeu
        root.setCenter(createGameInterface());

        // Démarrer le chronomètre
        startGameTimer();

        gameStarted = true;
        LOGGER.info("Jeu démarré avec succès");
    }

    private VBox createGameInterface() {
        VBox gameBox = new VBox(10);
        gameBox.setAlignment(javafx.geometry.Pos.CENTER);

        // Chronomètre
        timerLabel = new Label("00:00:00");
        timerLabel.setId("timerLabel");
        timerLabel.getStyleClass().add("timer-label");

        // Barre d'état avec timer
        HBox statusBar = new HBox(10);
        statusBar.setAlignment(javafx.geometry.Pos.CENTER);
        statusBar.getChildren().addAll(new Label("Temps:"), timerLabel);

        // Zone de jeu
        Label gameTitle = new Label("Votre ferme");
        gameTitle.getStyleClass().add("game-title");
        VBox gameArea = new VBox(10);
        gameArea.setAlignment(javafx.geometry.Pos.CENTER);
        gameArea.getChildren().add(gameTitle);

        // Contrôles
        Button pauseButton = new Button("Pause");
        pauseButton.setOnAction(e -> togglePause());
        Button saveButton = new Button("Sauvegarder");
        saveButton.setOnAction(e -> handleSave());
        Button financeButton = new Button("Finances");
        financeButton.setOnAction(e -> openFinanceView());
        HBox controlsBar = new HBox(10);
        controlsBar.setAlignment(javafx.geometry.Pos.CENTER);
        controlsBar.getChildren().addAll(pauseButton, saveButton, financeButton);

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
        if (farm == null) {
            LOGGER.warning("Tentative de mise à jour avec farm null");
            return;
        }

        // Mettre à jour les cultures
        farm.getPlantedCrops().forEach(crop -> {
            if (crop != null) crop.updateGrowthStage();
        });

        // Mettre à jour les animaux
        if (farm.getAnimals() != null) {
            farm.getAnimals().forEach(animal -> {
                if (animal != null) animal.updateProductionCycle(farm);
            });
        }

        // Vérifier si les animaux ont faim
        if (farm.getAnimals() != null) {
            farm.getAnimals().removeIf(animal -> {
                if (animal != null && animal.isStarving()) {
                    LOGGER.info(animal.getType() + " à " + animal.getPosition() + " est mort de faim");
                    return true;
                }
                return false;
            });
        }

        // Mise à jour de tous les contrôleurs
        updateAllControllers();
    }

    @FXML
    private void handleSave() {
        if (farm == null) {
            LOGGER.warning("Impossible de sauvegarder: farm est null");
            return;
        }

        // Mettre à jour le temps écoulé dans la ferme
        farm.setElapsedTime(elapsedTimeInSeconds * 1000L);

        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Files.write(Paths.get("save.json"), gson.toJson(farm).getBytes());
            showAlert("Sauvegarde", "Jeu sauvegardé avec succès !", Alert.AlertType.INFORMATION);
            LOGGER.info("Sauvegarde effectuée avec succès");
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de sauvegarder: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleLoad() {
        try {
            String json = new String(Files.readAllBytes(Paths.get("save.json")));
            farm = new Gson().fromJson(json, Farm.class);

            if (farm != null) {
                elapsedTimeInSeconds = (int) (farm.getElapsedTime() / 1000);
                updateTimerDisplay();
                if (!gameStarted) startGame();
                updateAllControllers();
                fieldPaneController.resetTimer();
                showAlert("Chargement", "Jeu chargé avec succès !", Alert.AlertType.INFORMATION);
                LOGGER.info("Chargement effectué avec succès");
            }
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger: " + e.getMessage(), Alert.AlertType.ERROR);
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
        executeWithErrorHandling(() -> {
            if (gameTimer != null) gameTimer.stop();
            if (farm != null && gameStarted) handleSave();
            LOGGER.info("Application fermée proprement");
        }, "Erreur lors de la fermeture de l'application");
    }

    public void updateDashboard() {
        if (dashboardPaneController != null) {
            dashboardPaneController.updateResources(farm);
        }
    }

    @FXML
    private void openFinanceView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/financeController.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur et lui passer la ferme
            FinanceController controller = loader.getController();
            controller.setFarm(farm);

            Stage stage = new Stage();
            stage.setTitle("Gestion des Finances");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}