package nl.tudelft.sem.exceptions;

public class AddRoleFailureException extends Exception {
    private static final long serialVersionUID = 1L;

    public AddRoleFailureException(String e) {
        super(e);
    }
}
