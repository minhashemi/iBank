package json;

import java.util.Objects;

public class JSONValue {
    private final Object value;

    public JSONValue(Object value) {
        if (!(value instanceof String || value instanceof Long || value instanceof Double ||
                value instanceof Boolean || value == null || value instanceof JSONArray || value instanceof JSONObject)) {
            throw new IllegalArgumentException("Invalid JSONValue type");
        }
        this.value = value;
    }

    // Returns the stored value
    public Object getValue() {
        return value;
    }

    // Type checking utility
    public boolean isType(Class<?> type) {
        return type.isInstance(value);
    }

    @Override
    public String toString() {
        if (value == null) return "null";
        if (value instanceof String) return "\"" + escape((String) value) + "\"";
        return value.toString();
    }

    // Escape quotes and backslashes in JSON strings
    private String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
