package com.whatacook.cookers.config.parser;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Condition class to check if the environment is local.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
public class LocalEnvironmentCondition implements Condition {
    /**
     * Condition function to check if the environment is local.
     */
    @SuppressWarnings("NullableProblems")
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String environment = System.getenv("WHATA_COOK_ENV");
        return "local".equals(environment);
    }
}
