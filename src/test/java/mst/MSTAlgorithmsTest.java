package mst;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


class MSTAlgorithmsTest {

    @Test
    void testSmallGraph() {
        Graph graph = createSmallGraph();

        PrimAlgorithm prim = new PrimAlgorithm();
        KruskalAlgorithm kruskal = new KruskalAlgorithm();

        PrimAlgorithm.MSTResult primResult = prim.findMST(graph);
        KruskalAlgorithm.MSTResult kruskalResult = kruskal.findMST(graph);

        // Both algorithms should find same total weight
        assertEquals(primResult.getTotalWeight(), kruskalResult.getTotalWeight(), 0.001);

        // MST should have V-1 edges
        assertEquals(graph.getVertices() - 1, primResult.getEdges().size());
        assertEquals(graph.getVertices() - 1, kruskalResult.getEdges().size());

        // Execution time should be positive
        assertTrue(primResult.getExecutionTime() > 0);
        assertTrue(kruskalResult.getExecutionTime() > 0);
    }

    @Test
    void testMSTProperties() {
        Graph graph = createTestGraph();

        PrimAlgorithm prim = new PrimAlgorithm();
        PrimAlgorithm.MSTResult result = prim.findMST(graph);

        // Check MST properties
        assertEquals(graph.getVertices() - 1, result.getEdges().size());
        assertTrue(result.getTotalWeight() > 0);

        // Check no duplicate edges
        List<Edge> edges = result.getEdges();
        for (int i = 0; i < edges.size(); i++) {
            for (int j = i + 1; j < edges.size(); j++) {
                assertNotEquals(edges.get(i), edges.get(j));
            }
        }
    }

    @Test
    void testGraphLoading() {
        List<Graph> graphs = Graph.loadGraphsFromJson("mst_test_graphs.json");
        assertNotNull(graphs);
        assertFalse(graphs.isEmpty());

        for (Graph graph : graphs) {
            assertTrue(graph.getVertices() > 0);
            assertFalse(graph.getEdges().isEmpty());
        }
    }

    @Test
    void testReproducibility() {
        Graph graph = createTestGraph();

        PrimAlgorithm prim = new PrimAlgorithm();
        PrimAlgorithm.MSTResult result1 = prim.findMST(graph);
        PrimAlgorithm.MSTResult result2 = prim.findMST(graph);

        // Same algorithm should produce same results on same input
        assertEquals(result1.getTotalWeight(), result2.getTotalWeight(), 0.001);
        assertEquals(result1.getEdges().size(), result2.getEdges().size());
    }

    private Graph createSmallGraph() {
        List<Edge> edges = List.of(
                new Edge(0, 1, 2.0),
                new Edge(1, 2, 3.0),
                new Edge(0, 2, 1.0),
                new Edge(1, 3, 4.0),
                new Edge(2, 3, 5.0)
        );
        return new Graph(4, edges);
    }

    private Graph createTestGraph() {
        List<Edge> edges = List.of(
                new Edge(0, 1, 4.0),
                new Edge(0, 2, 8.0),
                new Edge(1, 2, 11.0),
                new Edge(1, 3, 8.0),
                new Edge(2, 4, 7.0),
                new Edge(2, 3, 1.0),
                new Edge(3, 4, 2.0),
                new Edge(3, 5, 6.0),
                new Edge(4, 5, 4.0)
        );
        return new Graph(6, edges);
    }
}