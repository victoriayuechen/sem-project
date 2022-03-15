package nl.tudelft.sem.exceptions;

public class InvalidApplicationException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidApplicationException(String s) {
        super(s);
    }
}
