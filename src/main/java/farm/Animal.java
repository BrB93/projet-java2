package farm;

import java.io.Serializable;
import java.util.logging.Logger;

public class Animal implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(Animal.class.getName());

    // Propriétés de base
    private String type;
    private int price;
    private int productionAmount;
    private int productionInterval; // en secondes
    private int productionCycle = 0;
    private String position;
    private long lastProductionTime;
    private boolean isProducing = true;

    /**
     * Constructeur simple avec juste le type
     */
    public Animal(String type) {
        this.type = type.toLowerCase();
        setDefaultValues(this.type);
        this.lastProductionTime = System.currentTimeMillis();
    }

    /**
     * Constructeur complet avec tous les paramètres
     */
    public Animal(String type, int price, int productionAmount, int productionInterval) {
        this.type = type.toLowerCase();
        this.price = price;
        this.productionAmount = productionAmount;
        this.productionInterval = productionInterval;
        this.lastProductionTime = System.currentTimeMillis();
    }

    /**
     * Définit les valeurs par défaut selon le type d'animal
     */
    private void setDefaultValues(String animalType) {
        switch(animalType) {
            case "poule":
                this.price = 50;
                this.productionAmount = 5;
                this.productionInterval = 15;
                break;
            case "vache":
                this.price = 200;
                this.productionAmount = 20;
                this.productionInterval = 30;
                break;
            case "cochon":
                this.price = 150;
                this.productionAmount = 25;
                this.productionInterval = 40;
                break;
            default:
                LOGGER.warning("Type d'animal inconnu: " + animalType + ", utilisation des valeurs par défaut");
                this.price = 100;
                this.productionAmount = 10;
                this.productionInterval = 20;
                break;
        }
    }

    /**
     * Met à jour le cycle de production et retourne la quantité produite si approprié
     * @param farm La ferme à mettre à jour avec la production
     * @return La quantité produite (0 si pas de production à ce cycle)
     */
    public int updateProductionCycle(Farm farm) {
        if (!isProducing) {
            return 0;
        }

        long currentTime = System.currentTimeMillis();
        long elapsedSeconds = (currentTime - lastProductionTime) / 1000;

        if (elapsedSeconds >= productionInterval) {
            lastProductionTime = currentTime;

            // Production selon le type d'animal
            String resource = getProductionType();
            if (farm != null) {
                farm.addResource(resource, productionAmount);
                LOGGER.fine(type + " a produit " + productionAmount + " " + resource);
                return productionAmount;
            }
        }

        return 0;
    }

    /**
     * Détermine le type de ressource produite par l'animal
     */
    public String getProductionType() {
        switch(type) {
            case "poule":
                return "œuf";
            case "vache":
                return "lait";
            case "cochon":
                return "viande";
            default:
                return "ressource";
        }
    }

    /**
     * Calcule le temps restant avant la prochaine production
     * @return Temps en secondes avant la prochaine production
     */
    public int getRemainingProductionTime() {
        long currentTime = System.currentTimeMillis();
        long elapsedSeconds = (currentTime - lastProductionTime) / 1000;
        int remaining = productionInterval - (int)elapsedSeconds;
        return Math.max(0, remaining);
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

    public int getProductionAmount() {
        return productionAmount;
    }

    public void setProductionAmount(int productionAmount) {
        this.productionAmount = productionAmount;
    }

    public int getProductionInterval() {
        return productionInterval;
    }

    public void setProductionInterval(int productionInterval) {
        this.productionInterval = productionInterval;
    }

    public int getProductionCycle() {
        return productionCycle;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean isProducing() {
        return isProducing;
    }

    public void setProducing(boolean producing) {
        isProducing = producing;
    }

    public long getLastProductionTime() {
        return lastProductionTime;
    }

    /**
     * Réinitialise le cycle de production
     */
    public void resetProductionCycle() {
        this.lastProductionTime = System.currentTimeMillis();
        this.productionCycle = 0;
    }

    @Override
    public String toString() {
        return type + " (position: " + (position != null ? position : "non placé") + ")";
    }
}