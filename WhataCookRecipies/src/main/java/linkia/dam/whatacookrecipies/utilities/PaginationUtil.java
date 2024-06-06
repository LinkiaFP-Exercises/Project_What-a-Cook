package linkia.dam.whatacookrecipies.utilities;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class PaginationUtil {

    public static <T> Mono<Page<T>> createPagedResult(Flux<T> items, Mono<Long> count, Pageable pageable) {
        return count.flatMap(totalCount -> items
                .skip((long) pageable.getPageNumber() * pageable.getPageSize())
                .take(pageable.getPageSize())
                .collectList()
                .map(list -> new PageImpl<>(list, pageable, totalCount))
        );
    }
}
