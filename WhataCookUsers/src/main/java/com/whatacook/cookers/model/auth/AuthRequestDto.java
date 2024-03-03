package com.whatacook.cookers.model.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class AuthRequestDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -8635136711286938592L;
    @NotBlank(message = "username is mandatory!")
    private String username;
    @NotBlank(message = "password is mandatory!")
    private String password;
}
