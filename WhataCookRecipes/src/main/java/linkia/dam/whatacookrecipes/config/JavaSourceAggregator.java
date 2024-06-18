package linkia.dam.whatacookrecipes.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.*;
import java.nio.file.*;
import java.util.stream.Stream;

/**
 * A component that aggregates all Java source files from a specified directory into a single text file.
 * This component is only active if the environment variable WHATA_COOK_ENV is set to "local".
 * The aggregated source code is written to "aggregated_source_code.txt".
 */
@Log4j2
@Component
@Conditional(LocalEnvironmentCondition.class)
public class JavaSourceAggregator {

    private static final String SOURCE_DIR = "src/main/java/linkia/dam/whatacookrecipes";
    private static final String OUTPUT_FILE = "aggregated_source_code_recipe-app.txt";

    /**
     * Aggregates all Java source files in the specified directory into a single text file.
     * This method is called automatically after the bean has been constructed.
     */
    @PostConstruct
    public void aggregateSourceCode() {
        File sourceDir = new File(SOURCE_DIR);

        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            log.error("El directorio especificado no existe: {}", SOURCE_DIR);
            return;
        }

        try {
            // Borrar el contenido existente del archivo
            final Path pathToOutputFile = Paths.get(OUTPUT_FILE);
            Files.deleteIfExists(pathToOutputFile);

            // Crear el archivo de salida
            try (BufferedWriter writer = Files.newBufferedWriter(pathToOutputFile, StandardOpenOption.CREATE)) {
                aggregateSourceFiles(sourceDir, writer);
            }
        } catch (IOException e) {
            log.error("Error while aggregating source code: {}", e.getMessage(), e);
        }
    }

    /**
     * Recursively aggregates all Java source files in the specified directory.
     *
     * @param directory the directory containing Java source files to aggregate
     * @param writer    the writer to which the aggregated source code is written
     * @throws IOException if an I/O error occurs
     */
    private void aggregateSourceFiles(File directory, BufferedWriter writer) throws IOException {
        try (Stream<Path> paths = Files.walk(directory.toPath())) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .forEach(path -> {
                        try (Stream<String> lines = Files.lines(path)) {
                            lines.forEach(line -> {
                                try {
                                    writer.write(line);
                                    writer.newLine();
                                } catch (IOException e) {
                                    log.error("Error writing line to output file: {}", e.getMessage(), e);
                                }
                            });
                            writer.write(System.lineSeparator());
                        } catch (IOException e) {
                            log.error("Error reading file {}: {}", path, e.getMessage(), e);
                        }
                    });
        }
    }
}
