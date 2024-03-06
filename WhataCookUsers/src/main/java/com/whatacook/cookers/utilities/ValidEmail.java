package com.whatacook.cookers.utilities;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = {}) // No necesita un validador personalizado
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Email(message = "Invalid email format")
@Pattern(regexp = "^[\\p{L}\\p{N}!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\p{L}\\p{N}!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\p{L}\\p{N}](?:[a-z0-9-]*[\\p{L}\\p{N}])?\\.)+[\\p{L}\\p{N}](?:[a-z0-9-]*[\\p{L}\\p{N}])?$")
public @interface ValidEmail {

    String message() default "Properly formatted email is required";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

