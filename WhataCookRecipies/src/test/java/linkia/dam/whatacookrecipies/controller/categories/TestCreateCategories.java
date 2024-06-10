package linkia.dam.whatacookrecipies.controller.categories;

import linkia.dam.whatacookrecipies.WhataCookRecipiesApplication;
import linkia.dam.whatacookrecipies.model.CategoryDto;
import linkia.dam.whatacookrecipies.service.CategoryService;
import linkia.dam.whatacookrecipies.controller.CategoryController;
import linkia.dam.whatacookrecipies.service.contracts.CategoryDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class TestCreateCategories extends BaseCategoriesTest {

    @Value("${app.endpoint.categories}")
    private String categoriesUri;

    private int amount;
    private List<CategoryDto> categoryDtoList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        amount = 36;
        categoryDtoList = generateCategoryDtoList(amount);
    }

    @Test
    void createCategories() {
        Flux<CategoryDto> categoryDtoFlux = Flux.fromIterable(categoryDtoList);

        when(categoryDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        when(categoryDao.save(any(CategoryDto.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        webTestClient.post()
                .uri(categoriesUri + "/bulk")
                .body(categoryDtoFlux, CategoryDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CategoryDto.class)
                .hasSize(amount)
                .contains(categoryDtoList.toArray(new CategoryDto[0]));
    }
}
