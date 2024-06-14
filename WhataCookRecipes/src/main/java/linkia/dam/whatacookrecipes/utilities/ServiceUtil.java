package linkia.dam.whatacookrecipes.utilities;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Sort;

/**
 * Utility class for common service-related functionalities.
 * Provides methods to get sorting direction and check mode strings.
 * <p>
 * Annotations:
 * - @UtilityClass: Indicates that this class is a utility class and provides a private constructor.
 * <p>
 * Methods:
 * - getSortByName(String mode): Returns a {@link Sort} object for sorting by name based on the mode.
 * - isNotNullAndStartWithD(String mode): Checks if the mode string is not null and starts with "d".
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
@UtilityClass
public class ServiceUtil {

    /**
     * Returns a {@link Sort} object for sorting by name based on the mode.
     *
     * @param mode The sorting mode.
     * @return A {@link Sort} object for sorting by name.
     */
    public static Sort getSortByName(String mode) {
        Sort.Direction direction = isNotNullAndStartWithD(mode) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, "name");
    }

    /**
     * Checks if the mode string is not null and starts with "d".
     *
     * @param mode The mode string to check.
     * @return true if the mode is not null and starts with "d", false otherwise.
     */
    public static boolean isNotNullAndStartWithD(String mode) {
        return mode != null && mode.toLowerCase().startsWith("d");
    }
}
