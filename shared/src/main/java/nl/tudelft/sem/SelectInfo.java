package nl.tudelft.sem;

import lombok.Data;

@Data
public class SelectInfo {
    private String username;
    private String courseCode;
    private Status status;

    public SelectInfo() {}

    /**
     * Constructor for selectInfo.
     *
     * @param username      Username of the user.
     * @param courseCode    Course code of the course.
     * @param status        The status of the application.
     */
    public SelectInfo(String username, String courseCode, Status status) {
        this.username = username;
        this.courseCode = courseCode;
        this.status = status;
    }
}
