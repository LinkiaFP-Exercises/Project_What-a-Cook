package com.whatacook.cookers.model.users;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.whatacook.cookers.utilities.ValidEmail;
import com.whatacook.cookers.utilities.ValidPassword;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

import static com.whatacook.cookers.utilities.Util.encryptPassword;

/**
 * Representation of user data for saving purposes.
 * Used to transfer user data from input forms to the UserDto entity.
 * <p>
 * Annotations:
 * - @NoArgsConstructor: Generates a no-arguments constructor.
 * - @Getter: Generates getter methods for all fields.
 * - @Setter: Generates setter methods for all fields.
 * - @ToString: Generates a toString method for the class.
 * - @JsonInclude: Specifies inclusion criteria for JSON serialization.
 * - @JsonIgnoreProperties: Specifies properties to ignore during JSON serialization.
 * <p>
 * Fields:
 * - email: The email address of the user.
 * - password: The password of the user.
 * - firstName: The first name of the user.
 * - surNames: The surnames of the user.
 * - birthdate: The birthdate of the user.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = {"password"}, allowSetters = true)
public class UserJustToSave {

    @NotBlank(message = "Email is mandatory")
    @ValidEmail
    private String email;

    @NotBlank(message = "Password is mandatory")
    @ValidPassword
    private String password;

    @NotBlank(message = "FirstName is mandatory")
    @NotNull
    @NotEmpty
    private String firstName;

    @NotBlank(message = "Surnames is mandatory")
    @NotNull
    @NotEmpty
    private String surNames;

    @NotNull(message = "Birthdate is mandatory")
    @Past(message = "The date must be in the past")
    private LocalDate birthdate;

    /**
     * Converts this UserJustToSave to a UserDto object.
     *
     * @return A UserDto object representing this UserJustToSave.
     */
    public UserDto toUserDTO() {

        UserDto userDTO = new UserDto();

        userDTO.setEmail(email);
        userDTO.setPassword(encryptPassword(password));
        userDTO.setFirstName(firstName);
        userDTO.setSurNames(surNames);
        userDTO.setBirthdate(birthdate);

        return userDTO;
    }

}
