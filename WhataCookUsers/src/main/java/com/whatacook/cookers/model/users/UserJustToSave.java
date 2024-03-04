package com.whatacook.cookers.model.users;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;

import static com.whatacook.cookers.utilities.Util.encryptPassword;

@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = { "password" }, allowSetters = true)
public class UserJustToSave {

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Properly formatted email is required", regexp="[\\p{L}\\p{N}!#$%&'*+/=?^_`{|}~-]+(?:.[\\p{L}\\p{N}!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\p{L}\\p{N}](?:[a-z0-9-]*[\\p{L}\\p{N}])?.)+[\\p{L}\\p{N}](?:[a-z0-9-]*[\\p{L}\\p{N}])?")
    private String email;

    @NotBlank(message = "Password is mandatory")
    private String password;

    @NotBlank(message = "FirstName is mandatory")
    private String firstName;

    @NotBlank(message = "Surnames is mandatory")
    private String surNames;

    @NotNull(message = "Birthdate is mandatory")
    @Past(message = "La fecha debe estar en el pasado")
    private LocalDate birthdate;

    public UserDTO toUserDTO() {

        UserDTO userDTO = new UserDTO();

        userDTO.setEmail(email);
        userDTO.setPassword(encryptPassword(password));
        userDTO.setFirstName(firstName);
        userDTO.setSurNames(surNames);
        userDTO.setBirthdate(birthdate);

        return userDTO;
    }

}
