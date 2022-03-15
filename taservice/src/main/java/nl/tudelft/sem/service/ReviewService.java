package nl.tudelft.sem.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.ReviewInfo;
import nl.tudelft.sem.entities.Review;
import nl.tudelft.sem.exceptions.EmptyTargetException;
import nl.tudelft.sem.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
    @Autowired
    private transient ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    /**
     * Endpoint to update review in the database.
     *
     * @param reviewInfo The updated ReviewInfo entity
     * @return The updated review entity as confirmation
     */
    public ReviewInfo updateReview(ReviewInfo reviewInfo) throws EmptyTargetException {
        if (reviewInfo == null) {
            throw new NullPointerException("Invalid review");
        }

        if (reviewRepository
                .findAllByUsernameAndCourseCode(
                        reviewInfo.getUsername(),
                        reviewInfo.getCourseCode())
                .isEmpty()) {
            throw new EmptyTargetException("No Review found");
        }

        Review review = reviewRepository
                .findAllByUsernameAndCourseCode(
                        reviewInfo.getUsername(),
                        reviewInfo.getCourseCode())
                .get();

        review.setTextualReview(reviewInfo.getTextualReview());
        review.setRating(reviewInfo.getRating());

        reviewRepository.save(review);

        return reviewInfo;
    }

    /**
     * Endpoint to delete a review by id.
     *
     * @param reviewId The ID of the review as a String
     * @return The deleted review entity
     */
    public Review deleteReview(String reviewId) throws EmptyTargetException {
        if (reviewId == null) {
            throw new NullPointerException("Invalid ID");
        }

        long id = Long.parseLong(reviewId);

        if (reviewRepository.findById(id).isEmpty()) {
            throw new EmptyTargetException("No Contract Found");
        }

        Review review = reviewRepository.findById(id).get();

        reviewRepository.deleteById(id);

        return review;
    }

    /**
     * Gets the average hours a TA has worked since the start of the course.
     *
     * @param courseCode The course code of the course.
     * @return the average amount of hours worked.
     */
    public Review getReviewByCourse(String username, String courseCode)
            throws EmptyTargetException {
        Optional<Review> review =
                reviewRepository.findAllByUsernameAndCourseCode(username, courseCode);

        if (review.isEmpty()) {
            throw new EmptyTargetException("No reviews with this username and/or course code.");
        }
        return review.get();
    }

    /**
     * Create a review.
     *
     * @param reviewInfo reviewInfo containing all information
     * @return the saved review object
     * @throws IOException Exception when something goes wrong
     * @throws InterruptedException Exception when something goes wrong
     */
    public Review createReview(ReviewInfo reviewInfo)
            throws IOException, InterruptedException {
        Review review = new Review();

        review.setTextualReview(reviewInfo.getTextualReview());
        review.setCourseCode(reviewInfo.getCourseCode());
        review.setRating(reviewInfo.getRating());
        review.setUsername(reviewInfo.getUsername());

        return reviewRepository.save(review);
    }

    /**
     * Gets all ratings of TA's for a course.
     *
     * @param courseCode the courseCode of the course
     * @return List of ratings
     * @throws IOException Exception when something goes wrong
     * @throws InterruptedException Exception when something goes wrong
     * @throws EmptyTargetException Exception when no reviews found
     */
    public List<Integer> getRatingsByCourse(String courseCode)
            throws IOException, InterruptedException, EmptyTargetException {
        List<Review> reviews =
                reviewRepository.findAllByCourseCode(courseCode);

        if (reviews.isEmpty()) {
            throw new EmptyTargetException("No candidates or reviews for this course yet.");
        }

        List<Integer> ratings = new ArrayList<>();
        reviews.forEach(review -> ratings.add(review.getRating()));

        return ratings;
    }

    /**
     * Get all ratings for a user.
     *
     * @param username username of the user
     * @return List of ratings
     * @throws EmptyTargetException Exception when no reviews found
     */
    public List<Integer> getRatingsByUsername(String username) throws EmptyTargetException {
        List<Review> reviews =
                reviewRepository.findAllByUsername(username);

        if (reviews.isEmpty()) {
            throw new EmptyTargetException("No reviews with this username and/or course code.");
        }

        List<Integer> ratings = new ArrayList<>();
        reviews.forEach(review -> ratings.add(review.getRating()));
        return ratings;
    }
}
