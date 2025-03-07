package farm;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class FinanceController implements Initializable {

    @FXML private TableView<FinanceManager.Transaction> transactionTable;
    @FXML private TableColumn<FinanceManager.Transaction, String> dateColumn;
    @FXML private TableColumn<FinanceManager.Transaction, String> descriptionColumn;
    @FXML private TableColumn<FinanceManager.Transaction, Integer> amountColumn;
    @FXML private TableColumn<FinanceManager.Transaction, String> typeColumn;

    @FXML private TextField descriptionField;
    @FXML private TextField amountField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private Label balanceLabel;
    @FXML private Label incomeLabel;
    @FXML private Label expenseLabel;

    private Farm farm;
    private FinanceManager financeManager;
    private ObservableList<FinanceManager.Transaction> transactions;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialiser la ComboBox avec les types de transactions
        typeComboBox.setItems(FXCollections.observableArrayList(
                FinanceManager.TransactionType.INCOME.getDisplayName(),
                FinanceManager.TransactionType.EXPENSE.getDisplayName()
        ));
        typeComboBox.getSelectionModel().selectFirst();

        // Configurer les colonnes du tableau
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("formattedDate"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("typeDisplayName"));

        // Formatter la colonne des montants
        amountColumn.setCellFactory(column -> new TableCell<FinanceManager.Transaction, Integer>() {
            @Override
            protected void updateItem(Integer amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(amount + " €");
                }
            }
        });
    }

    public void setFarm(Farm farm) {
        this.farm = farm;

        // S'il n'existe pas de gestionnaire de finances, en créer un
        if (farm != null) {
            // Supposons que le Farm a une méthode getFinanceManager()
            // À ajouter à la classe Farm
            financeManager = farm.getFinanceManager();
            if (financeManager == null) {
                financeManager = new FinanceManager();
                farm.setFinanceManager(financeManager);
            }
            refreshTransactionTable();
        }
    }

    @FXML
    private void handleAddTransaction() {
        try {
            String description = descriptionField.getText().trim();
            if (description.isEmpty()) {
                showAlert("La description ne peut pas être vide");
                return;
            }

            int amount = Integer.parseInt(amountField.getText().trim());
            if (amount <= 0) {
                showAlert("Le montant doit être supérieur à zéro");
                return;
            }

            FinanceManager.TransactionType type = typeComboBox.getValue().equals(
                    FinanceManager.TransactionType.INCOME.getDisplayName())
                    ? FinanceManager.TransactionType.INCOME
                    : FinanceManager.TransactionType.EXPENSE;

            financeManager.addTransaction(description, amount, type);

            // Mettre à jour le solde de la ferme
            if (type == FinanceManager.TransactionType.INCOME) {
                farm.addMoney(amount);
            } else {
                farm.spendMoney(amount);
            }

            // Rafraîchir le tableau et réinitialiser les champs
            refreshTransactionTable();
            descriptionField.clear();
            amountField.clear();

        } catch (NumberFormatException e) {
            showAlert("Veuillez entrer un montant valide");
        }
    }

    private void refreshTransactionTable() {
        transactions = FXCollections.observableArrayList(financeManager.getTransactions());
        transactionTable.setItems(transactions);

        // Mettre à jour les totaux
        balanceLabel.setText(financeManager.getBalance() + " €");
        incomeLabel.setText(financeManager.getTotalIncome() + " €");
        expenseLabel.setText(financeManager.getTotalExpenses() + " €");

        // Appliquer un style en fonction du solde (positif/négatif)
        if (financeManager.getBalance() < 0) {
            balanceLabel.setStyle("-fx-text-fill: red;");
        } else {
            balanceLabel.setStyle("-fx-text-fill: green;");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}