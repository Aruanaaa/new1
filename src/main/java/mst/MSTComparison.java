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
        // Используем путь к папке src/output
        java.io.File outputDir = new java.io.File("src/output");
        if (!outputDir.exists()) {
            boolean created = outputDir.mkdirs();
            if (!created) {
                System.err.println("❌ Failed to create output directory: " + outputDir.getAbsolutePath());
                return;
            }
        }

        java.io.File outputFile = new java.io.File(outputDir, "output_results.json");

        System.out.println("📁 Writing to: " + outputFile.getAbsolutePath());

        try (FileWriter writer = new FileWriter(outputFile)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            // Создаем JSON структуру
            JsonArray resultsArray = new JsonArray();

            for (ComparisonResult result : results) {
                JsonObject resultObj = new JsonObject();
                resultObj.addProperty("graphId", result.graphId);
                resultObj.addProperty("vertices", result.vertices);
                resultObj.addProperty("edges", result.edges);

                // Prim results
                JsonObject primObj = new JsonObject();
                primObj.addProperty("totalWeight", result.primWeight);
                primObj.addProperty("executionTimeNs", result.primTime);
                primObj.addProperty("operationsCount", result.primOperations);
                resultObj.add("prim", primObj);

                // Kruskal results
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
            root.addProperty("totalGraphsTested", results.size());
            root.addProperty("comparisonDate", new java.util.Date().toString());

            // Записываем JSON
            gson.toJson(root, writer);
            writer.flush(); // Принудительно сбрасываем буфер

            System.out.println("✅ Results successfully written!");
            System.out.println("📊 File contains results for " + results.size() + " graphs");
            System.out.println("📁 Location: " + outputFile.getAbsolutePath());

            // Проверяем что файл не пустой
            if (outputFile.exists() && outputFile.length() > 0) {
                System.out.println("📏 File size: " + outputFile.length() + " bytes");
            } else {
                System.err.println("❌ File is empty or doesn't exist!");
            }

        } catch (IOException e) {
            System.err.println("❌ Error writing results to JSON: " + e.getMessage());
            e.printStackTrace();

            // Альтернативная попытка записи
            writeBackupResults();
        }
    }

    private void writeBackupResults() {
        try {
            // Пробуем записать в корень проекта как запасной вариант
            java.io.File backupFile = new java.io.File("backup_results.json");
            try (FileWriter writer = new FileWriter(backupFile)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonArray resultsArray = new JsonArray();

                for (ComparisonResult result : results) {
                    JsonObject resultObj = new JsonObject();
                    resultObj.addProperty("graphId", result.graphId);
                    resultObj.addProperty("vertices", result.vertices);
                    resultObj.addProperty("edges", result.edges);
                    resultObj.addProperty("primWeight", result.primWeight);
                    resultObj.addProperty("primTime", result.primTime);
                    resultObj.addProperty("kruskalWeight", result.kruskalWeight);
                    resultObj.addProperty("kruskalTime", result.kruskalTime);
                    resultsArray.add(resultObj);
                }

                gson.toJson(resultsArray, writer);
                writer.flush();
                System.out.println("📁 Backup results written to: " + backupFile.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("❌ Backup also failed: " + e.getMessage());
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

        return String.format(
                "Prim total: %,d ns, %,d ops | Kruskal total: %,d ns, %,d ops | Faster: %s (by %,d ns)",
                primTotalTime, primTotalOps, kruskalTotalTime, kruskalTotalOps, fasterAlgorithm, timeDifference
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