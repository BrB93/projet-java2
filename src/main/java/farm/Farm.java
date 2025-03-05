package farm;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Farm implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(Farm.class.getName());
    private static final String SAVE_FILE_PATH = "farm_data.ser";

    // Propriétés de base de la ferme
    private String name;
    private int size;
    private int money = 0;
    private Map<String, Integer> inventory;
    private Map<String, Integer> resources;

    // Collections pour les cultures et les animaux
    private List<Crop> plantedCrops;
    private List<Animal> animals;

    // Constructeur principal
    public Farm(String name, int size) {
        this.name = name;
        this.size = size;
        this.money = 1000; // Capital de départ
        this.inventory = new HashMap<>();
        this.resources = new HashMap<>();
        this.plantedCrops = new ArrayList<>();
        this.animals = new ArrayList<>();
        LOGGER.info("Nouvelle ferme créée : " + name + ", taille: " + size + " hectares");
    }

    // Getters et setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        if (size > 0) {
            this.size = size;
        } else {
            LOGGER.warning("Tentative de définir une taille de ferme invalide: " + size);
        }
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void addMoney(int amount) {
        if (amount > 0) {
            this.money += amount;
            LOGGER.info("Ajout de " + amount + "€ au solde. Nouveau solde: " + this.money + "€");
        } else {
            LOGGER.warning("Tentative d'ajouter un montant négatif: " + amount);
        }
    }

    public boolean spendMoney(int amount) {
        if (amount <= 0) {
            LOGGER.warning("Tentative de dépenser un montant invalide: " + amount);
            return false;
        }

        if (this.money >= amount) {
            this.money -= amount;
            LOGGER.info("Dépense de " + amount + "€. Nouveau solde: " + this.money + "€");
            return true;
        } else {
            LOGGER.info("Fonds insuffisants pour dépenser " + amount + "€. Solde actuel: " + this.money + "€");
            return false;
        }
    }

    // Gestion de l'inventaire
    public Map<String, Integer> getInventory() {
        return inventory;
    }

    public void setInventory(Map<String, Integer> inventory) {
        this.inventory = inventory;
    }

    public void addToInventory(String item, int quantity) {
        if (quantity <= 0) {
            LOGGER.warning("Quantité invalide pour l'ajout à l'inventaire: " + quantity);
            return;
        }

        inventory.put(item, inventory.getOrDefault(item, 0) + quantity);
        LOGGER.fine("Ajout de " + quantity + " " + item + "(s) à l'inventaire");
    }

    public boolean removeFromInventory(String item, int quantity) {
        if (quantity <= 0) {
            LOGGER.warning("Quantité invalide pour le retrait de l'inventaire: " + quantity);
            return false;
        }

        int currentQuantity = inventory.getOrDefault(item, 0);
        if (currentQuantity >= quantity) {
            int newQuantity = currentQuantity - quantity;
            if (newQuantity > 0) {
                inventory.put(item, newQuantity);
            } else {
                inventory.remove(item);
            }
            LOGGER.fine("Retrait de " + quantity + " " + item + "(s) de l'inventaire");
            return true;
        } else {
            LOGGER.info("Quantité insuffisante de " + item + " dans l'inventaire");
            return false;
        }
    }

    // Gestion des ressources générales
    public Map<String, Integer> getResources() {
        return resources;
    }

    public void setResources(Map<String, Integer> resources) {
        this.resources = resources;
    }

    public void addResource(String resource, int quantity) {
        if (quantity <= 0) {
            LOGGER.warning("Quantité invalide pour l'ajout de ressource: " + quantity);
            return;
        }

        resources.put(resource, resources.getOrDefault(resource, 0) + quantity);
        LOGGER.fine("Ajout de " + quantity + " " + resource + "(s) aux ressources");
    }

    public boolean consumeResource(String resource, int quantity) {
        if (quantity <= 0) {
            LOGGER.warning("Quantité invalide pour la consommation de ressource: " + quantity);
            return false;
        }

        int currentQuantity = resources.getOrDefault(resource, 0);
        if (currentQuantity >= quantity) {
            int newQuantity = currentQuantity - quantity;
            if (newQuantity > 0) {
                resources.put(resource, newQuantity);
            } else {
                resources.remove(resource);
            }
            LOGGER.fine("Consommation de " + quantity + " " + resource + "(s)");
            return true;
        } else {
            LOGGER.info("Quantité insuffisante de " + resource);
            return false;
        }
    }

    // Méthode de vente de ressources
    public boolean sellResource(String resource, int amount, int pricePerUnit) {
        if (amount <= 0 || pricePerUnit <= 0) {
            LOGGER.warning("Paramètres invalides pour la vente: quantité=" + amount + ", prix=" + pricePerUnit);
            return false;
        }

        int currentAmount = inventory.getOrDefault(resource, 0);
        if (currentAmount >= amount) {
            // Mettre à jour l'inventaire
            inventory.put(resource, currentAmount - amount);
            if (inventory.get(resource) <= 0) {
                inventory.remove(resource);
            }

            // Ajouter au solde de la ferme
            int profit = amount * pricePerUnit;
            addMoney(profit);

            LOGGER.info("Vente réussie: " + amount + " " + resource + "(s) pour " + profit + "€");
            return true;
        } else {
            LOGGER.info("Stock insuffisant pour vendre " + amount + " " + resource + "(s)");
            return false;
        }
    }

    // Gestion des cultures
    public List<Crop> getPlantedCrops() {
        return plantedCrops;
    }

    public void setPlantedCrops(List<Crop> plantedCrops) {
        this.plantedCrops = plantedCrops;
    }

    public boolean plantCrop(Crop crop) {
        if (crop == null) {
            LOGGER.warning("Tentative de planter une culture nulle");
            return false;
        }

        // Vérifier s'il y a assez d'espace
        if (plantedCrops.size() < size) {
            plantedCrops.add(crop);
            LOGGER.info("Culture plantée: " + crop.getType());
            return true;
        } else {
            LOGGER.info("Espace insuffisant pour planter une nouvelle culture");
            return false;
        }
    }

    public boolean harvestCrop(int index) {
        if (index < 0 || index >= plantedCrops.size()) {
            LOGGER.warning("Index invalide pour la récolte: " + index);
            return false;
        }

        Crop crop = plantedCrops.get(index);
        if (crop.isReadyToHarvest()) {
            String resourceName = crop.getType();
            int yield = crop.getYield();

            // Ajouter la récolte à l'inventaire
            addToInventory(resourceName, yield);

            // Retirer la culture
            plantedCrops.remove(index);

            LOGGER.info("Récolte de " + yield + " " + resourceName);
            return true;
        } else {
            LOGGER.info("La culture n'est pas prête à être récoltée");
            return false;
        }
    }

    // Gestion des animaux
    public List<Animal> getAnimals() {
        return animals;
    }

    public void setAnimals(List<Animal> animals) {
        this.animals = animals;
    }

    public boolean addAnimal(Animal animal) {
        if (animal == null) {
            LOGGER.warning("Tentative d'ajouter un animal nul");
            return false;
        }

        // Vérifier s'il y a assez d'espace (1 animal = 0.2 hectare)
        if (animals.size() < size * 5) {
            animals.add(animal);
            LOGGER.info("Animal ajouté: " + animal.getType());
            return true;
        } else {
            LOGGER.info("Espace insuffisant pour ajouter un nouvel animal");
            return false;
        }
    }

    public boolean sellAnimal(int index) {
        if (index < 0 || index >= animals.size()) {
            LOGGER.warning("Index invalide pour la vente d'animal: " + index);
            return false;
        }

        Animal animal = animals.get(index);
        int price = animal.getPrice();

        // Ajouter le prix à l'argent de la ferme
        addMoney(price);

        // Retirer l'animal
        animals.remove(index);

        LOGGER.info("Animal vendu pour " + price + "€");
        return true;
    }

    // Méthodes de sauvegarde et chargement
    public void saveFarmState() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE_PATH))) {
            oos.writeObject(this);
            LOGGER.info("État de la ferme sauvegardé avec succès");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la sauvegarde de la ferme", e);
        }
    }

    public static Farm loadFarmState() {
        File saveFile = new File(SAVE_FILE_PATH);
        if (!saveFile.exists()) {
            LOGGER.info("Aucun fichier de sauvegarde trouvé");
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile))) {
            Farm farm = (Farm) ois.readObject();
            LOGGER.info("État de la ferme chargé avec succès");
            return farm;
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement de la ferme", e);
            return null;
        }
    }

    @Override
    public String toString() {
        return "Farm{" +
                "name='" + name + '\'' +
                ", size=" + size +
                ", money=" + money +
                ", plantedCrops=" + plantedCrops.size() +
                ", animals=" + animals.size() +
                '}';
    }
}