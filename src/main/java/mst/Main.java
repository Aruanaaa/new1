package mst;

import java.util.List;

/**
 * Main class to run MST algorithms on all test graphs
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== MST Algorithm Comparison ===\n");

        // Load all test graphs
        String[] testFiles = {
                "mst_small_graphs.json",
                "mst_medium_graphs.json",
                "mst_large_graphs.json",
                "mst_extra_large_graphs.json",
                "mst_test_graphs.json"
        };

        for (String testFile : testFiles) {
            try {
                System.out.println("Loading: " + testFile);
                List<Graph> graphs = Graph.loadGraphsFromJson(testFile);
                System.out.printf("Loaded %d graphs from %s%n%n", graphs.size(), testFile);

                MSTComparison comparison = new MSTComparison(graphs);
                comparison.runComparison();

            } catch (Exception e) {
                System.err.println("Failed to process " + testFile + ": " + e.getMessage());
            }
        }

        System.out.println("=== Comparison Complete ===");
    }
}