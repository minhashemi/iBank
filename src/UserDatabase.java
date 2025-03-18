import java.io.*;
import java.util.ArrayList;
import java.util.List;
import json.*;

public class UserDatabase {
    private static final String FILE_PATH = "users.json";

    public static void updateUserBalance(String username, double newBalance) {
        List<Customer> customers = loadUsers();  // Load all users

        for (Customer customer : customers) {
            if (customer.getUsername().equals(username)) {
                customer.setBalance(newBalance); // Update balance
                break;
            }
        }

        saveUsers(customers);  // Save updated user list to JSON
    }


    public static void saveUsers(List<Customer> newCustomers) {
        List<Customer> existingCustomers = loadUsers();  // Load existing users

        // ✅ Update existing users with new data
        for (Customer newCustomer : newCustomers) {
            boolean updated = false;
            for (Customer existingCustomer : existingCustomers) {
                if (existingCustomer.getUsername().equals(newCustomer.getUsername())) {
                    // ✅ Replace existing customer with updated data
                    existingCustomer.setBalance(newCustomer.getBalance());
                    updated = true;
                    break;
                }
            }
            // If user was not found in existing list, add as a new user
            if (!updated) {
                existingCustomers.add(newCustomer);
            }
        }

        // Convert to JSONArray
        JSONArray jsonArray = new JSONArray();
        for (Customer customer : existingCustomers) {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("name", new JSONValue(customer.getName()));
            jsonObj.put("username", new JSONValue(customer.getUsername()));
            jsonObj.put("password", new JSONValue(customer.getPassword()));
            jsonObj.put("pin", new JSONValue((long) customer.getPin()));
            jsonObj.put("card", new JSONValue((long) customer.getCardNumber()));
            jsonObj.put("balance", new JSONValue(customer.getBalance()));
            jsonArray.add(new JSONValue(jsonObj));
        }

        try (FileWriter file = new FileWriter(FILE_PATH)) {
            file.write(jsonArray.toString());
//            System.out.println("[DEBUG] User data saved successfully!");  // Debug log
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    public static List<Customer> loadUsers() {
        List<Customer> customers = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return customers; // No database file, return empty list

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            JSONValue parsedValue = JSONParser.parse(jsonContent.toString());
            if (parsedValue.getValue() instanceof JSONArray jsonArray) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONValue value = jsonArray.get(i);
                    if (value.getValue() instanceof JSONObject jsonObj) {
                        JSONValue nameValue = jsonObj.get("name");
                        JSONValue usernameValue = jsonObj.get("username");
                        JSONValue passwordValue = jsonObj.get("password");
                        JSONValue pinValue = jsonObj.get("pin");
                        JSONValue cardValue = jsonObj.get("card");
                        JSONValue balanceValue = jsonObj.get("balance");

                        if (nameValue == null || usernameValue == null || passwordValue == null ||
                                pinValue == null) {
                            continue; // Skip invalid entries
                        }

                        String name = nameValue.getValue().toString();
                        String username = usernameValue.getValue().toString();
                        String password = passwordValue.getValue().toString();
                        int pin = Integer.parseInt(pinValue.getValue().toString());
                        int card = Integer.parseInt(cardValue.getValue().toString());
                        double balance = Double.parseDouble(balanceValue.getValue().toString());

                        customers.add(new Customer(name, username, password, pin, balance, card));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Cannot load users");
        }
        return customers;
    }

}
