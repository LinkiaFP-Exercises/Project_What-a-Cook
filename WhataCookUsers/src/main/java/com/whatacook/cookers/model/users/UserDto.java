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

/**
 * Data Transfer Object (DTO) for User entity.
 * Represents the user data stored in the database.
 * <p>
 * Annotations:
 * - @NoArgsConstructor: Generates a no-arguments constructor.
 * - @AllArgsConstructor: Generates an all-arguments constructor.
 * - @Getter: Generates getter methods for all fields.
 * - @Setter: Generates setter methods for all fields.
 * - @ToString: Generates a toString method for the class.
 * - @EqualsAndHashCode: Generates equals and hashCode methods for the class.
 * - @Document: Marks this class as a MongoDB document.
 * <p>
 * Fields:
 * - _id: The unique identifier for the user.
 * - registration: The registration date and time of the user.
 * - email: The email address of the user.
 * - password: The password of the user.
 * - firstName: The first name of the user.
 * - surNames: The surnames of the user.
 * - birthdate: The birthdate of the user.
 * - roleType: The role type of the user.
 * - accountStatus: The account status of the user.
 * - requestDeleteDate: The date and time when the user requested account deletion.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
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

    /**
     * Converts this UserDto to a UserJson object.
     *
     * @return A UserJson object representing this UserDto.
     */
    public UserJson toJson() {
        return UserJson.from(this);
    }

    /**
     * Converts this UserDto to a UserJson object without the ID.
     *
     * @return A UserJson object representing this UserDto without the ID.
     */
    public UserJson toJsonWithoutId() {
        this.set_id(null);
        return UserJson.from(this);
    }

}
