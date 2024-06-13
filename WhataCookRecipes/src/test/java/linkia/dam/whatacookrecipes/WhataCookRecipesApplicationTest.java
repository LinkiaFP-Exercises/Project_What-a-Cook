package linkia.dam.whatacookrecipes;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {WhataCookRecipesApplication.class, TestMongoConfig.class})
public class WhataCookRecipesApplicationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        assertThat(context).isNotNull();
        assertThat(context.getBean("testMongoClient")).isNotNull();
    }
}
