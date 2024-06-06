package linkia.dam.whatacookrecipies.utilities;

import linkia.dam.whatacookrecipies.model.CategoryDto;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Mono;

import java.util.List;

@UtilityClass
public class ServiceUtil {

    public static Sort sortByName(String direction) {
        final boolean isNullOrAsc = direction == null || direction.toLowerCase().startsWith("a");
        Sort.Direction way = isNullOrAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(way, "name");
    }

    public static Mono<PagedResponse<CategoryDto>> aggregateCategories(ReactiveMongoTemplate reactiveMongoTemplate, Pageable pageable, Criteria criteria) {
        List<AggregationOperation> operations = List.of(
                Aggregation.match(criteria == null ? new Criteria() : criteria),
                Aggregation.skip((long) pageable.getPageNumber() * pageable.getPageSize()),
                Aggregation.limit(pageable.getPageSize())
        );

        Aggregation aggregation = Aggregation.newAggregation(operations);
        Mono<List<CategoryDto>> categoriesMono = reactiveMongoTemplate.aggregate(aggregation, "category", CategoryDto.class).collectList();
        Mono<Long> countMono = reactiveMongoTemplate.count(Query.query(criteria == null ? new Criteria() : criteria), "category");

        return categoriesMono.zipWith(countMono)
                .map(tuple -> {
                    List<CategoryDto> categories = tuple.getT1();
                    long totalElements = tuple.getT2();
                    int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());
                    return new PagedResponse<>(categories, pageable.getPageNumber(), pageable.getPageSize(), totalElements, totalPages);
                });
    }
}
