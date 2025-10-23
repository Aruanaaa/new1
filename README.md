# City Transportation Network Optimization

## Project Overview
This project implements Prim's and Kruskal's algorithms to optimize city transportation networks by finding Minimum Spanning Trees with minimal construction costs.

## 🚀 Features
- **Prim's Algorithm** with priority queue optimization
- **Kruskal's Algorithm** with Union-Find data structure
- **JSON-based** input/output system
- **Performance metrics**: execution time, operation counts
- **Visualization**: GraphViz DOT file generation
- **Comprehensive testing** with JUnit

## 📁 Project Structure
src/
├── main/java/mst/
│ ├── Graph.java # Custom graph data structure
│ ├── Edge.java # Edge representation
│ ├── PrimAlgorithm.java # Prim's algorithm implementation
│ ├── KruskalAlgorithm.java # Kruskal's algorithm with Union-Find
│ ├── MSTComparison.java # Algorithm comparison and analysis
│ ├── CSVExporter.java # CSV results export
│ ├── GraphVisualizer.java # GraphViz visualization
│ └── Main.java # Application entry point
├── test/java/mst/
│ └── MSTAlgorithmsTest.java # Comprehensive test suite
└── resources/
├── mst_test_graphs.json
├── mst_small_graphs.json
├── mst_medium_graphs.json
├── mst_large_graphs.json
└── mst_extra_large_graphs.json


## 🛠️ How to Run

### Prerequisites
- Java 11+
- Maven
- GraphViz (optional, for visualization)

### Running the Application
```bash
# Compile and run
mvn compile exec:java -Dexec.mainClass="mst.Main"

# Or run directly from IDE
# Run Main.java from src/main/java/mst/


Running Tests

mvn test

Output Files
output/output_results.json - Detailed results with MST edges

output/comparison_results.csv - Performance comparison table

output/*.dot - GraphViz files for visualization