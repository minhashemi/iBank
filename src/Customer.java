import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Customer extends User {
    private final int cardNumber;
    private final int pin;
    private double balance;
    private ArrayList<Transaction> transactionHistory = new ArrayList<>();
    private boolean suspended;
    private int tries;

    // register
    public Customer(String name, String username, String password, int pin) {
        super(name, username, password, "Customer");
        this.cardNumber = (int) (Math.random() * 9_999_999);
        this.pin = pin;
        setBalance(0.0);
        this.suspended = false;
    }

    // login
    public Customer(String name, String username, String password, int pin, double balance, int cardNumber) {
        super(name, username, password, "Customer");
        this.cardNumber = cardNumber;
        this.pin = pin;
        setBalance(balance);
        this.suspended = false;
    }

    public int getCardNumber() {
        return cardNumber;
    }
    public int getPin() {
        return pin;
    }

    public int getTries(){
        return tries;
    }

    public boolean getSuspended(){
        return suspended;
    }
    public double getBalance(){
        return balance;
    }
    public void setBalance(double balance){
        this.balance = balance;
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Card Number: " + cardNumber);
        System.out.println("PIN: " + pin);
        System.out.println("Balance: " + balance);
    }

    public void addTries(){
        this.tries += 1;
    }

    public void setSuspended(){
        this.suspended = true;
    }

    public void resetTries(){
        this.tries = 0;
    }



    public void addTransaction(String description) {
        Transaction transaction = new Transaction(description);
        transactionHistory.add(transaction);
        TransactionDatabase.saveTransaction(this.getUsername(), transaction);
    }


    public void deposit(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid amount. Please try again.\n");
            return;
        }

        Scanner input = new Scanner(System.in);
        System.out.print("Enter your PIN: ");
        int enteredPin;
        try {
            enteredPin = input.nextInt();

            while (enteredPin != this.pin) {
                if (getTries() == 9) {
                    setSuspended();
                    CustomerMenu.deposit();
                }

                System.out.println("Invalid PIN. Please try again.\n");
                addTries();

                System.out.print("Enter your PIN: ");
                enteredPin = input.nextInt();
            }
        } catch (InputMismatchException e) {
            if (getTries() == 9) {
                setSuspended();
                CustomerMenu.deposit();
            }

            System.out.println("Invalid PIN. Please try again.\n");
            addTries();
            CustomerMenu.deposit();
        }

        DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();

        this.balance += amount;
        addTransaction("(+) Deposit of " + amount + " at " + dtf.format(now));
        System.out.println("Deposit successful! Your new balance is " + this.balance + "\n");
        resetTries();
    }

    public void withdrawal(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid amount. Please try again.\n");
            return;
        }

        if (this.balance < amount) {
            System.out.println("Insufficient balance. Please try again.\n");
            return;
        }

        Scanner input = new Scanner(System.in);
        System.out.print("Enter your PIN: ");
        int enteredPin;
        try {
            enteredPin = input.nextInt();

            while (enteredPin != this.pin) {
                if (getTries() == 9) {
                    setSuspended();
                    CustomerMenu.withdrawal();
                }

                System.out.println("Invalid PIN. Please try again.\n");
                addTries();

                System.out.print("Enter your PIN: ");
                enteredPin = input.nextInt();
            }
        } catch (InputMismatchException e) {
            if (getTries() == 9) {
                setSuspended();
                CustomerMenu.withdrawal();
            }

            System.out.println("Invalid PIN. Please try again.\n");
            addTries();
            CustomerMenu.withdrawal();
        }

        DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        this.balance -= amount;

        addTransaction("(-) Withdrawal of " + amount + " at " + dtf.format(now));
        System.out.println("Withdrawal successful! Your new balance is " + this.balance + "\n");
        resetTries();
    }

    public void transfer(Customer receiver, double amount) {
        if (amount <= 0) {
            System.out.println("Invalid amount. Please try again.\n");
            return;
        }

        if (this.balance < amount) {
            System.out.println("Insufficient balance. Please try again.\n");
            return;
        }

        Scanner input = new Scanner(System.in);
        System.out.print("Enter your PIN: ");
        int enteredPin;
        try {
            enteredPin = input.nextInt();

            while (enteredPin != this.pin) {
                if (getTries() == 9) {
                    setSuspended();
                    CustomerMenu.transfer(App.customers);
                }

                System.out.println("Invalid PIN. Please try again.\n");
                addTries();

                System.out.print("Enter your PIN: ");
                enteredPin = input.nextInt();
            }
        } catch (InputMismatchException e) {
            if (getTries() == 9) {
                setSuspended();
                CustomerMenu.transfer(App.customers);
            }

            System.out.println("Invalid PIN. Please try again.\n");
            addTries();
            CustomerMenu.transfer(App.customers);
        }


        DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        this.balance -= amount;
        receiver.balance += amount;

        String senderTransaction = "(-) Transfer of " + amount + " to " + receiver.getUsername() +
                " (Card: " + receiver.cardNumber + ") at " + dtf.format(now);
        String receiverTransaction = "(+) Received transfer of " + amount + " from " + this.getUsername() +
                " (Card: " + this.cardNumber + ") at " + dtf.format(now);

        // transaction history
        addTransaction(senderTransaction);
        receiver.addTransaction(receiverTransaction);

        // save
        TransactionDatabase.saveTransaction(this.getUsername(), new Transaction(senderTransaction));
        TransactionDatabase.saveTransaction(receiver.getUsername(), new Transaction(receiverTransaction));

        System.out.println("Transfer successful! Your new balance is " + this.balance + "\n");
        resetTries();
    }




    public void showTransactionHistory() {
        List<Transaction> history = TransactionDatabase.loadTransactions().getOrDefault(this.getUsername(), new ArrayList<>());

        System.out.println("Transaction history: ");
        if (history.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            for (Transaction transaction : history) {
                System.out.println(transaction.getDescription());
            }
        }
        System.out.println("---------------------------------\n");
    }

}