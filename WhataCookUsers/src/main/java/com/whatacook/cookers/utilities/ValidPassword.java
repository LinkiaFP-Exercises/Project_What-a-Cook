package com.whatacook.cookers.utilities;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for validating passwords.
 * Ensures passwords contain at least 8 characters, including uppercase, lowercase letters, numbers, and special characters.
 * <p>
 * Annotations:
 * - @Documented: Indicates that elements using this annotation should be documented by javadoc and similar tools.
 * - @Constraint: Specifies the validator class.
 * - @Target: Specifies the kinds of elements an annotation type can be applied to.
 * - @Retention: Specifies how long annotations with the annotated type are to be retained.
 * <p>
 * Fields:
 * - message: Custom error message for invalid passwords.
 * - groups: Allows the specification of validation groups.
 * - payload: Can be used by clients to associate metadata with a given constraint declaration.
 * <p>
 * Constants:
 * - characters: Allowed special characters in the password.
 * - regex: Regular expression for password validation.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
@Documented
@Constraint(validatedBy = {})
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Pattern(regexp = "^(?=.*[\\p{Ll}])(?=.*[\\p{Lu}])(?=.*\\p{N})(?=.*[\\u0021\\u00A1\\u007C\\u0027\\u00B4\\u0060\\u00A8\\u005E\\u002A\\u002B\\u0040\\u00B7\\u0023\\u0024\\u0025\\u0026\\u002F\\u007B\\u007D\\u0028\\u0029\\u003D\\u005C\\u002D\\u005F\\u003A\\u002E\\u003B\\u002C\\u003C\\u003E\\u003F\\u00BF])[\\p{L}\\p{N}\\u0021\\u00A1\\u007C\\u0027\\u00B4\\u0060\\u00A8\\u005E\\u002A\\u002B\\u0040\\u00B7\\u0023\\u0024\\u0025\\u0026\\u002F\\u007B\\u007D\\u0028\\u0029\\u003D\\u005C\\u002D\\u005F\\u003A\\u002E\\u003B\\u002C\\u003C\\u003E\\u003F\\u00BF]{8,}$")
public @interface ValidPassword {
    String characters = "";
    String regex = "";

    String message() default "Password must contain at least 8 characters, including uppercase, lowercase letters, numbers, and special characters (!#$%&'*+/=?^_`{|}~-).";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
