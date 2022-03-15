package nl.tudelft.sem.exceptions;

public class InvalidStatusException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidStatusException(String e) {
        super(e);
    }
}
