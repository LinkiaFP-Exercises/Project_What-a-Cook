package com.whatacook.cookers.model.users;

import com.whatacook.cookers.model.constants.AccountStatus;
import com.whatacook.cookers.model.constants.Role;
import com.whatacook.cookers.utilities.ValidEmail;
import com.whatacook.cookers.utilities.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Document(collection = "users")
public class UserDto {

    @Id
    private String _id;

    private LocalDateTime registration = LocalDateTime.now();

    @NotBlank(message = "Email is mandatory")
    @ValidEmail
    private String email;

    @ValidPassword
    private String password;

    private String firstName;

    private String surNames;

    private LocalDate birthdate;

    private Role roleType = Role.BASIC;

    private AccountStatus accountStatus = AccountStatus.PENDING;

    private LocalDateTime requestDeleteDate;

    public UserJson toJson() {
        return UserJson.from(this);
    }

    public UserJson toJsonWithoutId() {
        this.set_id(null);
        return UserJson.from(this);
    }

}
