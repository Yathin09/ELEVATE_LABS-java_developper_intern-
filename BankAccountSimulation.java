import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Transaction class to store transaction history
class Transaction {
    private String type;
    private double amount;
    private LocalDateTime timestamp;
    private double balanceAfter;
    
    public Transaction(String type, double amount, double balanceAfter) {
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.balanceAfter = balanceAfter;
    }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("%-10s | $%-8.2f | %s | Balance: $%.2f", 
            type, amount, timestamp.format(formatter), balanceAfter);
    }
}

// Base Account class
class Account {
    protected String accountNumber;
    protected String accountHolder;
    protected double balance;
    protected List<Transaction> transactionHistory;
    
    // Constructor
    public Account(String accountNumber, String accountHolder, double initialBalance) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
        
        if (initialBalance > 0) {
            transactionHistory.add(new Transaction("DEPOSIT", initialBalance, balance));
        }
    }
    
    // Deposit method
    public void deposit(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid deposit amount. Amount must be positive.");
            return;
        }
        
        balance += amount;
        transactionHistory.add(new Transaction("DEPOSIT", amount, balance));
        System.out.printf("Successfully deposited $%.2f. New balance: $%.2f%n", amount, balance);
    }
    
    // Withdraw method
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid withdrawal amount. Amount must be positive.");
            return false;
        }
        
        if (amount > balance) {
            System.out.println("Insufficient funds. Current balance: $" + balance);
            return false;
        }
        
        balance -= amount;
        transactionHistory.add(new Transaction("WITHDRAW", amount, balance));
        System.out.printf("Successfully withdrew $%.2f. New balance: $%.2f%n", amount, balance);
        return true;
    }
    
    // Get balance
    public double getBalance() {
        return balance;
    }
    
    // Get account details
    public void displayAccountInfo() {
        System.out.println("\n=== Account Information ===");
        System.out.println("Account Number: " + accountNumber);
        System.out.println("Account Holder: " + accountHolder);
        System.out.println("Current Balance: $" + balance);
        System.out.println("Account Type: " + this.getClass().getSimpleName());
    }
    
    // Display transaction history
    public void displayTransactionHistory() {
        System.out.println("\n=== Transaction History ===");
        System.out.println("Type       | Amount   | Date & Time         | Balance After");
        System.out.println("--------------------------------------------------------");
        
        if (transactionHistory.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            for (Transaction transaction : transactionHistory) {
                System.out.println(transaction);
            }
        }
    }
    
    // Getters
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public String getAccountHolder() {
        return accountHolder;
    }
}

// SavingsAccount class - inherits from Account
class SavingsAccount extends Account {
    private double interestRate;
    private static final double MIN_BALANCE = 100.0;
    
    public SavingsAccount(String accountNumber, String accountHolder, double initialBalance, double interestRate) {
        super(accountNumber, accountHolder, initialBalance);
        this.interestRate = interestRate;
    }
    
    // Override withdraw method to enforce minimum balance
    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid withdrawal amount. Amount must be positive.");
            return false;
        }
        
        if ((balance - amount) < MIN_BALANCE) {
            System.out.printf("Cannot withdraw. Minimum balance of $%.2f must be maintained.%n", MIN_BALANCE);
            System.out.printf("Current balance: $%.2f, Attempted withdrawal: $%.2f%n", balance, amount);
            return false;
        }
        
        return super.withdraw(amount);
    }
    
    // Calculate and add interest
    public void addInterest() {
        double interest = balance * (interestRate / 100);
        balance += interest;
        transactionHistory.add(new Transaction("INTEREST", interest, balance));
        System.out.printf("Interest of $%.2f added at %.2f%% rate. New balance: $%.2f%n", 
            interest, interestRate, balance);
    }
    
    @Override
    public void displayAccountInfo() {
        super.displayAccountInfo();
        System.out.printf("Interest Rate: %.2f%%%n", interestRate);
        System.out.printf("Minimum Balance Required: $%.2f%n", MIN_BALANCE);
    }
}

// CheckingAccount class - inherits from Account
class CheckingAccount extends Account {
    private double overdraftLimit;
    private static final double OVERDRAFT_FEE = 35.0;
    
    public CheckingAccount(String accountNumber, String accountHolder, double initialBalance, double overdraftLimit) {
        super(accountNumber, accountHolder, initialBalance);
        this.overdraftLimit = overdraftLimit;
    }
    
    // Override withdraw method to allow overdraft
    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid withdrawal amount. Amount must be positive.");
            return false;
        }
        
        if (amount > (balance + overdraftLimit)) {
            System.out.printf("Transaction declined. Exceeds overdraft limit.%n");
            System.out.printf("Current balance: $%.2f, Overdraft limit: $%.2f%n", balance, overdraftLimit);
            return false;
        }
        
        balance -= amount;
        
        // Apply overdraft fee if balance goes negative
        if (balance < 0) {
            balance -= OVERDRAFT_FEE;
            transactionHistory.add(new Transaction("WITHDRAW", amount, balance + OVERDRAFT_FEE));
            transactionHistory.add(new Transaction("OVERDRAFT_FEE", OVERDRAFT_FEE, balance));
            System.out.printf("Withdrawal successful. Overdraft fee of $%.2f applied.%n", OVERDRAFT_FEE);
        } else {
            transactionHistory.add(new Transaction("WITHDRAW", amount, balance));
        }
        
        System.out.printf("Successfully withdrew $%.2f. New balance: $%.2f%n", amount, balance);
        return true;
    }
    
    @Override
    public void displayAccountInfo() {
        super.displayAccountInfo();
        System.out.printf("Overdraft Limit: $%.2f%n", overdraftLimit);
    }
}

// Bank class to manage multiple accounts
class Bank {
    private List<Account> accounts;
    private int nextAccountNumber;
    
    public Bank() {
        this.accounts = new ArrayList<>();
        this.nextAccountNumber = 1001;
    }
    
    public Account createSavingsAccount(String accountHolder, double initialBalance, double interestRate) {
        String accountNumber = "SAV" + nextAccountNumber++;
        SavingsAccount account = new SavingsAccount(accountNumber, accountHolder, initialBalance, interestRate);
        accounts.add(account);
        System.out.println("Savings account created successfully!");
        return account;
    }
    
    public Account createCheckingAccount(String accountHolder, double initialBalance, double overdraftLimit) {
        String accountNumber = "CHK" + nextAccountNumber++;
        CheckingAccount account = new CheckingAccount(accountNumber, accountHolder, initialBalance, overdraftLimit);
        accounts.add(account);
        System.out.println("Checking account created successfully!");
        return account;
    }
    
    public Account findAccount(String accountNumber) {
        for (Account account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }
        return null;
    }
    
    public void displayAllAccounts() {
        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }
        
        System.out.println("\n=== All Bank Accounts ===");
        for (Account account : accounts) {
            System.out.printf("%-10s | %-20s | $%-10.2f | %s%n",
                account.getAccountNumber(),
                account.getAccountHolder(),
                account.getBalance(),
                account.getClass().getSimpleName()
            );
        }
    }
}

// Main class with interactive menu
public class BankAccountSimulation {
    private static Bank bank = new Bank();
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        System.out.println("=== Welcome to Bank Account Simulation ===");
        
        // Create some sample accounts for demonstration
        createSampleAccounts();
        
        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    depositMoney();
                    break;
                case 3:
                    withdrawMoney();
                    break;
                case 4:
                    checkBalance();
                    break;
                case 5:
                    viewTransactionHistory();
                    break;
                case 6:
                    viewAccountDetails();
                    break;
                case 7:
                    addInterestToSavings();
                    break;
                case 8:
                    bank.displayAllAccounts();
                    break;
                case 9:
                    running = false;
                    System.out.println("Thank you for using Bank Account Simulation!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }
    
    private static void createSampleAccounts() {
        bank.createSavingsAccount("John Doe", 1000.0, 2.5);
        bank.createCheckingAccount("Jane Smith", 500.0, 1000.0);
        System.out.println("\nSample accounts created for demonstration.\n");
    }
    
    private static void displayMenu() {
        System.out.println("\n=== Bank Account Menu ===");
        System.out.println("1. Create Account");
        System.out.println("2. Deposit Money");
        System.out.println("3. Withdraw Money");
        System.out.println("4. Check Balance");
        System.out.println("5. View Transaction History");
        System.out.println("6. View Account Details");
        System.out.println("7. Add Interest (Savings Account)");
        System.out.println("8. View All Accounts");
        System.out.println("9. Exit");
        System.out.print("Choose an option: ");
    }
    
    private static void createAccount() {
        System.out.println("\n=== Create New Account ===");
        System.out.println("1. Savings Account");
        System.out.println("2. Checking Account");
        System.out.print("Choose account type: ");
        
        int type = getIntInput();
        System.out.print("Enter account holder name: ");
        String name = scanner.nextLine();
        System.out.print("Enter initial balance: $");
        double balance = getDoubleInput();
        
        if (type == 1) {
            System.out.print("Enter interest rate (%): ");
            double rate = getDoubleInput();
            bank.createSavingsAccount(name, balance, rate);
        } else if (type == 2) {
            System.out.print("Enter overdraft limit: $");
            double overdraft = getDoubleInput();
            bank.createCheckingAccount(name, balance, overdraft);
        } else {
            System.out.println("Invalid account type.");
        }
    }
    
    private static void depositMoney() {
        Account account = selectAccount();
        if (account != null) {
            System.out.print("Enter deposit amount: $");
            double amount = getDoubleInput();
            account.deposit(amount);
        }
    }
    
    private static void withdrawMoney() {
        Account account = selectAccount();
        if (account != null) {
            System.out.print("Enter withdrawal amount: $");
            double amount = getDoubleInput();
            account.withdraw(amount);
        }
    }
    
    private static void checkBalance() {
        Account account = selectAccount();
        if (account != null) {
            System.out.printf("Current balance: $%.2f%n", account.getBalance());
        }
    }
    
    private static void viewTransactionHistory() {
        Account account = selectAccount();
        if (account != null) {
            account.displayTransactionHistory();
        }
    }
    
    private static void viewAccountDetails() {
        Account account = selectAccount();
        if (account != null) {
            account.displayAccountInfo();
        }
    }
    
    private static void addInterestToSavings() {
        Account account = selectAccount();
        if (account instanceof SavingsAccount) {
            ((SavingsAccount) account).addInterest();
        } else {
            System.out.println("Interest can only be added to Savings accounts.");
        }
    }
    
    private static Account selectAccount() {
        bank.displayAllAccounts();
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        Account account = bank.findAccount(accountNumber);
        
        if (account == null) {
            System.out.println("Account not found.");
        }
        return account;
    }
    
    private static int getIntInput() {
        while (true) {
            try {
                int value = Integer.parseInt(scanner.nextLine());
                return value;
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }
    
    private static double getDoubleInput() {
        while (true) {
            try {
                double value = Double.parseDouble(scanner.nextLine());
                return value;
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid amount: ");
            }
        }
    }
}