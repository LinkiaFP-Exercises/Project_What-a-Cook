package com.whatacook.cookers.utilities;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
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
@Constraint(validatedBy = {})
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Pattern(regexp = "^(?=.*[\\p{Ll}])(?=.*[\\p{Lu}])(?=.*\\p{N})(?=.*[!¡|'´`¨^*+@·#$%&/{}()=-_:.;,<>?¿])[\\p{L}\\p{N}!¡|'´`¨^*+@·#$%&/{}()=-_:.;,<>?¿]{8,}$")
public @interface ValidPassword {
    String characters = "";
    String regex = "";
    String message() default "Password must contain at least 8 characters, including uppercase, lowercase letters, numbers, and special characters (!#$%&'*+/=?^_`{|}~-).";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

