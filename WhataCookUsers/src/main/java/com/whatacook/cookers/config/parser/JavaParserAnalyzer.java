package com.whatacook.cookers.config.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
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

        Map<String, PackageInfo> packageMap = new HashMap<>();
        try {
            // Crear el directorio de salida si no existe
            Files.createDirectories(Paths.get(OUTPUT_DIR));

            // Borrar el contenido existente del archivo
            final Path pathTo_OUTPUT_FILE = Paths.get(OUTPUT_FILE);
            Files.deleteIfExists(pathTo_OUTPUT_FILE);

            extractData(projectDir, packageMap);

            // Guardar los datos extraídos en un archivo JSON
            String jsonString = toJsonString(packageMap);
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
    private static String escapeJson(String input) {
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
     * Converts a map of packages to a JSON string.
     *
     * @param packageMap the map of packages
     * @return the JSON string
     */
    private String toJsonString(Map<String, PackageInfo> packageMap) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(packageMap);
        } catch (IOException e) {
            log.error("Error converting to JSON string: {}", e.getMessage(), e);
            return "{}";
        }
    }

    /**
     * Extracts method information from Java source files in the specified directory.
     *
     * @param projectDir the project directory
     * @param packageMap the map to store extracted method information organized by package
     * @throws IOException if an I/O error occurs
     */
    private void extractData(File projectDir, Map<String, PackageInfo> packageMap) throws IOException {
        ParserConfiguration parserConfiguration = new ParserConfiguration()
                .setAttributeComments(false);
        JavaParser javaParser = new JavaParser(parserConfiguration);
        try (Stream<java.nio.file.Path> paths = Files.walk(projectDir.toPath())) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .forEach(path -> {
                        try {
                            CompilationUnit cu = javaParser.parse(path).getResult().orElseThrow(IOException::new);
                            cu.accept(new PackageVisitor(), packageMap);
                        } catch (IOException e) {
                            log.error("Error while extractData() in JavaParserAnalyzer: {}", e.getMessage(), e);
                        }
                    });
        }
    }

    /**
     * Visitor class to extract package and class information.
     */
    private static class PackageVisitor extends VoidVisitorAdapter<Map<String, PackageInfo>> {
        @Override
        public void visit(CompilationUnit cu, Map<String, PackageInfo> collector) {
            super.visit(cu, collector);
            String packageName = cu.getPackageDeclaration()
                    .map(pd -> pd.getName().toString())
                    .orElse("default");

            PackageInfo packageInfo = collector.computeIfAbsent(packageName, PackageInfo::new);

            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(c -> {
                ClassInfo classInfo = new ClassInfo();
                classInfo.setName(c.getNameAsString());
                c.getMethods().forEach(m -> {
                    MethodInfo methodInfo = new MethodInfo();
                    methodInfo.setName(m.getNameAsString());
                    methodInfo.setReturnType(m.getTypeAsString());
                    methodInfo.setParameters(m.getParameters().toString());
                    methodInfo.setJavadoc(m.getJavadoc().map(javadoc -> escapeJson(javadoc.toString())).orElse(""));
                    methodInfo.setBody(m.getBody().map(body -> escapeJson(body.toString())).orElse(""));
                    classInfo.getMethods().add(methodInfo);
                });
                packageInfo.getClasses().add(classInfo);
            });
        }
    }

    /**
     * Class representing package information.
     */
    @Getter
    @Setter
    private static class PackageInfo implements Serializable {
        @Serial
        private static final long serialVersionUID = 421L;
        private String name;
        private List<ClassInfo> classes = new ArrayList<>();

        public PackageInfo(String name) {
            this.name = name;
        }
    }

    /**
     * Class representing class information.
     */
    @Getter
    @Setter
    private static class ClassInfo implements Serializable {
        @Serial
        private static final long serialVersionUID = 422L;
        private String name;
        private List<MethodInfo> methods = new ArrayList<>();
    }

    /**
     * Class representing method information.
     */
    @Getter
    @Setter
    private static class MethodInfo implements Serializable {
        @Serial
        private static final long serialVersionUID = 423L;
        private String name;
        private String returnType;
        private String parameters;
        private String javadoc;
        private String body;
    }
}
