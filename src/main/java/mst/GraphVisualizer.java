package mst;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Generates GraphViz DOT files for visualizing graphs and MST results
 */
public class GraphVisualizer {

    /**
     * Generate DOT file for GraphViz visualization
     */
    public static void generateDOT(Graph graph, List<Edge> mstEdges, String filename, String graphName) {
        // Ensure output directory exists
        java.io.File outputDir = new java.io.File("output");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        java.io.File dotFile = new java.io.File(outputDir, filename);

        try (FileWriter writer = new FileWriter(dotFile)) {
            writer.write("// DOT file generated for MST Visualization\n");
            writer.write("// Use: https://edotor.net/ or install GraphViz locally\n");
            writer.write("digraph " + graphName + " {\n");
            writer.write("  rankdir=LR;\n");
            writer.write("  node [shape=circle, style=filled, fillcolor=lightblue, fontname=Arial];\n");
            writer.write("  edge [fontname=Arial, fontsize=10];\n\n");

            // Write all original edges in light gray
            writer.write("  // Original graph edges (light gray)\n");
            for (Edge edge : graph.getEdges()) {
                writer.write(String.format("  %d -> %d [label=\"%.1f\", color=gray, penwidth=1, style=dashed];\n",
                        edge.getSource(), edge.getDestination(), edge.getWeight()));
            }

            // Write MST edges in bold red
            writer.write("\n  // Minimum Spanning Tree edges (bold red)\n");
            for (Edge edge : mstEdges) {
                writer.write(String.format("  %d -> %d [label=\"%.1f\", color=red, penwidth=3.0, style=solid];\n",
                        edge.getSource(), edge.getDestination(), edge.getWeight()));
            }

            writer.write("}\n");
            System.out.println("✅ DOT file generated: " + dotFile.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("❌ Error generating DOT file: " + e.getMessage());
        }
    }

    /**
     * Generate simplified DOT file for large graphs (shows only MST)
     */
    public static void generateSimpleDOT(Graph graph, List<Edge> mstEdges, String filename, String graphName) {
        java.io.File outputDir = new java.io.File("output");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        java.io.File dotFile = new java.io.File(outputDir, filename);

        try (FileWriter writer = new FileWriter(dotFile)) {
            writer.write("// Simplified DOT file - MST only\n");
            writer.write("graph " + graphName + " {\n");
            writer.write("  layout=fdp;\n");
            writer.write("  node [shape=circle, style=filled, fillcolor=lightblue];\n");
            writer.write("  edge [fontsize=8];\n\n");

            // Write only MST edges
            for (Edge edge : mstEdges) {
                writer.write(String.format("  %d -- %d [label=\"%.1f\", color=red, penwidth=2.0];\n",
                        edge.getSource(), edge.getDestination(), edge.getWeight()));
            }

            writer.write("}\n");
            System.out.println("✅ Simplified DOT file generated: " + dotFile.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("❌ Error generating simplified DOT file: " + e.getMessage());
        }
    }

    /**
     * Visualize first few graphs from the dataset
     */
    public static void visualizeGraphs(List<Graph> graphs, PrimAlgorithm prim, KruskalAlgorithm kruskal) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("GRAPH VISUALIZATION (BONUS SECTION)");
        System.out.println("=".repeat(60));

        // Visualize only first 3 graphs to avoid too many files
        int graphsToVisualize = Math.min(3, graphs.size());

        for (int i = 0; i < graphsToVisualize; i++) {
            Graph graph = graphs.get(i);

            // Only visualize smaller graphs (up to 50 vertices)
            if (graph.getVertices() <= 50) {
                PrimAlgorithm.MSTResult primResult = prim.findMST(graph);
                KruskalAlgorithm.MSTResult kruskalResult = kruskal.findMST(graph);

                generateDOT(graph, primResult.getEdges(),
                        String.format("graph_%d_prim.dot", i + 1),
                        String.format("Graph%d_PrimMST", i + 1));

                generateDOT(graph, kruskalResult.getEdges(),
                        String.format("graph_%d_kruskal.dot", i + 1),
                        String.format("Graph%d_KruskalMST", i + 1));

                generateSimpleDOT(graph, primResult.getEdges(),
                        String.format("graph_%d_prim_simple.dot", i + 1),
                        String.format("Graph%d_PrimMST_Simple", i + 1));
            }
        }

        printVisualizationInstructions();
    }

    /**
     * Print instructions for rendering DOT files
     */
    private static void printVisualizationInstructions() {
        System.out.println("\n📊 GRAPH VISUALIZATION INSTRUCTIONS:");
        System.out.println("1. ONLINE (Easiest):");
        System.out.println("   - Go to: https://edotor.net/");
        System.out.println("   - Copy content from .dot files in output folder");
        System.out.println("   - Paste and click 'Generate Graph'");

        System.out.println("\n2. DESKTOP (GraphViz required):");
        System.out.println("   - Install GraphViz: https://graphviz.org/download/");
        System.out.println("   - Generate PNG: dot -Tpng output/filename.dot -o output/filename.png");
        System.out.println("   - Generate SVG: dot -Tsvg output/filename.dot -o output/filename.svg");

        System.out.println("\n3. QUICK COMMANDS:");
        System.out.println("   dot -Tpng output/graph_1_prim.dot -o output/graph_1_prim.png");
        System.out.println("   dot -Tpng output/graph_1_kruskal.dot -o output/graph_1_kruskal.png");

        System.out.println("\n🎨 TIPS:");
        System.out.println("   - Red edges: MST edges");
        System.out.println("   - Gray dashed edges: Original graph edges");
        System.out.println("   - Blue circles: City districts (vertices)");
    }
}