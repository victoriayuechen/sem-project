package nl.tudelft.sem.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.ErrorString;
import nl.tudelft.sem.ReviewInfo;
import nl.tudelft.sem.entities.Review;
import nl.tudelft.sem.exceptions.EmptyTargetException;
import nl.tudelft.sem.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/review")
public class ReviewController {
    @Autowired
    private transient ReviewService reviewService;
    private final transient String lectureAuthority =
            "hasAnyAuthority('ROLE_LECTURER', 'ROLE_ADMIN')";

    /**
     * Retrieves all reviews of a TA on a certain course.
     *
     * @param request    The request object.
     * @param username   The username of the TA.
     * @param courseCode The course code of the course from which the review came.
     * @return the review object.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_LECTURER', 'ROLE_ADMIN')")
    @GetMapping("/getReview/{username}/{courseCode}")
    public ResponseEntity<?> getReviewByCourse(HttpServletRequest request,
                                               @PathVariable String username,
                                               @PathVariable String courseCode) {
        try {
            Review review = reviewService.getReviewByCourse(username, courseCode);
            return ResponseEntity.ok(review);
        } catch (EmptyTargetException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    /** Retrieves all ratings of a TA.
     *
     * @param request       The request object.
     * @param username      The username of the TA.
     * @return the ratings as a list of integers.
     */

    @PreAuthorize(lectureAuthority)
    @GetMapping("/getRatings/user/{username}")
    public ResponseEntity<?> getRatingsByUsername(HttpServletRequest request,
                                                  @PathVariable String username) {
        try {
            List<Integer> ratings = reviewService.getRatingsByUsername(username);
            return ResponseEntity.ok(ratings);
        } catch (EmptyTargetException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }

    }

    /** Endpoint to save a new review in the database.
     *
     * @param request The request of the client
     * @param reviewInfo The content of the future review.
     * @return The review saved in the database.
     */
    @PreAuthorize(lectureAuthority)
    @PostMapping("/create")
    public ResponseEntity<?> createReview(HttpServletRequest request,
                                          @RequestBody ReviewInfo reviewInfo) {
        try {
            Review review = reviewService.createReview(reviewInfo);
            return ResponseEntity
                    .ok()
                    .body(review);
        } catch (IOException | InterruptedException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorString(e.getMessage()));
        }
    }

    /** Retrieves all ratings of a candidates for a course.
     *
     * @param request       The request object.
     * @param courseCode    The courseCode of the course to get ratings of
     * @return the ratings as a list of integers.
     */
    @PreAuthorize(lectureAuthority)
    @GetMapping("/getRatings/course/{courseCode}")
    public ResponseEntity<?> getRatingsByCourse(HttpServletRequest request,
                                                @PathVariable String courseCode) {
        try {
            List<Integer> ratings = reviewService.getRatingsByCourse(courseCode);
            return ResponseEntity
                    .ok()
                    .body(ratings);
        } catch (IOException | InterruptedException | EmptyTargetException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorString(e.getMessage()));
        }
    }

    /**
     * Endpoint to update review in the database.
     *
     * @param request The request object
     * @param reviewInfo The updated ReviewInfo entity
     * @return The updated review entity as confirmation
     */
    @PreAuthorize(lectureAuthority)
    @PutMapping("/updateReview")
    public ResponseEntity<?> updateReview(HttpServletRequest request,
                                          @RequestBody ReviewInfo reviewInfo) {
        try {
            ReviewInfo updatedReview = reviewService.updateReview(reviewInfo);
            return ResponseEntity
                    .ok()
                    .body(updatedReview);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorString(e.getMessage()));
        }
    }


    /**
     * Endpoint to delete a review by id.
     *
     * @param request The request object
     * @param reviewId The ID of the review as a String
     * @return The deleted review entity
     */
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @DeleteMapping("/deleteReview/{reviewId}")
    public ResponseEntity<?> deleteReview(HttpServletRequest request,
                                          @PathVariable String reviewId) {
        try {
            Review review = reviewService.deleteReview(reviewId);
            return ResponseEntity
                    .ok()
                    .body(review);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorString(e.getMessage()));
        }
    }
}
