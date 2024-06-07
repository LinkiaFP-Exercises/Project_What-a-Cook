package linkia.dam.whatacookrecipies.controller;

import linkia.dam.whatacookrecipies.WhataCookRecipiesApplication;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@WebFluxTest(CategoryController.class)
@ContextConfiguration(classes = {WhataCookRecipiesApplication.class})
public class BaseTestingConfiguration {

    @Autowired
    protected WebTestClient webTestClient;

}
