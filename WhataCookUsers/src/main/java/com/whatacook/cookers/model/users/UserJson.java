package com.whatacook.cookers.model.users;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.whatacook.cookers.utilities.ValidEmail;
import com.whatacook.cookers.utilities.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @NotBlank(message = "Email is mandatory")
    @ValidEmail
    private String email;

    @ValidPassword
    private String password;

    @ValidPassword
    private String newPassword;

    private String firstName;
    private String surNames;
    private LocalDate birthdate;
    private String roleType;
    private String accountStatus;
    private String accountStatusMsg;
    private LocalDateTime requestDeleteDate;

    public UserJson(UserDto userDTO) {

        if (userDTO.get_id() != null)
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

    public UserJson(String id) { _id = id; }

    public static UserJson from(UserDto userDTO) { return new UserJson(userDTO); }

}
