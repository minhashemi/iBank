import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class CustomerMenu {
    static Scanner input = new Scanner(System.in);

    public static void customerMenu() {
        if (BankApp.currentCustomer.getSuspended()) {
            System.out.println("Your account has suspended, please contact administrator!\n");
            BankApp.mainMenu();
            return;
        }

        System.out.println("**** Welcome, " + BankApp.currentCustomer.getName() + "! ****");
        System.out.println("1. Deposit");
        System.out.println("2. Withdrawal");
        System.out.println("3. Transfer");
        System.out.println("4. Transaction History");
        System.out.println("5. Account Information");
        System.out.println("6. Log Out");
        System.out.println("7. Exit App");
        System.out.println("***********************************");

        System.out.print("Enter your choice: ");
        int choice = 0;
        try {
            choice = input.nextInt();

            if (choice < 1 || choice > 7) {
                System.out.println("Invalid choice. Please try again.\n");
                customerMenu();
            }
        } catch (InputMismatchException e) {
            input.nextLine();
            System.out.println("Invalid input. Please try again.\n");
            customerMenu();
        }

        switch (choice) {
            case 1:
                deposit();
                break;
            case 2:
                withdrawal();
                break;
            case 3:
                transfer(BankApp.customers);
                break;
            case 4:
                transactionHistory();
                break;
            case 5:
                accountInformation();
                break;
            case 6:
                logOut();
                break;
            case 7:
                BankApp.exitApp();
                break;
        }
    }

    public static void deposit() {
        System.out.print("Enter the amount to deposit: ");
        double amount = input.nextDouble();

        if (amount <= 0) {
            System.out.println("Invalid amount. Please try again.\n");
            return;
        }

        BankApp.currentCustomer.deposit(amount);
        UserDatabase.updateUserBalance(BankApp.currentCustomer.getUsername(), BankApp.currentCustomer.getBalance());

        customerMenu();
    }



    public static void withdrawal() {
        if (BankApp.currentCustomer.getSuspended()) {
            System.out.println("Your account has been suspended, please contact the administrator!\n");
            BankApp.mainMenu();
            return;
        }

        System.out.print("Enter the amount to withdraw: ");
        double amount = input.nextDouble();

        if (amount > BankApp.currentCustomer.getBalance()) {
            System.out.println("Insufficient funds. Your balance is: " + BankApp.currentCustomer.getBalance());
            return;
        }

        BankApp.currentCustomer.withdrawal(amount);
        UserDatabase.updateUserBalance(BankApp.currentCustomer.getUsername(), BankApp.currentCustomer.getBalance());

        customerMenu();
    }


    public static void transfer(ArrayList<Customer> customers) {
        System.out.print("Enter the card number to transfer to: ");
        int cardNumber = input.nextInt();
        System.out.print("Enter the amount to transfer: ");
        double amount = input.nextDouble();

        // Find receiver
        Customer receiver = null;
        for (Customer customer : customers) {
            if (customer.getCardNumber() == cardNumber) {
                receiver = customer;
                break;
            }
        }

        if (receiver == null) {
            System.out.println("Invalid card number. Please try again.\n");
            customerMenu();
            return;
        }

        // enough balance?
        if (amount > BankApp.currentCustomer.getBalance()) {
            System.out.println("Insufficient funds. Your balance is: " + BankApp.currentCustomer.getBalance());
            return;
        }

        BankApp.currentCustomer.transfer(receiver, amount);
        UserDatabase.updateUserBalance(BankApp.currentCustomer.getUsername(), BankApp.currentCustomer.getBalance());
        UserDatabase.updateUserBalance(receiver.getUsername(), receiver.getBalance());

        customerMenu();
    }


    public static void transactionHistory() {
        if (BankApp.currentCustomer.getSuspended()) {
            System.out.println("Your account has suspended, please contact administrator!\n");
            BankApp.mainMenu();
            return;
        }

        BankApp.currentCustomer.showTransactionHistory();
        customerMenu();
    }

    public static void accountInformation() {
        if (BankApp.currentCustomer.getSuspended()) {
            System.out.println("Your account has suspended, please contact administrator!\n");
            BankApp.mainMenu();
            return;
        }

        System.out.println("**** Account Information ****");
        BankApp.currentCustomer.displayInfo();
        System.out.println("*****************************\n");
        CustomerMenu.customerMenu();
    }

    public static void logOut() {
        System.out.println("Goodbye, " + BankApp.currentCustomer.getName() + "! See you next time.\n");
        BankApp.currentCustomer = null;
        BankApp.mainMenu();
    }
}
