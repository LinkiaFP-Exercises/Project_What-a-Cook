package com.whatacook.cookers.config.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Component that analyzes Java source files in a specific directory and extracts method information.
 * The extracted information is saved to a JSON file.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
@Log4j2
@Component
@Conditional(LocalEnvironmentCondition.class)
public class JavaParserAnalyzer {

    private static final String OUTPUT_DIR = "src/main/java/com/whatacook/cookers";
    private static final String OUTPUT_FILE = OUTPUT_DIR + "/extracted_code_data_from_cookers-app.json";

    /**
     * PostConstruct method that initiates the analysis process.
     * It creates the output directory and file, then extracts method data from the Java source files.
     */
    @PostConstruct
    public void analyze() {
        File projectDir = new File(OUTPUT_DIR);

        if (!projectDir.exists() || !projectDir.isDirectory()) {
            log.error("El directorio especificado no existe: {}", OUTPUT_DIR);
            return;
        }

        List<MethodInfo> extractedData = new ArrayList<>();
        try {
            // Crear el directorio de salida si no existe
            Files.createDirectories(Paths.get(OUTPUT_DIR));

            // Borrar el contenido existente del archivo
            final Path pathTo_OUTPUT_FILE = Paths.get(OUTPUT_FILE);
            Files.deleteIfExists(pathTo_OUTPUT_FILE);

            extractData(projectDir, extractedData);

            // Guardar los datos extraídos en un archivo JSON
            List<Map<String, String>> jsonList = extractedData.stream()
                    .map(methodInfo -> {
                        Map<String, String> jsonObject = new HashMap<>();
                        jsonObject.put("name", escapeJson(methodInfo.getName()));
                        jsonObject.put("returnType", escapeJson(methodInfo.getReturnType()));
                        jsonObject.put("parameters", escapeJson(methodInfo.getParameters()));
                        jsonObject.put("javadoc", escapeJson(methodInfo.getJavadoc()));
                        jsonObject.put("body", escapeJson(methodInfo.getBody()));
                        return jsonObject;
                    }).collect(Collectors.toList());

            String jsonString = toJsonString(jsonList);
            Files.write(pathTo_OUTPUT_FILE, jsonString.getBytes());
        } catch (IOException e) {
            log.error("Error while analyzing Java parser: {}", e.getMessage(), e);
        }
    }

    /**
     * Escapes special characters in a JSON string.
     *
     * @param input the input string
     * @return the escaped string
     */
    private String escapeJson(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Converts a list of maps to a JSON string.
     *
     * @param jsonList the list of maps
     * @return the JSON string
     */
    private String toJsonString(List<Map<String, String>> jsonList) {
        StringBuilder jsonString = new StringBuilder("[\n");
        for (Map<String, String> jsonObject : jsonList) {
            jsonString.append("  {\n");
            for (Map.Entry<String, String> entry : jsonObject.entrySet()) {
                jsonString.append("    \"").append(entry.getKey()).append("\": \"").append(entry.getValue()).append("\",\n");
            }
            // Remove last comma and newline, then add closing brace
            jsonString.setLength(jsonString.length() - 2);
            jsonString.append("\n  },\n");
        }
        // Remove last comma and newline, then add closing bracket
        if (!jsonList.isEmpty()) {
            jsonString.setLength(jsonString.length() - 2);
        }
        jsonString.append("\n]");
        return jsonString.toString();
    }

    /**
     * Extracts method information from Java source files in the specified directory.
     *
     * @param projectDir the project directory
     * @param extractedData the list to store extracted method information
     * @throws IOException if an I/O error occurs
     */
    private void extractData(File projectDir, List<MethodInfo> extractedData) throws IOException {
        JavaParser javaParser = new JavaParser();
        try (Stream<java.nio.file.Path> paths = Files.walk(projectDir.toPath())) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .forEach(path -> {
                        try {
                            CompilationUnit cu = javaParser.parse(path).getResult().orElseThrow(IOException::new);
                            cu.accept(new MethodVisitor(), extractedData);
                        } catch (IOException e) {
                            log.error("Error while extractData() in JavaParserAnalyzer: {}", e.getMessage(), e);
                        }
                    });
        }
    }

    /**
     * Visitor class to extract method information from a CompilationUnit.
     */
    private static class MethodVisitor extends VoidVisitorAdapter<List<MethodInfo>> {
        @Override
        public void visit(MethodDeclaration md, List<MethodInfo> collector) {
            super.visit(md, collector);
            MethodInfo methodInfo = new MethodInfo();
            methodInfo.setName(md.getNameAsString());
            methodInfo.setReturnType(md.getTypeAsString());
            methodInfo.setParameters(md.getParameters().toString());
            methodInfo.setJavadoc(md.getJavadoc().map(Object::toString).orElse(""));
            methodInfo.setBody(md.getBody().map(Object::toString).orElse(""));
            collector.add(methodInfo);
        }
    }

    /**
     * Class representing method information.
     */
    @Getter
    @Setter
    private static class MethodInfo implements Serializable {
        @Serial
        private static final long serialVersionUID = 420L;
        private String name;
        private String returnType;
        private String parameters;
        private String javadoc;
        private String body;
    }

}

