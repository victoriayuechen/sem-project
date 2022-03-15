package nl.tudelft.sem;

public class ErrorString {
    private transient String error;

    public ErrorString(String message) {
        this.error = "=== Error Message: " + message + " ===";
    }

    public String getError() {
        return this.error;
    }
}
