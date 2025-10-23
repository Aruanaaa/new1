package mst;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== MST Algorithm Comparison ===\n");

        // First, check JSON files structure
        System.out.println("Checking JSON files structure...");
        JSONChecker.checkJSONFiles();
        System.out.println();

        // Then try to load and process
        String[] testFiles = {
                "mst_test_graphs.json",
                "mst_small_graphs.json",
                "mst_medium_graphs.json",
                "mst_large_graphs.json",
                "mst_extra_large_graphs.json"
        };

        boolean anyFileLoaded = false;

        for (String testFile : testFiles) {
            try {
                System.out.println("Loading: " + testFile);
                List<Graph> graphs = Graph.loadGraphsFromJson(testFile);
                System.out.printf("Successfully loaded %d graphs from %s%n%n", graphs.size(), testFile);

                MSTComparison comparison = new MSTComparison(graphs);
                comparison.runComparison();
                anyFileLoaded = true;

            } catch (Exception e) {
                System.err.println("Failed to process " + testFile + ": " + e.getMessage());
            }
        }

        if (!anyFileLoaded) {
            System.out.println("No JSON files could be loaded. Creating test graph manually...");
            runManualTest();
        }

        System.out.println("=== Comparison Complete ===");
    }

    private static void runManualTest() {
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