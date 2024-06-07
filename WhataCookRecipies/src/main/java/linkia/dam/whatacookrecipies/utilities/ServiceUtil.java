package linkia.dam.whatacookrecipies.utilities;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Sort;

import java.util.Comparator;

@UtilityClass
public class ServiceUtil {

    public static Sort sortByName(String direction) {
        Sort.Direction way = isNotNullAndStartWithD(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(way, "name");
    }

    public static boolean isNotNullAndStartWithD(String direction) {
        return direction != null && direction.toLowerCase().startsWith("d");
    }

}
