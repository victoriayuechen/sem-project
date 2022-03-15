package nl.tudelft.sem;

import lombok.Data;

@Data
public class ContractInfo {
    private String username;
    private String courseCode;
    private int hoursRequired;
    private String textualContract;
    private Status status;
    private String taDescription;
}
