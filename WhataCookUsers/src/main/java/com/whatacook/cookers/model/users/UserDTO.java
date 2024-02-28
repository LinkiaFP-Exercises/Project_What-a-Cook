package com.whatacook.cookers.model.users;

import com.whatacook.cookers.model.constants.AccountStatus;
import com.whatacook.cookers.model.constants.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Document(collection = "users")
public class UserDTO {

    @Id
    private String _id;

    @CreatedDate
    private LocalDateTime registration = LocalDateTime.now();

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Properly formatted email is required", regexp = "[\\p{L}\\p{N}!#$%&'*+/=?^_`{|}~-]+(?:.[\\p{L}\\p{N}!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\p{L}\\p{N}](?:[a-z0-9-]*[\\p{L}\\p{N}])?.)+[\\p{L}\\p{N}](?:[a-z0-9-]*[\\p{L}\\p{N}])?")
    private String email;

    private String password;

    private String firstName;

    private String surNames;

    private LocalDate birthdate;

    private Role roleType = Role.BASIC;

    private AccountStatus accountStatus = AccountStatus.PENDING;

    private LocalDateTime requestDeleteDate;

    public static UserDTO justWithMail(String email) {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(email);
        return userDTO;
    }

    public UserJson toJson() {
        return UserJson.from(this);
    }

}
