package json;
import java.util.ArrayList;
import java.util.List;

public class JSONArray {
    private final List<JSONValue> values = new ArrayList<>();

    public void add(JSONValue value) {
        values.add(value);
    }

    public int size() {
        return values.size();
    }

    public JSONValue get(int index) {
        if (index < 0 || index >= values.size()) {
            throw new IndexOutOfBoundsException("Index out of range: " + index);
        }
        return values.get(index);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < values.size(); i++) {
            sb.append(values.get(i));
            if (i < values.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
