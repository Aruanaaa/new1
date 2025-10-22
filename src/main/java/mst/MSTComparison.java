private void writeResultsToJson() {
    java.io.File outputDir = new java.io.File("output");
    if (!outputDir.exists()) {
        outputDir.mkdirs();
    }

    java.io.File outputFile = new java.io.File(outputDir, "output_results.json");

    try (FileWriter writer = new FileWriter(outputFile)) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Создаем основную структуру JSON
        JsonObject root = new JsonObject();
        JsonArray resultsArray = new JsonArray();

        for (ComparisonResult result : results) {
            JsonObject resultObj = new JsonObject();
            resultObj.addProperty("graphId", result.graphId);
            resultObj.addProperty("vertices", result.vertices);
            resultObj.addProperty("edges", result.edges);

            // Prim results
            JsonObject primObj = new JsonObject();
            primObj.addProperty("totalWeight", result.primWeight);
            primObj.addProperty("executionTimeNs", result.primTime);
            primObj.addProperty("operationsCount", result.primOperations);
            resultObj.add("prim", primObj);

            // Kruskal results
            JsonObject kruskalObj = new JsonObject();
            kruskalObj.addProperty("totalWeight", result.kruskalWeight);
            kruskalObj.addProperty("executionTimeNs", result.kruskalTime);
            kruskalObj.addProperty("operationsCount", result.kruskalOperations);
            resultObj.add("kruskal", kruskalObj);

            resultsArray.add(resultObj);
        }

        root.add("results", resultsArray);
        root.addProperty("summary", generateSummary());
        root.addProperty("totalGraphsTested", results.size());
        root.addProperty("comparisonDate", new java.util.Date().toString());

        // Записываем и принудительно сбрасываем буфер
        gson.toJson(root, writer);
        writer.flush();

        System.out.println("✅ Results successfully written to: " + outputFile.getAbsolutePath());
        System.out.println("📊 File contains results for " + results.size() + " graphs");

    } catch (IOException e) {
        System.err.println("❌ Error writing results to JSON: " + e.getMessage());
        e.printStackTrace();

        // Попробуем альтернативный путь
        try {
            java.io.File altFile = new java.io.File("output_results_backup.json");
            try (FileWriter altWriter = new FileWriter(altFile)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonArray resultsArray = new JsonArray();

                for (ComparisonResult result : results) {
                    JsonObject resultObj = new JsonObject();
                    resultObj.addProperty("graphId", result.graphId);
                    resultObj.addProperty("vertices", result.vertices);
                    resultObj.addProperty("edges", result.edges);
                    resultObj.addProperty("primWeight", result.primWeight);
                    resultObj.addProperty("kruskalWeight", result.kruskalWeight);
                    resultsArray.add(resultObj);
                }

                gson.toJson(resultsArray, altWriter);
                altWriter.flush();
                System.out.println("📁 Backup results written to: " + altFile.getAbsolutePath());
            }
        } catch (IOException e2) {
            System.err.println("❌ Backup also failed: " + e2.getMessage());
        }
    }
}