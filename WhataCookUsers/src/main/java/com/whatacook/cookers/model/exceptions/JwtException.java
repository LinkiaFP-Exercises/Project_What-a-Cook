package com.whatacook.cookers.model.exceptions;

import java.io.Serial;

public class JwtException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -2010045549194778425L;

    private static final String DETAILS = "Exception type JWT";

    public JwtException(String detail) {
        super(DETAILS + ": -> " + detail);
    }
}
