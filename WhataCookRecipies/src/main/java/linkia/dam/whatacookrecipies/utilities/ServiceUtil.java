package linkia.dam.whatacookrecipies.utilities;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Sort;

@UtilityClass
public class ServiceUtil {

    public static Sort sortByName(String direction) {
        final boolean isNullOrAsc = direction.isBlank() || direction.toLowerCase().startsWith("a");
        Sort.Direction way = isNullOrAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(way, "name");
    }

}
