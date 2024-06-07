package linkia.dam.whatacookrecipies.utilities;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Sort;

@UtilityClass
public class ServiceUtil {

    public static Sort getSortByName(String mode) {
        Sort.Direction direction = isNotNullAndStartWithD(mode) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, "name");
    }

    public static boolean isNotNullAndStartWithD(String mode) {
        return mode != null && mode.toLowerCase().startsWith("d");
    }

}
