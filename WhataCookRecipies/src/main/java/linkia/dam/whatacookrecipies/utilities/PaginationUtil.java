package linkia.dam.whatacookrecipies.utilities;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.Comparator;

@Log4j2
public class PaginationUtil {

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

    public static Pageable getPageableSortByName(int page, int size, String direction) {
        return PageRequest.of(page, size, ServiceUtil.sortByName(direction));
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

    public static <T> Comparator<T> getComparator(Class<T> tClass, String direction) {
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
            if (ServiceUtil.isNotNullAndStartWithD(direction)) {
                comparator = comparator.reversed();
            }
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        }
        return comparator;
    }

}
