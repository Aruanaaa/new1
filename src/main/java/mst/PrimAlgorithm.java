package mst;

import java.util.*;

/**
 * Implementation of Prim's algorithm for Minimum Spanning Tree
 */
public class PrimAlgorithm {
    private long operationsCount;
    private long executionTime;
    private List<Edge> mstEdges;
    private double totalWeight;

    public MSTResult findMST(Graph graph) {
        operationsCount = 0;
        long startTime = System.nanoTime();
        mstEdges = new ArrayList<>();
        totalWeight = 0.0;

        if (graph.getVertices() == 0) {
            executionTime = System.nanoTime() - startTime;
            return new MSTResult(mstEdges, totalWeight, operationsCount, executionTime);
        }

        boolean[] visited = new boolean[graph.getVertices()];
        double[] minEdge = new double[graph.getVertices()];
        int[] parent = new int[graph.getVertices()];

        Arrays.fill(minEdge, Double.MAX_VALUE);
        Arrays.fill(parent, -1);
        minEdge[0] = 0;
        operationsCount += 3 * graph.getVertices(); // Array fills

        PriorityQueue<Vertex> pq = new PriorityQueue<>(Comparator.comparingDouble(v -> v.weight));
        pq.offer(new Vertex(0, 0));
        operationsCount++;

        while (!pq.isEmpty()) {
            Vertex current = pq.poll();
            operationsCount++;

            int u = current.vertex;
            if (visited[u]) continue;

            visited[u] = true;
            operationsCount++;

            if (parent[u] != -1) {
                mstEdges.add(new Edge(parent[u], u, minEdge[u]));
                totalWeight += minEdge[u];
                operationsCount += 2;
            }

            for (Edge edge : graph.getAdjacencyList().get(u)) {
                int v = edge.getDestination();
                double weight = edge.getWeight();
                operationsCount += 2;

                if (!visited[v] && weight < minEdge[v]) {
                    minEdge[v] = weight;
                    parent[v] = u;
                    pq.offer(new Vertex(v, weight));
                    operationsCount += 4;
                }
            }
        }

        executionTime = System.nanoTime() - startTime;
        return new MSTResult(mstEdges, totalWeight, operationsCount, executionTime);
    }

    private static class Vertex {
        int vertex;
        double weight;

        Vertex(int vertex, double weight) {
            this.vertex = vertex;
            this.weight = weight;
        }
    }

    public static class MSTResult {
        private final List<Edge> edges;
        private final double totalWeight;
        private final long operationsCount;
        private final long executionTime;

        public MSTResult(List<Edge> edges, double totalWeight, long operationsCount, long executionTime) {
            this.edges = new ArrayList<>(edges);
            this.totalWeight = totalWeight;
            this.operationsCount = operationsCount;
            this.executionTime = executionTime;
        }

        public List<Edge> getEdges() { return Collections.unmodifiableList(edges); }
        public double getTotalWeight() { return totalWeight; }
        public long getOperationsCount() { return operationsCount; }
        public long getExecutionTime() { return executionTime; }
    }
}