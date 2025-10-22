package mst;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Compares Prim's and Kruskal's algorithms and writes results to JSON
 */
public class MSTComparison {
    private final List<Graph> graphs;
    private final List<ComparisonResult> results;

    public MSTComparison(List<Graph> graphs) {
        this.graphs = graphs;
        this.results = new ArrayList<>();
    }

    public void runComparison() {
        System.out.println("Running MST algorithm comparison...");

        for (int i = 0; i < graphs.size(); i++) {
            Graph graph = graphs.get(i);
            System.out.printf("Testing graph %d: %s%n", i + 1, graph);

            PrimAlgorithm prim = new PrimAlgorithm();
            KruskalAlgorithm kruskal = new KruskalAlgorithm();

            PrimAlgorithm.MSTResult primResult = prim.findMST(graph);
            KruskalAlgorithm.MSTResult kruskalResult = kruskal.findMST(graph);

            ComparisonResult result = new ComparisonResult(
                    i + 1,
                    graph.getVertices(),
                    graph.getEdgeCount(),
                    primResult,
                    kruskalResult
            );

            results.add(result);
            printResult(result);
        }

        writeResultsToJson();
    }

    private void printResult(ComparisonResult result) {
        System.out.printf("Graph %d Results:%n", result.graphId);
        System.out.printf("  Prim:    weight=%.2f, time=%,d ns, operations=%,d%n",
                result.primWeight, result.primTime, result.primOperations);
        System.out.printf("  Kruskal: weight=%.2f, time=%,d ns, operations=%,d%n",
                result.kruskalWeight, result.kruskalTime, result.kruskalOperations);
        System.out.printf("  Weight difference: %.6f%n",
                Math.abs(result.primWeight - result.kruskalWeight));
        System.out.println();
    }

    private void writeResultsToJson() {
        try (FileWriter writer = new FileWriter("output/output_results.json")) {
            // Создаем папку output если её нет
            new java.io.File("output").mkdirs();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonArray resultsArray = new JsonArray();

            for (ComparisonResult result : results) {
                JsonObject resultObj = new JsonObject();
                resultObj.addProperty("graphId", result.graphId);
                resultObj.addProperty("vertices", result.vertices);
                resultObj.addProperty("edges", result.edges);

                JsonObject primObj = new JsonObject();
                primObj.addProperty("totalWeight", result.primWeight);
                primObj.addProperty("executionTimeNs", result.primTime);
                primObj.addProperty("operationsCount", result.primOperations);
                resultObj.add("prim", primObj);

                JsonObject kruskalObj = new JsonObject();
                kruskalObj.addProperty("totalWeight", result.kruskalWeight);
                kruskalObj.addProperty("executionTimeNs", result.kruskalTime);
                kruskalObj.addProperty("operationsCount", result.kruskalOperations);
                resultObj.add("kruskal", kruskalObj);

                resultsArray.add(resultObj);
            }

            JsonObject root = new JsonObject();
            root.add("results", resultsArray);
            root.addProperty("summary", generateSummary());

            gson.toJson(root, writer);
            System.out.println("Results written to output/output_results.json");

        } catch (IOException e) {
            System.err.println("Error writing results to JSON: " + e.getMessage());
        }
    }

    private String generateSummary() {
        long primTotalTime = results.stream().mapToLong(r -> r.primTime).sum();
        long kruskalTotalTime = results.stream().mapToLong(r -> r.kruskalTime).sum();
        long primTotalOps = results.stream().mapToLong(r -> r.primOperations).sum();
        long kruskalTotalOps = results.stream().mapToLong(r -> r.kruskalOperations).sum();

        return String.format(
                "Prim total time: %,d ns, Kruskal total time: %,d ns. " +
                        "Prim total operations: %,d, Kruskal total operations: %,d",
                primTotalTime, kruskalTotalTime, primTotalOps, kruskalTotalOps
        );
    }

    private static class ComparisonResult {
        final int graphId;
        final int vertices;
        final int edges;
        final double primWeight;
        final long primTime;
        final long primOperations;
        final double kruskalWeight;
        final long kruskalTime;
        final long kruskalOperations;

        ComparisonResult(int graphId, int vertices, int edges,
                         PrimAlgorithm.MSTResult primResult,
                         KruskalAlgorithm.MSTResult kruskalResult) {
            this.graphId = graphId;
            this.vertices = vertices;
            this.edges = edges;
            this.primWeight = primResult.getTotalWeight();
            this.primTime = primResult.getExecutionTime();
            this.primOperations = primResult.getOperationsCount();
            this.kruskalWeight = kruskalResult.getTotalWeight();
            this.kruskalTime = kruskalResult.getExecutionTime();
            this.kruskalOperations = kruskalResult.getOperationsCount();
        }
    }
}