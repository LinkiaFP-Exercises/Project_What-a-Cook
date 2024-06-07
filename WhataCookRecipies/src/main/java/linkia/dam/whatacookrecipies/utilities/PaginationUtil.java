package linkia.dam.whatacookrecipies.utilities;

import linkia.dam.whatacookrecipies.model.CategoryDto;
import linkia.dam.whatacookrecipies.model.IngredientDto;
import linkia.dam.whatacookrecipies.model.MeasureDto;
import linkia.dam.whatacookrecipies.model.RecipeDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static linkia.dam.whatacookrecipies.utilities.ServiceUtil.isNotNullAndStartWithD;
import static linkia.dam.whatacookrecipies.utilities.ServiceUtil.getSortByName;

@Log4j2
public class PaginationUtil {

    public static <T> Mono<Page<T>> createPagedResult(List<T> items, int page, int size, String mode, Class<T> tClass) {
        return Mono.just(items.stream()
                .sorted(getComparator(tClass, mode))
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.collectingAndThen(Collectors.toList(),
                        list -> new PageImpl<>(list, PageRequest.of(page, size, getSortByName(mode)), items.size())))
        );
    }

    public static <T> Comparator<T> getComparator(Class<T> tClass, String mode) {
        Comparator<T> comparator = switch (tClass.getSimpleName()) {
            case "CategoryDto" -> Comparator.comparing(item -> ((CategoryDto) item).getName());
            case "IngredientDto" -> Comparator.comparing(item -> ((IngredientDto) item).getName());
            case "MeasureDto" -> Comparator.comparing(item -> ((MeasureDto) item).getName());
            case "RecipeDto" -> Comparator.comparing(item -> ((RecipeDto) item).getName());
            default -> throw new IllegalArgumentException("Unsupported class: " + tClass.getSimpleName());
        };
        return isNotNullAndStartWithD(mode) ? comparator.reversed() : comparator;
    }

/*
    public static <T> Mono<Page<T>> createPagedResult(Flux<T> items, Mono<Long> count, Pageable pageable, Comparator<T> comparator) {
        return count.flatMap(totalCount -> items
                .sort(comparator)
                .skip((long) pageable.getPageNumber() * pageable.getPageSize())
                .take(pageable.getPageSize())
                .collectList()
                .map(list -> new PageImpl<>(list, pageable, totalCount))
        );
    }

    public static <T> Mono<Page<T>> createPagedResult(Flux<T> items, Mono<Long> count, Pageable pageable, Class<T> tClass) {
        return count.flatMap(totalCount -> items
                .sort(getComparator(tClass, pageable))
                .skip((long) pageable.getPageNumber() * pageable.getPageSize())
                .take(pageable.getPageSize())
                .collectList()
                .map(list -> new PageImpl<>(list, pageable, totalCount))
        );
    }

    public static <T> Mono<Page<T>> createPagedResult(Flux<T> items, int page, int size, String direction, Class<T> tClass) {
        return items.count().flatMap(total -> items
                .sort(getComparator(tClass, direction))
                .skip((long) page * size)
                .take(size)
                .collectList()
                .map(list -> new PageImpl<>(list, getPageableSortByName(page, size, direction), total))
        );
    }

    public static <T> Mono<Page<T>> createPagedResult(Flux<T> items, Mono<Long> count, int page, int size, String direction, Class<T> tClass) {
        Pageable pageable = getPageableSortByName(page, size, direction);
        return count.flatMap(totalCount -> items
                .sort(getComparator(tClass, pageable))
                .skip((long) pageable.getPageNumber() * pageable.getPageSize())
                .take(pageable.getPageSize())
                .collectList()
                .map(list -> new PageImpl<>(list, pageable, totalCount))
        );
    }

    public static <T> Comparator<T> getComparator(Class<T> tClass, Pageable pageable) {
        Sort.Order order = pageable.getSort().getOrderFor("name");
        Comparator<T> comparator = null;
        try {
            Method method = tClass.getMethod("getName");
            comparator = Comparator.comparing(item -> {
                try {
                    return (Comparable) method.invoke(item);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                return null;
            });
            if (order.isDescending()) {
                comparator = comparator.reversed();
            }
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        }
        return comparator;
    }

 */

}
