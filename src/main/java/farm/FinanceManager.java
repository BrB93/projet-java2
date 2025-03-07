package farm;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FinanceManager implements Serializable {
    private static final long serialVersionUID = 1L;

    public static enum TransactionType {
        INCOME("Revenu"),
        EXPENSE("Dépense");

        private final String displayName;

        TransactionType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public static class Transaction implements Serializable {
        private static final long serialVersionUID = 1L;

        private Date date;
        private String description;
        private int amount;
        private TransactionType type;

        public Transaction(String description, int amount, TransactionType type) {
            this.date = new Date();
            this.description = description;
            this.amount = amount;
            this.type = type;
        }

        public Date getDate() {
            return date;
        }

        public String getFormattedDate() {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            return formatter.format(date);
        }

        public String getDescription() {
            return description;
        }

        public int getAmount() {
            return amount;
        }

        public TransactionType getType() {
            return type;
        }

        public String getTypeDisplayName() {
            return type.getDisplayName();
        }

        @Override
        public String toString() {
            return String.format("%s: %s - %d€ (%s)", getFormattedDate(), description, amount, type.getDisplayName());
        }
    }

    private List<Transaction> transactions;

    public FinanceManager() {
        transactions = new ArrayList<>();
    }

    public void addTransaction(String description, int amount, TransactionType type) {
        transactions.add(new Transaction(description, amount, type));
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public int getTotalIncome() {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToInt(Transaction::getAmount)
                .sum();
    }

    public int getTotalExpenses() {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToInt(Transaction::getAmount)
                .sum();
    }

    public int getBalance() {
        return getTotalIncome() - getTotalExpenses();
    }
}