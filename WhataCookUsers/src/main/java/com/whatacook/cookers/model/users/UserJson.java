package com.whatacook.cookers.model.users;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.whatacook.cookers.utilities.ValidEmail;
import com.whatacook.cookers.utilities.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * JSON representation of the User entity.
 * Used for serialization and deserialization of user data.
 * <p>
 * Annotations:
 * - @NoArgsConstructor: Generates a no-arguments constructor.
 * - @Getter: Generates getter methods for all fields.
 * - @Setter: Generates setter methods for all fields.
 * - @ToString: Generates a toString method for the class.
 * - @EqualsAndHashCode: Generates equals and hashCode methods for the class.
 * - @JsonInclude: Specifies inclusion criteria for JSON serialization.
 * - @JsonIgnoreProperties: Specifies properties to ignore during JSON serialization.
 * <p>
 * Fields:
 * - _id: The unique identifier for the user.
 * - registration: The registration date and time of the user.
 * - email: The email address of the user.
 * - password: The password of the user.
 * - newPassword: The new password for the user.
 * - firstName: The first name of the user.
 * - surNames: The surnames of the user.
 * - birthdate: The birthdate of the user.
 * - roleType: The role type of the user.
 * - accountStatus: The account status of the user.
 * - accountStatusMsg: The message describing the account status.
 * - requestDeleteDate: The date and time when the user requested account deletion.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = {"password"}, allowSetters = true)
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

    /**
     * Constructs a UserJson object from a UserDto object.
     *
     * @param userDTO The UserDto object to convert.
     */
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

    /**
     * Constructs a UserJson object with a given ID.
     *
     * @param id The ID of the user.
     */
    public UserJson(String id) {
        _id = id;
    }

    /**
     * Creates a UserJson object from a UserDto object.
     *
     * @param userDTO The UserDto object to convert.
     * @return A UserJson object.
     */
    public static UserJson from(UserDto userDTO) {
        return new UserJson(userDTO);
    }

}
