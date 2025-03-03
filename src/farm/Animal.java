package farm;

public class Animal {
    private String name;
    private double buyPrice;
    private double dailyProductValue;
    private double health;

    public Animal(String name, double buyPrice, double dailyProductValue) {
        this.name = name;
        this.buyPrice = buyPrice;
        this.dailyProductValue = dailyProductValue;
        this.health = 100;
    }

    public void updateState() {
        // Code pour mettre à jour l'état de l'animal
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public double getDailyProductValue() {
        return dailyProductValue * (health / 100);
    }

    // Autres getters/setters selon besoin
}