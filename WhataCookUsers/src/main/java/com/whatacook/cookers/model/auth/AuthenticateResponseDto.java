package com.whatacook.cookers.model.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@Getter
public class AuthenticateResponseDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -3708952482598857196L;
    private final String JWT_TOKEN;


}
