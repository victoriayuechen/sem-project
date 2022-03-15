package nl.tudelft.sem;

import lombok.Data;

@Data
public class WorkloadInfo {
    private String username;
    private String courseCode;
    private int hours;
    private Status status;
}
