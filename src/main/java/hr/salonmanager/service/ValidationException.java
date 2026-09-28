package hr.salonmanager.service;

/** Iznimka za neispravan korisnički unos ili kršenje poslovnog pravila. */
public class ValidationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message);
    }
}
