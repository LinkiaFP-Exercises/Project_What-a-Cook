package linkia.dam.whatacookrecipies.utilities;

import org.springframework.data.domain.Sort;

public class ServiceUtil {

    public static Sort sortByName(String direction) {
        final boolean isNullOrAsc = direction == null || direction.toLowerCase().startsWith("a");
        Sort.Direction way = isNullOrAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(way, "name");
    }
}
