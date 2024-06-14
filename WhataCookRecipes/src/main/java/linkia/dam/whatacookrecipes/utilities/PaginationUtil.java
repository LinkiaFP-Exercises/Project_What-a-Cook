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

/**
 * Utility class for handling pagination.
 * Provides methods to create paged results and get comparators for sorting.
 * <p>
 * Annotations:
 * - @Log4j2: Generates a logger for the class.
 * <p>
 * Methods:
 * - createPagedResult(List<T> items, int page, int size, String mode): Creates a paged result from a list of items.
 * - getComparator(String mode): Returns a comparator for sorting items based on the mode.
 *
 * @Author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
@Log4j2
public class PaginationUtil {

    /**
     * Creates a paged result from a list of items.
     *
     * @param items The list of items to paginate.
     * @param page  The page number to retrieve.
     * @param size  The number of items per page.
     * @param mode  The sorting mode.
     * @param <T>   The type of items, extending {@link NamedEntity}.
     * @return A {@link Mono} emitting a {@link Page} of items.
     */
    public static <T extends NamedEntity> Mono<Page<T>> createPagedResult(List<T> items, int page, int size, String mode) {
        return Mono.just(items.stream()
                .sorted(getComparator(mode))
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.collectingAndThen(Collectors.toList(),
                        list -> new PageImpl<>(list, PageRequest.of(page, size, getSortByName(mode)), items.size())))
        );
    }

    /**
     * Returns a comparator for sorting items based on the mode.
     *
     * @param mode The sorting mode.
     * @param <T>  The type of items, extending {@link NamedEntity}.
     * @return A comparator for sorting items.
     */
    private static <T extends NamedEntity> Comparator<T> getComparator(String mode) {
        Comparator<T> comparator = Comparator.comparing(NamedEntity::getName);
        return isNotNullAndStartWithD(mode) ? comparator.reversed() : comparator;
    }
}
