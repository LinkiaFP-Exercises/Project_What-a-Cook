package com.whatacook.cookers.model.constants;

public enum Role {

    BASIC("USER"), SUBSCRIBER("USER,PREMIUM"), CHIEF("ADMIN"), FULL("ADMIN,USER,PREMIUM");

    Role(String role) {
        this.role = role;
    }
    public String get() {
        return role;
    }
    private final String role;

}