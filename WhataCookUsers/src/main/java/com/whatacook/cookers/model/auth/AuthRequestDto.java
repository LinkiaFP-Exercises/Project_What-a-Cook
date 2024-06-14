package com.whatacook.cookers.model.auth;

import com.whatacook.cookers.utilities.ValidEmail;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * Data Transfer Object (DTO) for authentication requests.
 * Contains the username and password required for authentication.
 * <p>
 * Annotations:
 * - @NoArgsConstructor: Generates a no-arguments constructor.
 * - @AllArgsConstructor: Generates an all-arguments constructor.
 * - @Getter: Generates getter methods for all fields.
 * - @Setter: Generates setter methods for all fields.
 * - @ToString: Generates a toString method for the class.
 * - @EqualsAndHashCode: Generates equals and hashCode methods for the class.
 * <p>
 * Fields:
 * - serialVersionUID: Unique identifier for serializable classes.
 * - username: The username (email) of the user.
 * - password: The password of the user.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class AuthRequestDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -8635136711286938592L;

    @NotBlank(message = "username is mandatory!")
    @ValidEmail
    private String username;

    @NotBlank(message = "password is mandatory!")
    private String password;
}
