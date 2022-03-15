package nl.tudelft.sem;

import java.time.ZonedDateTime;
import lombok.Data;

@Data
public class CourseInfo {
    private String courseCode;
    private String courseName;
    private int quarter;
    private int numberOfStudents;
    private boolean isOpen;
    private double averageTaHour;
    private int duration;
    private int numberOfTas;
    private ZonedDateTime startDate;
}
