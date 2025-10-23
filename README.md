# City Transportation Network Optimization

# Project Overview
This project implements Prim's and Kruskal's algorithms to find Minimum Spanning Trees for city transportation networks. The goal is to determine the optimal set of roads connecting all districts with minimal construction cost.

# Features
- Prim's Algorithm implementation with performance metrics
- Kruskal's Algorithm implementation with Union-Find
- JSON-based input/output system
- Comprehensive testing suite
- Performance comparison and analysis

# Project Structure
src/
├── main/java/mst/
│ ├── Graph.java # Graph data structure
│ ├── Edge.java # Edge representation
│ ├── PrimAlgorithm.java # Prim's algorithm
│ ├── KruskalAlgorithm.java # Kruskal's algorithm
│ ├── MSTComparison.java # Algorithm comparison
│ └── Main.java # Entry point
├── test/java/mst/
│ └── MSTAlgorithmsTest.java # Unit tests
└── resources/
├── mst_test_graphs.json
├── mst_small_graphs.json
├── mst_medium_graphs.json
├── mst_large_graphs.json
└── mst_extra_large_graphs.json


# How to Run
1. **Compile and run**:mvn compile exec:java -Dexec.mainClass="mst.Main"`
2. **Run tests**:mvn test
3. **View results**: Check `output/output_results.json`

# Input Format
JSON files should contain graphs in this format:
```json
{
  "graphs": [
    {
      "id": 1,
      "nodes": ["A", "B", "C"],
      "edges": [
        {"from": "A", "to": "B", "weight": 2.0},
        {"from": "B", "to": "C", "weight": 3.0}
      ]
    }
  ]
}


