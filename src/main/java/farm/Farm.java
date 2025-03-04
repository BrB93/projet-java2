package farm;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.Serializable;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Farm implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(Farm.class.getName());

    private double money;
    private int fieldCount;
    private Map<String, Integer> inventory;
    private List<Crop> plantedCrops;
    private List<Animal> animals;
    // Ajout de la taille de la ferme
    private int size = 5;
    // Ajout du système de ressources
    private Map<String, Integer> resources = new HashMap<>();
    // Ajout du compteur de cultures
    private Map<String, Integer> cropCounts = new HashMap<>();

    public Farm(double initialMoney, int initialFieldCount) {
        this.money = initialMoney;
        this.fieldCount = initialFieldCount;
        this.inventory = new HashMap<>();
        this.plantedCrops = new ArrayList<>();
        this.animals = new ArrayList<>();
        this.resources = new HashMap<>();
        this.cropCounts = new HashMap<>();
    }

    public double getMoney() {
        return money;
    }

    public void addMoney(double amount) {
        this.money += amount;
        LOGGER.fine("Argent ajouté: " + amount + ", total: " + money);
    }

    public boolean spendMoney(double amount) {
        if (money >= amount) {
            money -= amount;
            LOGGER.fine("Argent dépensé: " + amount + ", reste: " + money);
            return true;
        }
        LOGGER.warning("Fonds insuffisants pour dépenser " + amount);
        return false;
    }

    public int getFieldCount() {
        return fieldCount;
    }

    public void addCropToInventory(String cropType) {
        inventory.put(cropType, inventory.getOrDefault(cropType, 0) + 1);
        LOGGER.fine("Culture ajoutée à l'inventaire: " + cropType);
    }

    public boolean hasCropInInventory(String cropType) {
        return inventory.getOrDefault(cropType, 0) > 0;
    }

    public void removeCropFromInventory(String cropType) {
        if (hasCropInInventory(cropType)) {
            inventory.put(cropType, inventory.get(cropType) - 1);
            LOGGER.fine("Culture retirée de l'inventaire: " + cropType);
        } else {
            LOGGER.warning("Tentative de retirer une culture absente de l'inventaire: " + cropType);
        }
    }

    public void plantCrop(String cropType) {
        if (hasCropInInventory(cropType) && plantedCrops.size() < fieldCount) {
            // Utiliser le constructeur simple qui existe maintenant
            Crop crop = new Crop(cropType);

            plantedCrops.add(crop);
            removeCropFromInventory(cropType);
            LOGGER.info("Culture plantée: " + cropType);
        } else {
            LOGGER.warning("Impossible de planter: " + cropType +
                    (plantedCrops.size() >= fieldCount ? " (terrain plein)" : " (pas dans l'inventaire)"));
        }
    }

    public void addAnimal(String animalType) {
        // Utiliser le constructeur simple
        Animal animal = new Animal(animalType);
        animals.add(animal);
        LOGGER.info("Animal ajouté: " + animalType);
    }

    public void addCrop(String type) {
        // Ajouter au compteur de cultures
        cropCounts.put(type, cropCounts.getOrDefault(type, 0) + 1);
        LOGGER.fine("Compteur de culture incrémenté pour: " + type);
    }

    public List<Crop> getPlantedCrops() {
        return plantedCrops;
    }

    public List<Animal> getAnimals() {
        return animals;
    }

    public Map<String, Integer> getInventory() {
        return inventory;
    }

    public void saveFarmState() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("farm_save.dat"))) {
            out.writeObject(this);
            LOGGER.info("Ferme sauvegardée avec succès");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la sauvegarde", e);
        }
    }

    public static Farm loadFarmState() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("farm_save.dat"))) {
            Farm farm = (Farm) in.readObject();
            LOGGER.info("Ferme chargée avec succès");
            return farm;
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement", e);
            return null;
        }
    }

    public int getSize() {
        return size;
    }

    public Map<String, Integer> getResources() {
        return resources;
    }

    public void addResource(String name, int quantity) {
        resources.put(name, resources.getOrDefault(name, 0) + quantity);
        LOGGER.fine("Ressource ajoutée: " + name + " (" + quantity + ")");
    }

    public Map<String, Integer> getCropCounts() {
        return cropCounts;
    }

    public void addToInventory(String itemType, int quantity) {
        inventory.put(itemType, inventory.getOrDefault(itemType, 0) + quantity);
        LOGGER.fine(quantity + " " + itemType + " ajouté(s) à l'inventaire");
    }

    public void removeFromInventory(String itemType, int quantity) {
        int current = inventory.getOrDefault(itemType, 0);
        if (current >= quantity) {
            inventory.put(itemType, current - quantity);
            LOGGER.fine(quantity + " " + itemType + " retiré(s) de l'inventaire");
        } else {
            LOGGER.warning("Stock insuffisant pour retirer " + quantity + " " + itemType);
        }
    }

}