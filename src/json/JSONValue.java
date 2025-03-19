package json;

public class JSONValue {
    private final Object value;

    public JSONValue(Object value) {
        if (!(value instanceof String || value instanceof Long || value instanceof Double ||
                value instanceof Boolean || value == null || value instanceof JSONArray || value instanceof JSONObject)) {
            throw new IllegalArgumentException("Invalid JSONValue type");
        }
        this.value = value;
    }

    public Object getValue() {
        return value;
    }


    @Override
    public String toString() {
        if (value == null) return "null";
        if (value instanceof String) return "\"" + escape((String) value) + "\"";
        return value.toString();
    }

    // esc quotes and backslashes
    private String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
