package com.whatacook.cookers.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApi {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("WhataCook <> Cookers")
                        .description("Aplicación de gestión de usuarios de la app WhataCook")
                        .version("0.0.1-SNAPSHOT")
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")));
    }

    @Bean
    public GroupedOpenApi employeesOpenApi(@Value("${springdoc.api-docs.version}") String appVersion) {
        String[] paths = {"api/users/**"};
        return GroupedOpenApi.builder().group("users")
                .addOpenApiCustomizer(openApi -> openApi.info(new Info()
                        .title("Cookers API").version(appVersion)))
                .pathsToMatch(paths)
                .build();
    }

}
