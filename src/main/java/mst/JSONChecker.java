package mst;

import com.google.gson.JsonArray; // Добавляем этот импорт
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Utility to check JSON file structure
 */
public class JSONChecker {

    public static void checkJSONFiles() {
        String[] testFiles = {
                "mst_test_graphs.json",
                "mst_small_graphs.json",
                "mst_medium_graphs.json",
                "mst_large_graphs.json",
                "mst_extra_large_graphs.json"
        };

        for (String filename : testFiles) {
            System.out.println("Checking: " + filename);
            checkJSONFile(filename);
            System.out.println();
        }
    }

    private static void checkJSONFile(String filename) {
        try (InputStream inputStream = Graph.class.getClassLoader().getResourceAsStream(filename)) {
            if (inputStream == null) {
                System.out.println("  ❌ File not found in resources");
                return;
            }

            InputStreamReader reader = new InputStreamReader(inputStream);
            JsonElement jsonElement = JsonParser.parseReader(reader);

            if (jsonElement.isJsonObject() && jsonElement.getAsJsonObject().has("graphs")) {
                System.out.println("  ✅ New format: { \"graphs\": [ ... ] }");
                JsonArray graphsArray = jsonElement.getAsJsonObject().getAsJsonArray("graphs");
                System.out.println("  Number of graphs: " + graphsArray.size());

                // Check first graph structure
                if (graphsArray.size() > 0) {
                    JsonObject firstGraph = graphsArray.get(0).getAsJsonObject();
                    System.out.println("  First graph has:");
                    if (firstGraph.has("id")) System.out.println("    - id: " + firstGraph.get("id"));
                    if (firstGraph.has("nodes")) System.out.println("    - nodes: " + firstGraph.getAsJsonArray("nodes").size() + " nodes");
                    if (firstGraph.has("edges")) System.out.println("    - edges: " + firstGraph.getAsJsonArray("edges").size() + " edges");
                }
            } else if (jsonElement.isJsonArray()) {
                System.out.println("  ✅ Old format: [ ... ] (array of graphs)");
                System.out.println("  Number of graphs: " + jsonElement.getAsJsonArray().size());
            } else {
                System.out.println("  ❌ Unknown format");
            }

        } catch (Exception e) {
            System.out.println("  ❌ Error: " + e.getMessage());
        }
    }
}