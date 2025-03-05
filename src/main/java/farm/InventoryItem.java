package farm;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class InventoryItem {
    private final SimpleStringProperty category;
    private final SimpleStringProperty name;
    private final SimpleIntegerProperty quantity;
    private final SimpleIntegerProperty value;

    public InventoryItem(String category, String name, int quantity, int value) {
        this.category = new SimpleStringProperty(category);
        this.name = new SimpleStringProperty(name);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.value = new SimpleIntegerProperty(value);
    }

    public String getCategory() {
        return category.get();
    }

    public String getName() {
        return name.get();
    }

    public int getQuantity() {
        return quantity.get();
    }

    public int getValue() {
        return value.get();
    }

    public SimpleStringProperty categoryProperty() {
        return category;
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public SimpleIntegerProperty quantityProperty() {
        return quantity;
    }

    public SimpleIntegerProperty valueProperty() {
        return value;
    }
}