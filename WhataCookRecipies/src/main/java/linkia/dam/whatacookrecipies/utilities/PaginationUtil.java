package linkia.dam.whatacookrecipies.utilities;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public static <T> Comparator<T> getComparator(String direction, Class<T> tClass) {
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
