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

    public Graph(int vertices, List<Edge> edges) {
        this.vertices = vertices;
        this.edges = new ArrayList<>(edges);
        this.adjacencyList = createAdjacencyList(vertices, edges);
    }

    public int getVertices() { return vertices; }
    public List<Edge> getEdges() { return Collections.unmodifiableList(edges); }
    public List<List<Edge>> getAdjacencyList() { return Collections.unmodifiableList(adjacencyList); }

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
     * Load graphs from JSON file in resources folder
     */
    public static List<Graph> loadGraphsFromJson(String filename) {
        List<Graph> graphs = new ArrayList<>();
        Gson gson = new Gson();

        try (InputStream inputStream = Graph.class.getClassLoader().getResourceAsStream(filename)) {
            if (inputStream == null) {
                throw new RuntimeException("File not found in resources: " + filename +
                        ". Make sure the file exists in src/main/resources");
            }

            InputStreamReader reader = new InputStreamReader(inputStream);
            JsonArray graphsArray = gson.fromJson(reader, JsonArray.class);

            if (graphsArray == null) {
                throw new RuntimeException("Invalid JSON format in file: " + filename);
            }

            for (JsonElement graphElement : graphsArray) {
                JsonObject graphObject = graphElement.getAsJsonObject();
                int vertices = graphObject.get("vertices").getAsInt();

                JsonArray edgesArray = graphObject.get("edges").getAsJsonArray();
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

            System.out.println("Successfully loaded " + graphs.size() + " graphs from " + filename);

        } catch (Exception e) {
            throw new RuntimeException("Error loading graphs from " + filename + ": " + e.getMessage(), e);
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