package linkia.dam.whatacookrecipies.utilities;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Sort;

@UtilityClass
public class ServiceUtil {

    public static Sort sortByName(String direction) {
        final boolean isNullOrAsc = direction != null && direction.toLowerCase().startsWith("d");
        Sort.Direction way = isNullOrAsc ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(way, "name");
    }

}
