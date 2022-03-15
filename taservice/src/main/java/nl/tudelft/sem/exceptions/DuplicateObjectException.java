package nl.tudelft.sem.exceptions;

public class DuplicateObjectException extends Exception {
    private static final long serialVersionUID = 1L;

    public DuplicateObjectException(String e) {
        super(e);
    }
}
