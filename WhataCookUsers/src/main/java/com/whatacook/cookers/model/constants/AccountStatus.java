package com.whatacook.cookers.model.constants;

public enum AccountStatus {

    PENDING("Falta confirmar el e-mail para activar la cuenta!"),
    OK("Cuenta activada correctamente!"),
    OFF("Cuenta desactivada!"),
    REQUEST_DELETE("Borrado de cuenta solicitado!"),
    MARKED_DELETE("Cuenta marcada para ser borrada!"),
    DELETE("CUENTA BORRADA!"),
    OUTDATED("Hay que actualizar el PASSWORD!");

    AccountStatus(String message) {
        this.message = message;
    }
    public String getDetails() {
        return message;
    }
    private final String message;
}
