package nl.tudelft.sem;

import lombok.Data;

@Data
public class AverageWorkload {
    private String courseCode;
    private int averageHours;
    private String username;
}
