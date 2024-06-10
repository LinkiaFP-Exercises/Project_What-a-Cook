package linkia.dam.whatacookrecipies.controller;

import linkia.dam.whatacookrecipies.model.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@ExtendWith(SpringExtension.class)
public class BaseTestingConfiguration {

    protected final String DELETED = "deleted";
    protected final String PATH_ID = "/id/{id}";
    protected final String PATH_NAME = "/name/{name}";
    protected String pathVariable, valuePathVariable, name;
    protected int page, size, amount = 36;

    protected int getNumberLastElements() {
        return amount % size == 0 ? size : amount % size;
    }

    protected <T extends NamedEntity> T getExpectedDto(boolean desc, List<T> listToSort) {
        List<T> sortedList = new ArrayList<>(listToSort);

        sortedList.sort((a, b) -> desc ? b.getName().compareTo(a.getName()) : a.getName().compareTo(b.getName()));

        int startIndex = page * size;
        if (startIndex >= sortedList.size()) {
            throw new IndexOutOfBoundsException("Start index is out of bounds");
        }

        return sortedList.get(startIndex);
    }

    protected void TestGetByPathVariableFounded(WebTestClient webTestClient, String pathVariable, String valuePathVariable, NamedEntity namedEntity) {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(pathVariable)
                        .build(valuePathVariable))
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(namedEntity.getId())
                .jsonPath("$.name").isEqualTo(namedEntity.getName());
    }

    protected void TestGetByPathVariableNotFound(WebTestClient webTestClient, String pathVariable, String valuePathVariable) {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(pathVariable)
                        .build(valuePathVariable))
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }
}
