package nl.tudelft.sem.exceptions;

public class InvalidCourseException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidCourseException(String s) {
        super(s);
    }

}
