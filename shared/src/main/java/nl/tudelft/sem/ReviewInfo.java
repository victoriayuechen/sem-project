package nl.tudelft.sem;

import lombok.Data;

@Data
public class ReviewInfo {
    private int rating;
    private String textualReview;
    private String username;
    private String courseCode;
}
