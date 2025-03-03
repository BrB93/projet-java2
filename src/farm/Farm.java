package farm;

import java.util.ArrayList;
import java.util.List;

public class Farm {
    private double money;
    private List<Crop> crops;
    private List<Animal> animals;
    private int maxPlots; // Nombre de parcelles disponibles

    public Farm(double initialMoney, int maxPlots) {
        this.money = initialMoney;
        this.maxPlots = maxPlots;
        this.crops = new ArrayList<>();
        this.animals = new ArrayList<>();
    }

    public void buyCrop(Crop crop) {
        if (money >= crop.getBuyPrice() && crops.size() < maxPlots) {
            money -= crop.getBuyPrice();
            crop.plant();
            crops.add(crop);
        }
    }

    public void buyAnimal(Animal animal) {
        if (money >= animal.getBuyPrice()) {
            money -= animal.getBuyPrice();
            animals.add(animal);
        }
    }

    public void sellCrop(Crop crop) {
        if (crop.isMature()) {
            money += crop.getSellPrice();
            crops.remove(crop);
        }
    }

    // Ex: vendre les produits animaux (pas encore détaillé)
    public void sellAnimalProduct(double amount) {
        money += amount;
    }

    public void updateAll() {
        // Mettre à jour toutes les cultures
        for (Crop crop : crops) {
            crop.updateGrowth();
        }
        // Mettre à jour tous les animaux
        for (Animal animal : animals) {
            animal.updateState();
        }
    }

    public List<Crop> getCrops() {
        return crops; // où 'crops' est votre collection de cultures
    }
}
