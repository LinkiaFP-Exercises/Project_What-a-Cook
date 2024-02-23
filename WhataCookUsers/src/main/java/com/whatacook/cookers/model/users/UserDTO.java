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
    private LocalDateTime registration;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Properly formatted email is required", regexp = "[\\p{L}\\p{N}!#$%&'*+/=?^_`{|}~-]+(?:.[\\p{L}\\p{N}!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\p{L}\\p{N}](?:[a-z0-9-]*[\\p{L}\\p{N}])?.)+[\\p{L}\\p{N}](?:[a-z0-9-]*[\\p{L}\\p{N}])?")
    private String email;

    private String password;

    private String firstName;

    private String surNames;

    private LocalDate birthdate;

    private Role roleType = Role.BASIC;

    private AccountStatus accountStatus = AccountStatus.PENDING;

    @Setter(AccessLevel.NONE)
    private LocalDateTime requestDeleteDate;

    public static UserDTO justWithMail(String email) {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(email);
        return userDTO;
    }

    public UserJson toJson() {
        return UserJson.from(this);
    }

    public final void setAccountStatus(AccountStatus status) {
        // Condiciones para cambiar a REQUEST_DELETE y establecer fecha.
        if (status == AccountStatus.REQUEST_DELETE && requestDeleteDate == null) {
            this.accountStatus = status;
            this.requestDeleteDate = LocalDateTime.now();
            return; // Termina la ejecución para evitar chequeos innecesarios.
        }

        // Condición para cambiar a MARKED_DELETE después de 30 días.
        if (this.accountStatus == AccountStatus.REQUEST_DELETE && requestDeleteDate != null
                && ChronoUnit.DAYS.between(requestDeleteDate, LocalDateTime.now()) > 30) {
            this.accountStatus = AccountStatus.MARKED_DELETE;
            return; // Termina la ejecución para evitar chequeos innecesarios.
        }

        // Permite revertir el REQUEST_DELETE dentro de los 30 días si el nuevo estado
        // es permitido.
        if (this.accountStatus == AccountStatus.REQUEST_DELETE && requestDeleteDate != null
                && ChronoUnit.DAYS.between(requestDeleteDate, LocalDateTime.now()) <= 30
                && EnumSet.of(AccountStatus.PENDING, AccountStatus.OK, AccountStatus.OFF, AccountStatus.OUTDATED)
                .contains(status)) {
            this.requestDeleteDate = null;
            this.accountStatus = status;
            return; // Termina la ejecución para evitar chequeos innecesarios.
        }

        // Condiciones para cambiar a DELETE después de un año como MARKED_DELETE.
        if (status == AccountStatus.MARKED_DELETE && requestDeleteDate != null
                && ChronoUnit.DAYS.between(requestDeleteDate, LocalDateTime.now()) > 365) {
            this.accountStatus = AccountStatus.DELETE;
        }
    }
}
