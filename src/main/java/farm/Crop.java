package farm;

import java.io.Serializable;
import java.util.logging.Logger;

public class Crop implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(Crop.class.getName());

    // Constantes pour les stades de croissance
    public static final int SEED_STAGE = 0;
    public static final int GROWING_STAGE = 1;
    public static final int READY_STAGE = 2;

    // Propriétés de base
    private String type;
    private int price;
    private int growthTime; // temps total de croissance en secondes
    private int yield; // quantité produite à la récolte
    private String position;

    // Propriétés pour les stades de croissance
    private int growthStage = SEED_STAGE;
    private long plantedTime;
    private long timeToNextStage;

    /**
     * Constructeur simple avec juste le type
     */
    public Crop(String type) {
        this.type = type.toLowerCase();
        setDefaultValues(this.type);
        this.plantedTime = System.currentTimeMillis();
        calculateTimeToNextStage();
    }

    /**
     * Constructeur complet avec tous les paramètres
     */
    public Crop(String type, int price, int growthTime, int yield) {
        this.type = type.toLowerCase();
        this.price = price;
        this.growthTime = growthTime;
        this.yield = yield;
        this.plantedTime = System.currentTimeMillis();
        calculateTimeToNextStage();
    }

    /**
     * Définit les valeurs par défaut selon le type de culture
     */
    private void setDefaultValues(String cropType) {
        switch(cropType) {
            case "ble":
                this.price = 10;
                this.yield = 20;
                this.growthTime = 10; // en secondes
                break;
            case "mais":
                this.price = 15;
                this.yield = 25;
                this.growthTime = 10;
                break;
            case "carotte":
                this.price = 5;
                this.yield = 15;
                this.growthTime = 10;
                break;
            default:
                LOGGER.warning("Type de culture inconnu: " + cropType + ", utilisation des valeurs par défaut");
                this.price = 10;
                this.yield = 15;
                this.growthTime = 45;
                break;
        }
    }

    /**
     * Calcule le temps nécessaire pour passer au prochain stade
     */
    private void calculateTimeToNextStage() {
        // Diviser le temps total de croissance en 2 périodes
        timeToNextStage = growthTime / 2 * 1000; // Convertir en millisecondes
    }

    /**
     * Met à jour le stade de croissance en fonction du temps écoulé
     */
    public void updateGrowthStage() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - plantedTime;

        if (growthStage == SEED_STAGE && elapsedTime >= timeToNextStage) {
            growthStage = GROWING_STAGE;
            plantedTime = currentTime; // Réinitialiser pour le prochain stade
        } else if (growthStage == GROWING_STAGE && elapsedTime >= timeToNextStage) {
            growthStage = READY_STAGE;
        }
    }

    /**
     * Retourne le temps restant avant le prochain stade en secondes
     */
    public int getRemainingGrowthTime() {
        if (growthStage == READY_STAGE) {
            return 0;
        }

        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - plantedTime;
        int remaining = (int)(timeToNextStage - elapsedTime) / 1000;
        return Math.max(0, remaining);
    }

    /**
     * Indique si la culture est prête à être récoltée
     */
    public boolean isReadyToHarvest() {
        return growthStage >= READY_STAGE;
    }

    /**
     * Retourne le texte à afficher pour cette culture selon son stade
     */
    public String getDisplayText() {
        switch(growthStage) {
            case SEED_STAGE:
                return type + " (graine)";
            case GROWING_STAGE:
                return type + " (pousse)";
            case READY_STAGE:
                return type + " (prêt)";
            default:
                return type;
        }
    }

    /**
     * Retourne la classe de style CSS à utiliser selon le stade
     */
    public String getStageStyleClass() {
        switch(growthStage) {
            case SEED_STAGE:
                return "seed-stage";
            case GROWING_STAGE:
                return "growing-stage";
            case READY_STAGE:
                return "ready-stage";
            default:
                return "";
        }
    }

    // Getters et setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type.toLowerCase();
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getGrowthTime() {
        return growthTime;
    }

    public void setGrowthTime(int growthTime) {
        this.growthTime = growthTime;
    }

    public int getYield() {
        return yield;
    }

    public void setYield(int yield) {
        this.yield = yield;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getGrowthStage() {
        return growthStage;
    }

    @Override
    public String toString() {
        return type + " (position: " + (position != null ? position : "non plantée") +
                ", stade: " + (growthStage == SEED_STAGE ? "graine" :
                growthStage == GROWING_STAGE ? "pousse" : "prêt") + ")";
    }


}