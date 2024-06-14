package com.whatacook.cookers.model.constants;

/**
 * Enumeration representing the different statuses an account can have.
 * <p>
 * Fields:
 * - PENDING: Account pending confirmation with a message.
 * - OK: Account activated with a message.
 * - OFF: Account deactivated with a message.
 * - OUTDATED: Password outdated with a message.
 * - REQUEST_DELETE: Account deletion requested with a message.
 * - MARKED_DELETE: Account marked for deletion with a message.
 * <p>
 * Methods:
 * - getDetails(): Returns the message associated with the status.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
public enum AccountStatus {

    PENDING("Falta confirmar el e-mail para activar la cuenta!"),
    OK("Cuenta activada correctamente!"),
    OFF("Cuenta desactivada, solicite activación"),
    OUTDATED("Contraseña desactualizada, solicite actualización"),
    REQUEST_DELETE("Borrado de cuenta solicitado!"),
    MARKED_DELETE("Cuenta marcada para ser borrada!");

    private final String message;

    AccountStatus(String message) {
        this.message = message;
    }

    /**
     * Returns the message associated with the status.
     *
     * @return The status message.
     */
    public String getDetails() {
        return message;
    }
}
