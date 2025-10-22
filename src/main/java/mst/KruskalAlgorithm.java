package mst;

import java.util.*;

/**
 * Implementation of Kruskal's algorithm with Union-Find data structure
 */
public class KruskalAlgorithm {
    private long operationsCount;
    private long executionTime;

    public MSTResult findMST(Graph graph) {
        operationsCount = 0;
        long startTime = System.nanoTime();

        List<Edge> mstEdges = new ArrayList<>();
        double totalWeight = 0.0;

        // Sort edges by weight
        List<Edge> sortedEdges = new ArrayList<>(graph.getEdges());
        Collections.sort(sortedEdges);
        operationsCount += sortedEdges.size() * (long)(Math.log(sortedEdges.size()) / Math.log(2)); // Sort operations

        UnionFind uf = new UnionFind(graph.getVertices());
        operationsCount += graph.getVertices(); // UnionFind initialization

        for (Edge edge : sortedEdges) {
            operationsCount++;
            int root1 = uf.find(edge.getSource());
            int root2 = uf.find(edge.getDestination());
            operationsCount += 2;

            if (root1 != root2) {
                mstEdges.add(edge);
                totalWeight += edge.getWeight();
                uf.union(root1, root2);
                operationsCount += 3;
            }

            if (mstEdges.size() == graph.getVertices() - 1) break;
        }

        executionTime = System.nanoTime() - startTime;
        return new MSTResult(mstEdges, totalWeight, operationsCount, executionTime);
    }

    /**
     * Union-Find (Disjoint Set Union) data structure with path compression and union by rank
     */
    private static class UnionFind {
        private final int[] parent;
        private final int[] rank;

        public UnionFind(int size) {
            parent = new int[size];
            rank = new int[size];
            for (int i = 0; i < size; i++) {
                parent[i] = i;
            }
        }

        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]); // Path compression
            }
            return parent[x];
        }

        public void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX != rootY) {
                // Union by rank
                if (rank[rootX] < rank[rootY]) {
                    parent[rootX] = rootY;
                } else if (rank[rootX] > rank[rootY]) {
                    parent[rootY] = rootX;
                } else {
                    parent[rootY] = rootX;
                    rank[rootX]++;
                }
            }
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