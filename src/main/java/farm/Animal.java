package farm;

import java.io.Serializable;

public class Animal implements Serializable {
    private String type;
    private double buyPrice;
    private double productValue;
    private int productionTime; // temps en secondes
    private long lastProductionTime;

    public Animal(String type, double buyPrice, double productValue, int productionTime) {
        this.type = type;
        this.buyPrice = buyPrice;
        this.productValue = productValue;
        this.productionTime = productionTime;
        this.lastProductionTime = System.currentTimeMillis();
    }

    public String getType() {
        return type;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public double getProductValue() {
        return productValue;
    }

    public boolean canProduce() {
        long currentTime = System.currentTimeMillis();
        long elapsedSeconds = (currentTime - lastProductionTime) / 1000;
        return elapsedSeconds >= productionTime;
    }

    public void collect() {
        if (canProduce()) {
            lastProductionTime = System.currentTimeMillis();
        }
    }

    public int getTimeUntilProduction() {
        long currentTime = System.currentTimeMillis();
        long elapsedSeconds = (currentTime - lastProductionTime) / 1000;
        int remainingSeconds = productionTime - (int)elapsedSeconds;
        return Math.max(0, remainingSeconds);
    }
}