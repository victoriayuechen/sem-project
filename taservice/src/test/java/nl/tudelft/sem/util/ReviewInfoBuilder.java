package nl.tudelft.sem.util;

import nl.tudelft.sem.ReviewInfo;

public class ReviewInfoBuilder {
    public transient ReviewInfo reviewInfo;

    /**
     * Constructor for ReviewInfoBuilder.
     */
    public ReviewInfoBuilder() {
        reviewInfo = new ReviewInfo();
        reviewInfo.setTextualReview("This is a review");
        reviewInfo.setCourseCode("CSE2021");
        reviewInfo.setUsername("tdevalck");
        reviewInfo.setRating(8);
    }

    public ReviewInfo build() {
        return reviewInfo;
    }

    public ReviewInfoBuilder withTextualReview(String textualReview) {
        reviewInfo.setTextualReview(textualReview);
        return this;
    }

    public ReviewInfoBuilder withCourseCode(String courseCode) {
        reviewInfo.setCourseCode(courseCode);
        return this;
    }

    public ReviewInfoBuilder withUsername(String username) {
        reviewInfo.setUsername(username);
        return this;
    }

    public ReviewInfoBuilder withRating(int rating) {
        reviewInfo.setRating(rating);
        return this;
    }
}
