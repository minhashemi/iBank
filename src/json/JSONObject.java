package json;

import java.util.LinkedHashMap;
import java.util.Map;

public class JSONObject {
    private final Map<String, JSONValue> map = new LinkedHashMap<>();

    // Add KV
    public void put(String key, JSONValue value) {
        map.put(key, value);
    }

    // get V from K
    public JSONValue get(String key) {
        return map.getOrDefault(key, null);
    }

    // KV cnt
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
