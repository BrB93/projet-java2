package farm;

public class Crop {
    private String name;
    private int growthTime;
    private int currentAge;
    private boolean planted;
    private double buyPrice;
    private double sellPrice;

    public Crop(String name, int growthTime, double buyPrice, double sellPrice) {
        this.name = name;
        this.growthTime = growthTime;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.currentAge = 0;
        this.planted = false;
    }

    public void plant() {
        this.planted = true;
    }

    public void updateGrowth() {
        if (planted) {
            currentAge++;
        }
    }

    public boolean isMature() {
        return planted && currentAge >= growthTime;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public double getSellPrice() {
        return isMature() ? sellPrice : 0;
    }

    // Autres getters/setters selon besoin
}