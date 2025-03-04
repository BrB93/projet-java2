package farm;

import java.io.Serializable;

public class Crop implements Serializable {
    private String name;
    private double buyPrice;
    private double sellPrice;
    private int growthTime; // temps en secondes
    private int growthStage; // 0: semé, 1: en croissance, 2: mûr
    private long plantTime;

    public Crop(String name, double buyPrice, double sellPrice, int growthTime) {
        this.name = name;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.growthTime = growthTime;
        this.growthStage = 0;
        this.plantTime = System.currentTimeMillis();
    }

    public String getName() {
        return name;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public int getGrowthStage() {
        updateGrowthStage();
        return growthStage;
    }

    public void updateGrowthStage() {
        long currentTime = System.currentTimeMillis();
        long elapsedSeconds = (currentTime - plantTime) / 1000;

        if (elapsedSeconds >= growthTime && growthStage < 2) {
            growthStage = 2; // mûr
        } else if (elapsedSeconds >= growthTime / 2 && growthStage < 1) {
            growthStage = 1; // en croissance
        }
    }

    public boolean isReadyToHarvest() {
        updateGrowthStage();
        return growthStage == 2;
    }
}