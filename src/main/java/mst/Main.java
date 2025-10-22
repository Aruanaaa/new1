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
                "mst_test_graphs.json"  // Начнем только с тестового файла
        };

        boolean anyFileLoaded = false;

        for (String testFile : testFiles) {
            try {
                System.out.println("Loading: " + testFile);
                List<Graph> graphs = Graph.loadGraphsFromJson(testFile);
                System.out.printf("Loaded %d graphs from %s%n%n", graphs.size(), testFile);

                MSTComparison comparison = new MSTComparison(graphs);
                comparison.runComparison();
                anyFileLoaded = true;

            } catch (Exception e) {
                System.err.println("Failed to process " + testFile + ": " + e.getMessage());
                // Продолжаем с другими файлами
            }
        }

        if (!anyFileLoaded) {
            System.out.println("No JSON files found. Creating test graph manually...");
            runManualTest();
        }

        System.out.println("=== Comparison Complete ===");
    }

    /**
     * Fallback method if no JSON files are found
     */
    private static void runManualTest() {
        // Create a simple test graph manually
        List<Edge> edges = List.of(
                new Edge(0, 1, 2.0),
                new Edge(1, 2, 3.0),
                new Edge(2, 3, 1.0),
                new Edge(0, 3, 4.0),
                new Edge(1, 3, 5.0)
        );

        Graph testGraph = new Graph(4, edges);
        List<Graph> graphs = List.of(testGraph);

        MSTComparison comparison = new MSTComparison(graphs);
        comparison.runComparison();
    }
}