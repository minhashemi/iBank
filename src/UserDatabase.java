import java.io.*;
import java.util.ArrayList;
import java.util.List;
import json.*;

public class UserDatabase {
    private static final String FILE_PATH = "users.json";

    public static void saveUsers(List<Customer> newCustomers) {
        List<Customer> existingCustomers = loadUsers();  // Load existing users

        // Merge new users into existing list (avoid duplicates)
        for (Customer newCustomer : newCustomers) {
            boolean exists = false;
            for (Customer existingCustomer : existingCustomers) {
                if (existingCustomer.getUsername().equals(newCustomer.getUsername())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
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
            jsonObj.put("suspended", new JSONValue(customer.getSuspended()));
            jsonArray.add(new JSONValue(jsonObj));
        }

        // Write updated user list to file
        try (FileWriter file = new FileWriter(FILE_PATH)) {
            file.write(jsonArray.toString());
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
                        JSONValue suspendedValue = jsonObj.get("suspended");

                        if (nameValue == null || usernameValue == null || passwordValue == null ||
                                pinValue == null || suspendedValue == null) {
                            continue; // Skip invalid entries
                        }

                        String name = nameValue.getValue().toString();
                        String username = usernameValue.getValue().toString();
                        String password = passwordValue.getValue().toString();
                        int pin = Integer.parseInt(pinValue.getValue().toString());
                        boolean suspended = Boolean.parseBoolean(suspendedValue.getValue().toString());

                        customers.add(new Customer(name, username, password, pin));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Cannot load users");
        }
        return customers;
    }

}
