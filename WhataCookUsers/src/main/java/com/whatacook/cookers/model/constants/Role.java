package com.whatacook.cookers.model.constants;

/**
 * Enumeration representing the different roles a user can have.
 * <p>
 * Fields:
 * - BASIC: Basic user role.
 * - SUBSCRIBER: Subscriber role with premium privileges.
 * - CHIEF: Administrator role.
 * - FULL: Full access role with all privileges.
 * <p>
 * Methods:
 * - get(): Returns the role string associated with the role.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
public enum Role {

    BASIC("USER"), SUBSCRIBER("USER,PREMIUM"), CHIEF("ADMIN"), FULL("ADMIN,USER,PREMIUM");

    private final String role;

    Role(String role) {
        this.role = role;
    }

    /**
     * Returns the role string associated with the role.
     *
     * @return The role string.
     */
    public String get() {
        return role;
    }
}
