package farm;

import java.io.Serializable;
import java.util.logging.Logger;

public class Animal implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(Animal.class.getName());
    public static final int BABY_STAGE = 0;
    public static final int YOUNG_STAGE = 1;
    public static final int ADULT_STAGE = 2;

    // Propriétés de base
    private String type;
    private int price;
    private int productionAmount;
    private int productionInterval; // en secondes
    private int productionCycle = 0;
    private String position;
    private long lastProductionTime;
    private boolean isProducing = true;
    private int developmentStage = BABY_STAGE;
    private long birthTime;
    private long timeToNextStage;
    private int maturationTime; // Ajout de la propriété manquante
    private long lastFeedTime = System.currentTimeMillis();
    private boolean isStarving = false;

    /**
     * Constructeur simple avec juste le type
     */
    public Animal(String type) {
        this.type = type.toLowerCase();
        setDefaultValues(this.type);
        this.birthTime = System.currentTimeMillis();
        this.lastProductionTime = System.currentTimeMillis();
        calculateTimeToNextStage();
    }

    // Dans la classe Animal
    public String produce() {
        if (type.equalsIgnoreCase("Poule")) {
            return "oeuf";
        } else if (type.equalsIgnoreCase("Vache")) {
            return "lait";
        } else if (type.equalsIgnoreCase("Mouton")) {
            return "laine";
        }
        return null;
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
            case "mouton":  // Changé de "cochon" à "mouton"
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

        switch(animalType) {
            case "poule":
                this.maturationTime = 10; // en secondes
                break;
            case "vache":
                this.maturationTime = 10;
                break;
            case "mouton":  // Changé de "cochon" à "mouton"
                this.maturationTime = 10;
                break;
            default:
                this.maturationTime = 10;
                break;
        }
    }
    private void calculateTimeToNextStage() {
        // Diviser le temps total de maturation en 2 périodes
        timeToNextStage = maturationTime / 2 * 1000; // Convertir en millisecondes
    }

    public void produceResource(Farm farm) {
        if (farm == null) return;

        String resourceType = "";
        int quantity = 0;

        switch (type.toLowerCase()) {
            case "poule":
                resourceType = "oeuf";
                quantity = 1;
                break;
            case "vache":
                resourceType = "lait";
                quantity = 2;
                break;
            case "mouton":
                resourceType = "laine";
                quantity = 1;
                break;
        }

        if (!resourceType.isEmpty() && quantity > 0) {
            LOGGER.info("Production de " + quantity + " " + resourceType + "(s) par " + type);
            farm.addToInventory(resourceType, quantity);
            lastProductionTime = System.currentTimeMillis();  // Utilisation de la variable existante
        }
    }
    public void updateDevelopmentStage() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - birthTime;

        if (developmentStage == BABY_STAGE && elapsedTime >= timeToNextStage) {
            developmentStage = YOUNG_STAGE;
            birthTime = currentTime; // Réinitialiser pour le prochain stade
        } else if (developmentStage == YOUNG_STAGE && elapsedTime >= timeToNextStage) {
            developmentStage = ADULT_STAGE;
        }
    }

    public int getDevelopmentStage() {
        return developmentStage;
    }

    public String getDisplayText() {
        switch(developmentStage) {
            case BABY_STAGE:
                return type + " (bébé)";
            case YOUNG_STAGE:
                return type + " (jeune)";
            case ADULT_STAGE:
                return type + " (adulte)";
            default:
                return type;
        }
    }

    public String getStageStyleClass() {
        switch(developmentStage) {
            case BABY_STAGE:
                return "baby-stage";
            case YOUNG_STAGE:
                return "young-stage";
            case ADULT_STAGE:
                return "adult-stage";
            default:
                return "";
        }
    }

    /**
     * Met à jour le cycle de production et retourne la quantité produite si approprié
     * @param farm La ferme à mettre à jour avec la production
     * @return La quantité produite (0 si pas de production à ce cycle)
     */
    public int updateProductionCycle(Farm farm) {
        // Les animaux ne produisent que s'ils sont adultes
        if (!isProducing || developmentStage < ADULT_STAGE) {
            return 0;
        }

        long currentTime = System.currentTimeMillis();
        long elapsedSeconds = (currentTime - lastProductionTime) / 500;

        if (elapsedSeconds >= productionInterval) {
            lastProductionTime = currentTime;
            String resource = getProductionType();
            if (farm != null) {
                farm.addResource(resource, productionAmount);
                LOGGER.fine(type + " a produit " + productionAmount + " " + resource);
                return productionAmount;
            }
        }

        return 0;
    }

    public boolean canProduce() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastProductionTime >= productionInterval * 500) { // Convertir en millisecondes
            lastProductionTime = currentTime;
            return true;
        }
        return false;
    }

    /**
     * Détermine le type de ressource produite par l'animal
     */
    public String getProductionType() {
        switch(type) {
            case "poule":
                return "oeuf";  // Assurez-vous de ne pas utiliser "œuf" avec accent
            case "vache":
                return "lait";
            case "mouton":
                return "laine";
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

    public int getMaturationTime() {
        return maturationTime;
    }

    public void setMaturationTime(int maturationTime) {
        this.maturationTime = maturationTime;
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

    public boolean needsFeeding() {
        return System.currentTimeMillis() - lastFeedTime > 60000; // 1 minute
    }

    public void feed() {
        lastFeedTime = System.currentTimeMillis();
        isStarving = false;
        // Limiter le développement à ADULT_STAGE (valeur 2)
        developmentStage = Math.min(developmentStage + 1, ADULT_STAGE);
        // Supprimer l'appel à updateStageStyleClass qui n'existe pas
    }


    public boolean isStarving() {
        return System.currentTimeMillis() - lastFeedTime > 120000; // 2 minutes
    }

    public long getLastFeedTime() {
        return lastFeedTime;
    }

}