package com.whatacook.cookers.utilities;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for validating email addresses.
 * Ensures emails are properly formatted.
 * <p>
 * Annotations:
 * - @Documented: Indicates that elements using this annotation should be documented by javadoc and similar tools.
 * - @Constraint: Specifies the validator class.
 * - @Target: Specifies the kinds of elements an annotation type can be applied to.
 * - @Retention: Specifies how long annotations with the annotated type are to be retained.
 * <p>
 * Fields:
 * - message: Custom error message for invalid email addresses.
 * - groups: Allows the specification of validation groups.
 * - payload: Can be used by clients to associate metadata with a given constraint declaration.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
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
