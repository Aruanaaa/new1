package mst;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


public class CSVExporter {

    /**
     * Export comparison results to CSV file
     */
    public static void exportToCSV(List<MSTComparison.ComparisonResult> results, String filename) {
        // Ensure output directory exists
        java.io.File outputDir = new java.io.File("output");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        java.io.File csvFile = new java.io.File(outputDir, filename);

        try (FileWriter writer = new FileWriter(csvFile)) {
            // Write CSV header
            writer.write("GraphID,Vertices,Edges,GraphDensity,PrimWeight,PrimTimeMs,PrimOperations,KruskalWeight,KruskalTimeMs,KruskalOperations,WeightDifference,PrimFaster\n");

            // Write data rows
            for (MSTComparison.ComparisonResult result : results) {
                double primTimeMs = result.primTime / 1_000_000.0;
                double kruskalTimeMs = result.kruskalTime / 1_000_000.0;
                double density = (double) result.edges / (result.vertices * (result.vertices - 1) / 2.0);
                boolean primFaster = primTimeMs < kruskalTimeMs;

                writer.write(String.format("%d,%d,%d,%.4f,%.2f,%.3f,%d,%.2f,%.3f,%d,%.6f,%s\n",
                        result.graphId,
                        result.vertices,
                        result.edges,
                        density,
                        result.primWeight,
                        primTimeMs,
                        result.primOperations,
                        result.kruskalWeight,
                        kruskalTimeMs,
                        result.kruskalOperations,
                        Math.abs(result.primWeight - result.kruskalWeight),
                        primFaster ? "Yes" : "No"
                ));
            }

            // Add summary row
            writer.write("\n# SUMMARY STATISTICS\n");
            long primTotalTime = results.stream().mapToLong(r -> r.primTime).sum();
            long kruskalTotalTime = results.stream().mapToLong(r -> r.kruskalTime).sum();
            long primTotalOps = results.stream().mapToLong(r -> r.primOperations).sum();
            long kruskalTotalOps = results.stream().mapToLong(r -> r.kruskalOperations).sum();

            writer.write(String.format("Total,,,Prim: %.3f ms, %d ops,,Kruskal: %.3f ms, %d ops,,Prim Faster: %s",
                    primTotalTime / 1_000_000.0, primTotalOps,
                    kruskalTotalTime / 1_000_000.0, kruskalTotalOps,
                    primTotalTime < kruskalTotalTime ? "Yes" : "No"
            ));

            System.out.println("CSV results exported to: " + csvFile.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("Error exporting CSV: " + e.getMessage());
        }
    }


    public static void printSummaryTable(List<MSTComparison.ComparisonResult> results) {
        System.out.println("\n" + "=".repeat(120));
        System.out.println("SUMMARY PERFORMANCE TABLE");
        System.out.println("=".repeat(120));
        System.out.printf("%-8s %-10s %-8s %-12s %-12s %-12s %-12s %-12s%n",
                "Graph", "Vertices", "Edges", "Prim(ms)", "Kruskal(ms)", "Prim Ops", "Kruskal Ops", "Faster");
        System.out.println("-".repeat(120));

        for (MSTComparison.ComparisonResult result : results) {
            double primTimeMs = result.primTime / 1_000_000.0;
            double kruskalTimeMs = result.kruskalTime / 1_000_000.0;
            String faster = primTimeMs < kruskalTimeMs ? "PRIM" : "KRUSKAL";

            System.out.printf("%-8d %-10d %-8d %-12.3f %-12.3f %-12d %-12d %-12s%n",
                    result.graphId, result.vertices, result.edges,
                    primTimeMs, kruskalTimeMs,
                    result.primOperations, result.kruskalOperations,
                    faster);
        }

        // Totals
        long primTotalTime = results.stream().mapToLong(r -> r.primTime).sum();
        long kruskalTotalTime = results.stream().mapToLong(r -> r.kruskalTime).sum();

        System.out.println("-".repeat(120));
        System.out.printf("%-8s %-10s %-8s %-12.3f %-12.3f %-12s %-12s %-12s%n",
                "TOTAL", "", "",
                primTotalTime / 1_000_000.0,
                kruskalTotalTime / 1_000_000.0,
                "", "",
                primTotalTime < kruskalTotalTime ? "PRIM" : "KRUSKAL");
        System.out.println("=".repeat(120));
    }
}