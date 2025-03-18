package json;

import java.util.regex.*;

public class JSONParser {
    public static JSONValue parse(String json) {
        json = json.trim();
        if (json.startsWith("{")) {
            return new JSONValue(parseObject(json));
        } else if (json.startsWith("[")) {
            return new JSONValue(parseArray(json));
        } else {
            throw new IllegalArgumentException("Invalid JSON format!");
        }
    }

    private static JSONObject parseObject(String json) {
        JSONObject jsonObj = new JSONObject();
        json = json.trim();
        if (json.equals("{}")) return jsonObj;

        json = json.substring(1, json.length() - 1).trim(); // Remove {}

        Pattern pattern = Pattern.compile("\"(.*?)\"\\s*:\\s*(.*?)(,|$)");
        Matcher matcher = pattern.matcher(json);

        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2).trim();

            jsonObj.put(key, parseValue(value));
        }
        return jsonObj;
    }

    private static JSONArray parseArray(String json) {
        JSONArray jsonArray = new JSONArray();
        json = json.trim();
        if (json.equals("[]")) return jsonArray;

        json = json.substring(1, json.length() - 1).trim(); // Remove []

        int start = 0;
        int braces = 0;
        int brackets = 0;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);

            if (c == '{') braces++;
            if (c == '}') braces--;
            if (c == '[') brackets++;
            if (c == ']') brackets--;

            boolean isCommaSeparator = (c == ',' && braces == 0 && brackets == 0);

            if (isCommaSeparator || i == json.length() - 1) {
                String value = json.substring(start, isCommaSeparator ? i : i + 1).trim();
                if (!value.isEmpty()) {
                    jsonArray.add(parseValue(value));
                }
                start = i + 1;
            }
        }
        return jsonArray;
    }


    private static JSONValue parseValue(String value) {
        value = value.trim();

        if (value.startsWith("\"") && value.endsWith("\"")) {
            return new JSONValue(value.substring(1, value.length() - 1)); // String
        } else if (value.equals("true") || value.equals("false")) {
            return new JSONValue(Boolean.parseBoolean(value)); // Boolean
        } else if (value.equals("null")) {
            return new JSONValue(null); // Null
        } else if (value.matches("-?\\d+")) {
            return new JSONValue(Long.parseLong(value)); // Integer
        } else if (value.matches("-?\\d+(\\.\\d+)?")) {
            return new JSONValue(Double.parseDouble(value)); // Float/Double
        } else if (value.startsWith("{") && value.endsWith("}")) {
            return new JSONValue(parseObject(value)); // JSON Object
        } else if (value.startsWith("[") && value.endsWith("]")) {
            return new JSONValue(parseArray(value)); // JSON Array
        } else {
            throw new IllegalArgumentException("Invalid JSON value: " + value);
        }
    }
}
