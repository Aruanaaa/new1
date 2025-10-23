package mst;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Represents a graph with vertices and edges, provides JSON loading functionality
 */
public class Graph {
    private final int vertices;
    private final List<Edge> edges;
    private final List<List<Edge>> adjacencyList;
    private final Map<String, Integer> nodeToIndex; // Mapping for string node names to indices

    public Graph(int vertices, List<Edge> edges) {
        this.vertices = vertices;
        this.edges = new ArrayList<>(edges);
        this.adjacencyList = createAdjacencyList(vertices, edges);
        this.nodeToIndex = new HashMap<>();
    }

    public Graph(int vertices, List<Edge> edges, Map<String, Integer> nodeToIndex) {
        this.vertices = vertices;
        this.edges = new ArrayList<>(edges);
        this.adjacencyList = createAdjacencyList(vertices, edges);
        this.nodeToIndex = nodeToIndex;
    }

    public int getVertices() { return vertices; }
    public List<Edge> getEdges() { return Collections.unmodifiableList(edges); }
    public List<List<Edge>> getAdjacencyList() { return Collections.unmodifiableList(adjacencyList); }
    public Map<String, Integer> getNodeToIndex() { return Collections.unmodifiableMap(nodeToIndex); }

    private List<List<Edge>> createAdjacencyList(int vertices, List<Edge> edges) {
        List<List<Edge>> adjList = new ArrayList<>();
        for (int i = 0; i < vertices; i++) {
            adjList.add(new ArrayList<>());
        }

        for (Edge edge : edges) {
            adjList.get(edge.getSource()).add(edge);
            // For undirected graph, add reverse edge
            adjList.get(edge.getDestination()).add(
                    new Edge(edge.getDestination(), edge.getSource(), edge.getWeight())
            );
        }
        return adjList;
    }

    /**
     * Load graphs from JSON file in resources folder - supports both formats
     */
    public static List<Graph> loadGraphsFromJson(String filename) {
        List<Graph> graphs = new ArrayList<>();
        Gson gson = new Gson();

        try (InputStream inputStream = Graph.class.getClassLoader().getResourceAsStream(filename);
             InputStreamReader reader = new InputStreamReader(inputStream)) {

            if (inputStream == null) {
                throw new RuntimeException("File not found: " + filename);
            }

            JsonElement jsonElement = gson.fromJson(reader, JsonElement.class);

            if (jsonElement == null) {
                throw new RuntimeException("File is empty or invalid JSON: " + filename);
            }

            if (jsonElement.isJsonObject()) {
                // New format: { "graphs": [ ... ] }
                JsonObject rootObject = jsonElement.getAsJsonObject();
                if (rootObject.has("graphs")) {
                    JsonArray graphsArray = rootObject.getAsJsonArray("graphs");
                    graphs = parseNewFormat(graphsArray);
                } else {
                    throw new RuntimeException("JSON object should contain 'graphs' array");
                }
            } else if (jsonElement.isJsonArray()) {
                // Old format: [ ... ]
                JsonArray graphsArray = jsonElement.getAsJsonArray();
                graphs = parseOldFormat(graphsArray);
            } else {
                throw new RuntimeException("Invalid JSON format in file: " + filename);
            }

            System.out.println("Successfully loaded " + graphs.size() + " graphs from " + filename);

        } catch (Exception e) {
            throw new RuntimeException("Error loading graphs from " + filename + ": " + e.getMessage(), e);
        }

        return graphs;
    }


    private static List<Graph> parseNewFormat(JsonArray graphsArray) {
        List<Graph> graphs = new ArrayList<>();

        for (JsonElement graphElement : graphsArray) {
            JsonObject graphObject = graphElement.getAsJsonObject();

            // Get nodes and create mapping from node name to index
            JsonArray nodesArray = graphObject.getAsJsonArray("nodes");
            Map<String, Integer> nodeToIndex = new HashMap<>();
            List<String> nodeNames = new ArrayList<>();

            for (JsonElement nodeElement : nodesArray) {
                String nodeName = nodeElement.getAsString();
                nodeToIndex.put(nodeName, nodeNames.size());
                nodeNames.add(nodeName);
            }

            int vertices = nodesArray.size();

            // Parse edges
            JsonArray edgesArray = graphObject.getAsJsonArray("edges");
            List<Edge> edges = new ArrayList<>();

            for (JsonElement edgeElement : edgesArray) {
                JsonObject edgeObject = edgeElement.getAsJsonObject();
                String from = edgeObject.get("from").getAsString();
                String to = edgeObject.get("to").getAsString();
                double weight = edgeObject.get("weight").getAsDouble();

                int sourceIndex = nodeToIndex.get(from);
                int destIndex = nodeToIndex.get(to);

                edges.add(new Edge(sourceIndex, destIndex, weight));
            }

            graphs.add(new Graph(vertices, edges, nodeToIndex));
        }

        return graphs;
    }


    private static List<Graph> parseOldFormat(JsonArray graphsArray) {
        List<Graph> graphs = new ArrayList<>();

        for (JsonElement graphElement : graphsArray) {
            JsonObject graphObject = graphElement.getAsJsonObject();
            int vertices = graphObject.get("vertices").getAsInt();

            JsonArray edgesArray = graphObject.getAsJsonArray("edges");
            List<Edge> edges = new ArrayList<>();

            for (JsonElement edgeElement : edgesArray) {
                JsonArray edgeArray = edgeElement.getAsJsonArray();
                int source = edgeArray.get(0).getAsInt();
                int dest = edgeArray.get(1).getAsInt();
                double weight = edgeArray.get(2).getAsDouble();
                edges.add(new Edge(source, dest, weight));
            }

            graphs.add(new Graph(vertices, edges));
        }

        return graphs;
    }

    public int getEdgeCount() {
        return edges.size();
    }

    @Override
    public String toString() {
        return String.format("Graph(V=%d, E=%d)", vertices, edges.size());
    }
}