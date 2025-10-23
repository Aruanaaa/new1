package mst;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class MSTComparison {
    private final List<Graph> graphs;
    private final List<ComparisonResult> results;

    public MSTComparison(List<Graph> graphs) {
        this.graphs = graphs;
        this.results = new ArrayList<>();
    }

    public void runComparison() {
        System.out.println("Running MST algorithm comparison...");

        PrimAlgorithm prim = new PrimAlgorithm();
        KruskalAlgorithm kruskal = new KruskalAlgorithm();

        for (int i = 0; i < graphs.size(); i++) {
            Graph graph = graphs.get(i);
            System.out.printf("Testing graph %d: %s%n", i + 1, graph);

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

            // Print MST edges for first graph for verificationn
            if (i == 0) {
                printMSTDetails(graph, primResult, kruskalResult);
            }
        }

        // Generate outputs
        writeResultsToJson();
        CSVExporter.exportToCSV(results, "comparison_results.csv");
        CSVExporter.printSummaryTable(results);

        // Generate visualizations for smaller graphs
        List<Graph> smallGraphs = graphs.stream()
                .filter(g -> g.getVertices() <= 50)
                .limit(3)
                .collect(java.util.stream.Collectors.toList());

        if (!smallGraphs.isEmpty()) {
            GraphVisualizer.visualizeGraphs(smallGraphs, prim, kruskal);
        }
    }

    private void printResult(ComparisonResult result) {
        System.out.printf("Graph %d Results:%n", result.graphId);
        System.out.printf("  Prim:    weight=%.2f, time=%,d ns (%.3f ms), operations=%,d%n",
                result.primWeight, result.primTime, result.primTime / 1_000_000.0, result.primOperations);
        System.out.printf("  Kruskal: weight=%.2f, time=%,d ns (%.3f ms), operations=%,d%n",
                result.kruskalWeight, result.kruskalTime, result.kruskalTime / 1_000_000.0, result.kruskalOperations);
        System.out.printf("  Weight difference: %.6f%n",
                Math.abs(result.primWeight - result.kruskalWeight));
        System.out.println();
    }

    private void printMSTDetails(Graph graph, PrimAlgorithm.MSTResult primResult, KruskalAlgorithm.MSTResult kruskalResult) {
        System.out.println("=== MST Details (First Graph) ===");
        System.out.printf("Prim's MST (%d edges, weight=%.2f):%n",
                primResult.getEdges().size(), primResult.getTotalWeight());
        for (Edge edge : primResult.getEdges()) {
            System.out.printf("  %d - %d : %.2f%n", edge.getSource(), edge.getDestination(), edge.getWeight());
        }

        System.out.printf("Kruskal's MST (%d edges, weight=%.2f):%n",
                kruskalResult.getEdges().size(), kruskalResult.getTotalWeight());
        for (Edge edge : kruskalResult.getEdges()) {
            System.out.printf("  %d - %d : %.2f%n", edge.getSource(), edge.getDestination(), edge.getWeight());
        }
        System.out.println();
    }

    private void writeResultsToJson() {
        java.io.File outputDir = new java.io.File("output");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        java.io.File outputFile = new java.io.File(outputDir, "output_results.json");

        System.out.println("Writing JSON results to: " + outputFile.getAbsolutePath());

        try (FileWriter writer = new FileWriter(outputFile)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            JsonArray resultsArray = new JsonArray();

            for (ComparisonResult result : results) {
                JsonObject resultObj = new JsonObject();
                resultObj.addProperty("graphId", result.graphId);
                resultObj.addProperty("vertices", result.vertices);
                resultObj.addProperty("edges", result.edges);

                // Prim results with edges list
                JsonObject primObj = new JsonObject();
                primObj.addProperty("totalWeight", result.primWeight);
                primObj.addProperty("executionTimeNs", result.primTime);
                primObj.addProperty("executionTimeMs", result.primTime / 1_000_000.0);
                primObj.addProperty("operationsCount", result.primOperations);

                // Add Prim MST edges array
                JsonArray primEdgesArray = new JsonArray();
                for (Edge edge : result.primResult.getEdges()) {
                    JsonArray edgeArray = new JsonArray();
                    edgeArray.add(edge.getSource());
                    edgeArray.add(edge.getDestination());
                    edgeArray.add(edge.getWeight());
                    primEdgesArray.add(edgeArray);
                }
                primObj.add("mstEdges", primEdgesArray);

                resultObj.add("prim", primObj);

                // Kruskal results with edges list
                JsonObject kruskalObj = new JsonObject();
                kruskalObj.addProperty("totalWeight", result.kruskalWeight);
                kruskalObj.addProperty("executionTimeNs", result.kruskalTime);
                kruskalObj.addProperty("executionTimeMs", result.kruskalTime / 1_000_000.0);
                kruskalObj.addProperty("operationsCount", result.kruskalOperations);

                // Add Kruskal MST edges array
                JsonArray kruskalEdgesArray = new JsonArray();
                for (Edge edge : result.kruskalResult.getEdges()) {
                    JsonArray edgeArray = new JsonArray();
                    edgeArray.add(edge.getSource());
                    edgeArray.add(edge.getDestination());
                    edgeArray.add(edge.getWeight());
                    kruskalEdgesArray.add(edgeArray);
                }
                kruskalObj.add("mstEdges", kruskalEdgesArray);

                resultObj.add("kruskal", kruskalObj);

                resultsArray.add(resultObj);
            }

            JsonObject root = new JsonObject();
            root.add("results", resultsArray);
            root.addProperty("summary", generateSummary());
            root.addProperty("totalGraphsTested", results.size());
            root.addProperty("comparisonDate", new java.util.Date().toString());
            root.addProperty("algorithmComparison", getAlgorithmComparison());

            gson.toJson(root, writer);
            writer.flush();

            System.out.println("JSON results successfully written!");
            System.out.println("Contains results for " + results.size() + " graphs");
            System.out.println("File size: " + outputFile.length() + " bytes");

        } catch (IOException e) {
            System.err.println("Error writing results to JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String generateSummary() {
        if (results.isEmpty()) {
            return "No results to summarize";
        }

        long primTotalTime = results.stream().mapToLong(r -> r.primTime).sum();
        long kruskalTotalTime = results.stream().mapToLong(r -> r.kruskalTime).sum();
        long primTotalOps = results.stream().mapToLong(r -> r.primOperations).sum();
        long kruskalTotalOps = results.stream().mapToLong(r -> r.kruskalOperations).sum();

        String fasterAlgorithm = primTotalTime < kruskalTotalTime ? "Prim" : "Kruskal";
        long timeDifference = Math.abs(primTotalTime - kruskalTotalTime);
        double speedup = (double) Math.max(primTotalTime, kruskalTotalTime) / Math.min(primTotalTime, kruskalTotalTime);

        return String.format(
                "Prim: %,d ns (%.3f ms), %,d ops | Kruskal: %,d ns (%.3f ms), %,d ops | Faster: %s (%.2fx speedup)",
                primTotalTime, primTotalTime / 1_000_000.0, primTotalOps,
                kruskalTotalTime, kruskalTotalTime / 1_000_000.0, kruskalTotalOps,
                fasterAlgorithm, speedup
        );
    }

    private String getAlgorithmComparison() {
        long primWins = results.stream()
                .filter(r -> r.primTime < r.kruskalTime)
                .count();
        long kruskalWins = results.size() - primWins;

        return String.format("Prim faster in %d/%d cases (%.1f%%)",
                primWins, results.size(), (primWins * 100.0 / results.size()));
    }

    // Public static class to allow access from CSVExporter
    public static class ComparisonResult {
        public final int graphId;
        public final int vertices;
        public final int edges;
        public final double primWeight;
        public final long primTime;
        public final long primOperations;
        public final double kruskalWeight;
        public final long kruskalTime;
        public final long kruskalOperations;
        public final PrimAlgorithm.MSTResult primResult;
        public final KruskalAlgorithm.MSTResult kruskalResult;

        public ComparisonResult(int graphId, int vertices, int edges,
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
            this.primResult = primResult;
            this.kruskalResult = kruskalResult;
        }
    }
}