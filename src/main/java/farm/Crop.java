package farm;

import java.io.Serializable;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Crop implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(Crop.class.getName());

    // Propriétés de base
    private String name;
    private String type;
    private double buyPrice;
    private double sellPrice;
    private int growthTime; // temps en secondes
    private int growthStage; // 0: semé, 1: en croissance, 2: mûr
    private long plantTime;
    private String position;
    private boolean isHarvestable = false;
    private int yield; // quantité récoltée

    /**
     * Constructeur complet
     */
    public Crop(String name, double buyPrice, double sellPrice, int growthTime, int yield) {
        this.name = name;
        this.type = name.toLowerCase();
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.growthTime = growthTime;
        this.growthStage = 0;
        this.plantTime = System.currentTimeMillis();
        this.position = null;
        this.yield = yield;
    }

    /**
     * Constructeur simple avec juste le type
     */
    public Crop(String type) {
        this.type = type.toLowerCase();
        this.name = type;
        setDefaultValues(type);
        this.plantTime = System.currentTimeMillis();
    }

    /**
     * Définit les valeurs par défaut selon le type de culture
     */
    private void setDefaultValues(String cropType) {
        switch (cropType.toLowerCase()) {
            case "ble":
                this.buyPrice = 5.0;
                this.sellPrice = 10.0;
                this.growthTime = 30; // 30 secondes
                this.yield = 3;
                break;
            case "mais":
                this.buyPrice = 8.0;
                this.sellPrice = 15.0;
                this.growthTime = 45; // 45 secondes
                this.yield = 5;
                break;
            case "carotte":
                this.buyPrice = 3.0;
                this.sellPrice = 7.0;
                this.growthTime = 20; // 20 secondes
                this.yield = 2;
                break;
            default:
                LOGGER.warning("Type de culture inconnu: " + cropType + ", utilisation des valeurs par défaut");
                this.buyPrice = 5.0;
                this.sellPrice = 8.0;
                this.growthTime = 25; // 25 secondes
                this.yield = 1;
                break;
        }
    }

    /**
     * Met à jour l'étape de croissance selon le temps écoulé
     */
    public void updateGrowthStage() {
        try {
            long currentTime = System.currentTimeMillis();
            long elapsedSeconds = (currentTime - plantTime) / 1000;

            if (elapsedSeconds >= growthTime && growthStage < 2) {
                growthStage = 2; // mûr
                isHarvestable = true;
                LOGGER.fine(name + " à la position " + position + " est maintenant mûr");
            } else if (elapsedSeconds >= growthTime / 2 && growthStage < 1) {
                growthStage = 1; // en croissance
                LOGGER.fine(name + " à la position " + position + " est en croissance");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour de l'étape de croissance", e);
        }
    }

    /**
     * Vérifie si la culture est prête à être récoltée
     * @return true si prête à récolter, false sinon
     */
    public boolean isReadyToHarvest() {
        updateGrowthStage();
        return growthStage == 2 && isHarvestable;
    }

    /**
     * Récolte la culture et retourne la quantité récoltée
     * @return quantité récoltée, 0 si pas récoltable
     */
    public int harvest() {
        if (isReadyToHarvest()) {
            isHarvestable = false;
            LOGGER.info("Récolte de " + name + " à la position " + position + ": " + yield + " unités");
            return yield;
        }
        return 0;
    }

    /**
     * Calcule le temps restant avant que la culture soit prête
     * @return temps en secondes avant la récolte
     */
    public int getRemainingGrowthTime() {
        long currentTime = System.currentTimeMillis();
        long elapsedSeconds = (currentTime - plantTime) / 1000;
        int remaining = growthTime - (int)elapsedSeconds;
        return Math.max(0, remaining);
    }

    /**
     * Renvoie le pourcentage de croissance
     * @return pourcentage entre 0 et 100
     */
    public int getGrowthPercentage() {
        long currentTime = System.currentTimeMillis();
        long elapsedSeconds = (currentTime - plantTime) / 1000;
        return (int) Math.min(100, (elapsedSeconds * 100) / growthTime);
    }

    // Getters et setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public int getGrowthTime() {
        return growthTime;
    }

    public void setGrowthTime(int growthTime) {
        this.growthTime = growthTime;
    }

    public int getGrowthStage() {
        updateGrowthStage();
        return growthStage;
    }

    public long getPlantTime() {
        return plantTime;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getYield() {
        return yield;
    }

    public void setYield(int yield) {
        this.yield = yield;
    }

    @Override
    public String toString() {
        return name + " (" + getGrowthPercentage() + "% de croissance, position: " +
                (position != null ? position : "non planté") + ")";
    }
}