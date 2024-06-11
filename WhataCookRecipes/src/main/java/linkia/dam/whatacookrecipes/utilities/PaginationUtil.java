package linkia.dam.whatacookrecipes.utilities;

import linkia.dam.whatacookrecipes.model.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static linkia.dam.whatacookrecipes.utilities.ServiceUtil.isNotNullAndStartWithD;
import static linkia.dam.whatacookrecipes.utilities.ServiceUtil.getSortByName;

@Log4j2
public class PaginationUtil {

    public static <T extends NamedEntity> Mono<Page<T>> createPagedResult(List<T> items, int page, int size, String mode) {
        return Mono.just(items.stream()
                .sorted(getComparator(mode))
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.collectingAndThen(Collectors.toList(),
                        list -> new PageImpl<>(list, PageRequest.of(page, size, getSortByName(mode)), items.size())))
        );
    }

    private static <T extends NamedEntity> Comparator<T> getComparator(String mode) {
        Comparator<T> comparator = Comparator.comparing(NamedEntity::getName);
        return isNotNullAndStartWithD(mode) ? comparator.reversed() : comparator;
    }

}
