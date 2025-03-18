import java.io.*;
import java.util.*;
import json.*;

public class TransactionDatabase {
    private static final String FILE_PATH = "history.json";

    public static void saveTransaction(String username, Transaction transaction) {
        Map<String, List<Transaction>> transactions = loadTransactions();

        transactions.putIfAbsent(username, new ArrayList<>());
        transactions.get(username).add(transaction);

        saveTransactions(transactions);
    }

    private static void saveTransactions(Map<String, List<Transaction>> transactions) {
        JSONArray jsonArray = new JSONArray();
        for (Map.Entry<String, List<Transaction>> entry : transactions.entrySet()) {
            JSONObject userTransactions = new JSONObject();
            userTransactions.put("username", new JSONValue(entry.getKey()));

            JSONArray transactionList = new JSONArray();
            for (Transaction t : entry.getValue()) {
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("description", new JSONValue(t.getDescription()));
                transactionList.add(new JSONValue(jsonObj));
            }

            userTransactions.put("transactions", new JSONValue(transactionList));
            jsonArray.add(new JSONValue(userTransactions));
        }

        try (FileWriter file = new FileWriter(FILE_PATH)) {
            file.write(jsonArray.toString());
        } catch (IOException e) {
            System.out.println("Error saving transactions: " + e.getMessage());
        }
    }

    public static Map<String, List<Transaction>> loadTransactions() {
        Map<String, List<Transaction>> transactions = new HashMap<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return transactions;

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
                        JSONValue usernameValue = jsonObj.get("username");
                        JSONValue transactionsValue = jsonObj.get("transactions");

                        if (usernameValue == null || transactionsValue == null) continue;

                        String username = usernameValue.getValue().toString();
                        List<Transaction> userTransactions = new ArrayList<>();

                        if (transactionsValue.getValue() instanceof JSONArray transArray) {
                            for (int j = 0; j < transArray.size(); j++) {
                                JSONValue transValue = transArray.get(j);
                                if (transValue.getValue() instanceof JSONObject transObj) {
                                    JSONValue descValue = transObj.get("description");
                                    if (descValue != null) {
                                        userTransactions.add(new Transaction(descValue.getValue().toString()));
                                    }
                                }
                            }
                        }

                        transactions.put(username, userTransactions);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading transactions: " + e.getMessage());
        }
        return transactions;
    }
}
