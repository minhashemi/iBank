package json;

import java.util.LinkedHashMap;
import java.util.Map;

public class JSONObject {
    private final Map<String, JSONValue> map = new LinkedHashMap<>();

    // Add key-value pair
    public void put(String key, JSONValue value) {
        map.put(key, value);
    }

    // Get value by key (returns JSONValue or null)
    public JSONValue get(String key) {
        return map.getOrDefault(key, null);
    }

    // Get number of key-value pairs
    public int size() {
        return map.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (var entry : map.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(entry.getKey()).append("\":").append(entry.getValue());
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
}
