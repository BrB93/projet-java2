package farm;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Farm implements Serializable {
    private double money;
    private int fieldCount;
    private Map<String, Integer> inventory;
    private List<Crop> plantedCrops;
    private List<Animal> animals;

    public Farm(double initialMoney, int initialFieldCount) {
        this.money = initialMoney;
        this.fieldCount = initialFieldCount;
        this.inventory = new HashMap<>();
        this.plantedCrops = new ArrayList<>();
        this.animals = new ArrayList<>();
    }

    public double getMoney() {
        return money;
    }

    public void addMoney(double amount) {
        this.money += amount;
    }

    public boolean spendMoney(double amount) {
        if (money >= amount) {
            money -= amount;
            return true;
        }
        return false;
    }

    public int getFieldCount() {
        return fieldCount;
    }

    public void addCropToInventory(String cropType) {
        inventory.put(cropType, inventory.getOrDefault(cropType, 0) + 1);
    }

    public boolean hasCropInInventory(String cropType) {
        return inventory.getOrDefault(cropType, 0) > 0;
    }

    public void removeCropFromInventory(String cropType) {
        if (hasCropInInventory(cropType)) {
            inventory.put(cropType, inventory.get(cropType) - 1);
        }
    }

    public void plantCrop(String cropType) {
        if (hasCropInInventory(cropType) && plantedCrops.size() < fieldCount) {
            Crop crop;
            switch (cropType) {
                case "blé":
                    crop = new Crop("blé", 10, 20, 30); // 30 secondes pour pousser
                    break;
                case "maïs":
                    crop = new Crop("maïs", 15, 30, 45); // 45 secondes pour pousser
                    break;
                case "carotte":
                    crop = new Crop("carotte", 8, 16, 20); // 20 secondes pour pousser
                    break;
                default:
                    return;
            }
            plantedCrops.add(crop);
            removeCropFromInventory(cropType);
        }
    }

    public void addAnimal(String animalType) {
        Animal animal;
        switch (animalType) {
            case "poule":
                animal = new Animal("poule", 50, 5, 15); // 15 secondes pour produire
                break;
            case "vache":
                animal = new Animal("vache", 200, 20, 30); // 30 secondes pour produire
                break;
            case "cochon":
                animal = new Animal("cochon", 150, 25, 40); // 40 secondes pour produire
                break;
            default:
                return;
        }
        animals.add(animal);
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
            System.out.println("Ferme sauvegardée avec succès");
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Farm loadFarmState() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("farm_save.dat"))) {
            Farm farm = (Farm) in.readObject();
            System.out.println("Ferme chargée avec succès");
            return farm;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur lors du chargement: " + e.getMessage());
            return null;
        }
    }
}