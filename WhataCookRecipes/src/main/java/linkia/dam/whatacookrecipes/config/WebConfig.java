package linkia.dam.whatacookrecipes.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * Web configuration class for setting up resource handlers.
 * This class implements {@code WebFluxConfigurer} to configure the resource handlers
 * for serving static resources from the file system.
 * <p>
 * Annotations:
 * - {@code @Configuration}: Indicates that this class contains Spring configuration.
 * <p>
 * Methods:
 * - {@code addResourceHandlers(ResourceHandlerRegistry registry)}: Configures the resource handlers for serving static resources.
 * </p>
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
@Configuration
public class WebConfig implements WebFluxConfigurer {

    /**
     * Configures the resource handlers for the application.
     * <p>
     * This method overrides the {@code addResourceHandlers} method from the
     * {@code WebFluxConfigurer} interface to add a specific resource handler.
     * The resource handler configures the path to serve static files
     * from the file system directory {@code /app/javadoc} via the path
     * {@code /api/javadoc/**}.
     * </p>
     *
     * @param registry the resource handler registry to be used to add the handlers.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/api/javadoc/**")
                .addResourceLocations("file:/app/javadoc/");
    }
}
