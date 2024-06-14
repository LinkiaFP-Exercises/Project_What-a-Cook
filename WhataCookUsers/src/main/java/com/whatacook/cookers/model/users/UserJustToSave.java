package com.whatacook.cookers.model.users;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.whatacook.cookers.utilities.ValidEmail;
import com.whatacook.cookers.utilities.ValidPassword;
import jakarta.validation.constraints.*;
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
    @ValidEmail
    private String email;

    @NotBlank(message = "Password is mandatory")
    @ValidPassword
    private String password;

    @NotBlank(message = "FirstName is mandatory")
    @NotNull @NotEmpty
    private String firstName;

    @NotBlank(message = "Surnames is mandatory")
    @NotNull @NotEmpty
    private String surNames;

    @NotNull(message = "Birthdate is mandatory")
    @Past(message = "La fecha debe estar en el pasado")
    private LocalDate birthdate;

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
