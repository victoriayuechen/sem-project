package nl.tudelft.sem.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import nl.tudelft.sem.ReviewInfo;
import nl.tudelft.sem.entities.Review;
import nl.tudelft.sem.exceptions.EmptyTargetException;
import nl.tudelft.sem.repositories.ReviewRepository;
import nl.tudelft.sem.util.ReviewBuilder;
import nl.tudelft.sem.util.ReviewInfoBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ReviewServiceTest {
    private final transient ReviewRepository reviewRepository =
            Mockito.mock(ReviewRepository.class);
    private transient ReviewService reviewService = new ReviewService(reviewRepository);

    private transient Review review1;
    private transient Review review2;
    private transient Review review3;
    private transient ReviewInfo reviewInfo1;

    /**
     * Setup review entities used for testing.
     */
    @BeforeEach
    public void setup() {
        review1 = new Review();
        review1.setTextualReview("Magnificent");
        review1.setCourseCode("CSE2115");
        review1.setRating(5);
        review1.setUsername("Alex");

        review2 = new Review();
        review2.setCourseCode("CSE2115");
        review2.setRating(5);
        review2.setTextualReview("Excellent");
        review2.setUsername("Radu");

        review3 = new Review();
        review3.setUsername("Alex");
        review3.setCourseCode("CSE1105");
        review3.setRating(1);
        review3.setTextualReview("Odious");

        reviewInfo1 = new ReviewInfoBuilder().withTextualReview("Magnificent")
                .withRating(5).withUsername("Alex").withCourseCode("CSE2115")
                .build();
    }

    @Test
    public void deleteReviewEmptyTest() throws Exception {
        when(reviewRepository.findById(0L)).thenReturn(Optional.empty());

        assertThrows(EmptyTargetException.class, () -> reviewService.deleteReview("0"));

        verify(reviewRepository, never()).delete(any(Review.class));
    }

    @Test
    public void updateReviewEmptyTest() throws Exception {
        ReviewInfo reviewInfo = new ReviewInfoBuilder().build();

        when(reviewRepository
                .findAllByUsernameAndCourseCode(
                        reviewInfo.getUsername(),
                        reviewInfo.getCourseCode()))
                .thenReturn(Optional.empty());

        assertThrows(EmptyTargetException.class, () -> reviewService.updateReview(reviewInfo));

        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    public void deleteReviewTest() throws Exception {
        Review review = new ReviewBuilder().build(0L);

        when(reviewRepository.findById(0L)).thenReturn(Optional.of(review));

        assertEquals(review, reviewService.deleteReview("0"));

        verify(reviewRepository, times(1)).deleteById(0L);
    }

    @Test
    public void updateReviewTest() throws Exception {
        ReviewInfo reviewInfo = new ReviewInfoBuilder().build();

        Review review = new ReviewBuilder(reviewInfo).build(0L);
        ReviewInfo update = new ReviewInfoBuilder().withRating(2).withTextualReview("ha").build();

        Review expected = new ReviewBuilder(update).build(0L);

        when(reviewRepository
                .findAllByUsernameAndCourseCode(
                        reviewInfo.getUsername(),
                        reviewInfo.getCourseCode()))
                .thenReturn(Optional.of(review));

        assertEquals(update, reviewService.updateReview(update));

        verify(reviewRepository, times(1)).save(expected);
    }

    @Test
    public void getReviewByCourseTest() throws EmptyTargetException {
        Review review = new ReviewBuilder().build(0L);
        when(reviewRepository
                .findAllByUsernameAndCourseCode(review.getUsername(), review.getCourseCode()))
                .thenReturn(Optional.of(review));
        Assertions.assertEquals(review, reviewService
                .getReviewByCourse(review.getUsername(), review.getCourseCode()));
    }

    @Test
    public void getReviewByCourseExceptionTest() {
        Review review = new ReviewBuilder().build(0L);
        when(reviewRepository
                .findAllByUsernameAndCourseCode(review.getUsername(), review.getCourseCode()))
                .thenReturn(Optional.empty());
        assertThrows(EmptyTargetException.class, () ->
                reviewService.getReviewByCourse(review.getUsername(), review.getCourseCode())
        );
    }

    @Test
    public void getRatingsByUsernameTest() throws EmptyTargetException {
        when(reviewRepository.findAllByUsername(review1.getUsername()))
                .thenReturn(Arrays.asList(review1, review3));

        assertEquals(Arrays.asList(5, 1),
                reviewService.getRatingsByUsername(review1.getUsername()));
    }

    @Test
    public void getRatingsByUsernameEmptyTest() {
        when(reviewRepository.findAllByUsername("Radu"))
                .thenReturn(new ArrayList<>());

        assertThrows(EmptyTargetException.class, () ->
                reviewService.getRatingsByUsername("Radu"));
    }

    @Test
    public void createReviewTest() throws IOException, InterruptedException {
        when(reviewRepository.save(review1)).thenReturn(review1);

        assertEquals(reviewService.createReview(reviewInfo1), review1);

        verify(reviewRepository, times(1)).save(review1);
    }

    @Test
    public void getRatingsByCourseTest() throws Exception {
        when(reviewRepository.findAllByCourseCode(review1.getCourseCode()))
                .thenReturn(Arrays.asList(review1, review2));

        assertEquals(Arrays.asList(5, 5),
                reviewService.getRatingsByCourse(review1.getCourseCode()));
    }

    @Test
    public void getRatingsByEmptyCourseTest() throws IOException, InterruptedException {
        when(reviewRepository.findAllByCourseCode("CSE1400"))
                .thenReturn(new ArrayList<>());

        assertThrows(EmptyTargetException.class, () ->
                reviewService.getRatingsByCourse("CSE1400"));
    }
}
