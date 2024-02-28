package com.whatacook.cookers.model.users;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.whatacook.cookers.utilities.Util.encryptPassword;

@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = { "password" }, allowSetters = true)
public class UserJson {

    private String _id;

    private LocalDateTime registration;

//    @NotBlank(message = "Email is mandatory")
//    @Email(message = "Properly formatted email is required", regexp="[\\p{L}\\p{N}!#$%&'*+/=?^_`{|}~-]+(?:.[\\p{L}\\p{N}!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\p{L}\\p{N}](?:[a-z0-9-]*[\\p{L}\\p{N}])?.)+[\\p{L}\\p{N}](?:[a-z0-9-]*[\\p{L}\\p{N}])?")
    private String email;

    private String password;

    private String firstName;

    private String surNames;

    private LocalDate birthdate;

    private String roleType;

    private String accountStatus;

    private String accountStatusMsg;

    private LocalDateTime requestDeleteDate;

    public UserJson(UserDTO userDTO) {

        this._id = userDTO.get_id();
        this.registration = userDTO.getRegistration();
        this.email = userDTO.getEmail();
        this.firstName = userDTO.getFirstName();
        this.surNames = userDTO.getSurNames();
        this.birthdate = userDTO.getBirthdate();
        this.roleType = userDTO.getRoleType().get();
        this.accountStatus = userDTO.getAccountStatus().toString();
        this.accountStatusMsg = userDTO.getAccountStatus().getDetails();
        this.requestDeleteDate = userDTO.getRequestDeleteDate();
    }

    public static UserJson from(UserDTO userDTO) {

        return new UserJson(userDTO);
    }

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
