package nl.tudelft.sem.util;

import nl.tudelft.sem.ReviewInfo;
import nl.tudelft.sem.entities.Review;

public class ReviewBuilder {
    private transient Review review;

    /**
     * Contructor to easily make Review entities using a contractInfo entity.
     */
    public ReviewBuilder() {
        this.review = new Review();
        review.setTextualReview("This is a review.");
        review.setRating(8);
        review.setCourseCode("CSE2215");
        review.setUsername("ljpdeswart");
    }

    /**
     * Constructor using reviewInfo.
     *
     * @param reviewInfo reviewInfo to base review of
     */
    public ReviewBuilder(ReviewInfo reviewInfo) {
        this.review = new Review();
        review.setTextualReview(reviewInfo.getTextualReview());
        review.setRating(reviewInfo.getRating());
        review.setCourseCode(reviewInfo.getCourseCode());
        review.setUsername(reviewInfo.getUsername());
    }

    public Review build(long id) {
        review.setReviewId(id);
        return this.review;
    }

    public ReviewBuilder withTextualReview(String textualReview) {
        review.setTextualReview(textualReview);
        return this;
    }

    public ReviewBuilder withCourseCode(String courseCode) {
        review.setCourseCode(courseCode);
        return this;
    }

    public ReviewBuilder withUsername(String username) {
        review.setUsername(username);
        return this;
    }

    public ReviewBuilder withRating(int rating) {
        review.setRating(rating);
        return this;
    }
}
