package nl.tudelft.sem.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import nl.tudelft.sem.ReviewInfo;
import nl.tudelft.sem.entities.Review;
import nl.tudelft.sem.exceptions.EmptyTargetException;
import nl.tudelft.sem.service.ReviewService;
import nl.tudelft.sem.util.ReviewBuilder;
import nl.tudelft.sem.util.ReviewInfoBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReviewController.class)
public class ReviewControllerTest {
    @MockBean
    private transient ReviewService reviewService;

    @Autowired
    private transient MockMvc mockMvc;
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJz"
            + "dWIiOiJhbm5pYmFsZSIsImV4cCI6MTY0MDMw"
            + "NjkyMywiaWF0IjoxNjQwMjcwOTIzfQ.Ab3qsQdzo"
            + "U8viZwWtnFf9NqIG9GDsSssTxrjyXj_8Dg";
    private static final String AUTHORIZATION = "Authorization";
    private transient Review review1;
    private transient Review review2;
    private transient Review review3;
    private transient ReviewInfo reviewInfo1;
    private final transient String admin = "ADMIN";

    /**
     * Generic JSON parser.
     *
     * @param obj Object of any class
     * @return JSON String used for requests/response
     */
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "-";
    }

    /**
     * reviews used for testing.
     */
    @BeforeEach
    public void setup() {
        review1 = new Review();
        review1.setTextualReview("Magnificent");
        review1.setCourseCode("CSE21155");
        review1.setRating(5);
        review1.setUsername("Alexx");

        review2 = new Review();
        review2.setCourseCode("CSE2114");
        review2.setRating(5);
        review2.setTextualReview("Excellent");
        review2.setUsername("Radu");

        review3 = new Review();
        review3.setUsername("Alexd");
        review3.setCourseCode("CSE1105");
        review3.setRating(1);
        review3.setTextualReview("Odious");

        reviewInfo1 = new ReviewInfoBuilder().withTextualReview("Magnificent")
                .withRating(5).withUsername("Alex").withCourseCode("CSE2115")
                .build();
    }

    @WithMockUser(roles = admin)
    @Test
    public void getRatingsByCourseTest() throws Exception {
        when(reviewService.getRatingsByCourse("CSE2115")).thenReturn(Arrays.asList(5, 5));

        mockMvc.perform(get("/review/getRatings/course/{courseCode}", "CSE2115")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(Arrays.asList(5, 5))));
    }

    @WithMockUser(roles = admin)
    @Test
    public void updateReviewNullTest() throws Exception {
        mockMvc.perform(put("/review/updateReview")
                .content(asJsonString(null))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = admin)
    @Test
    public void updateReviewEmptyTest() throws Exception {
        ReviewInfo reviewInfo = new ReviewInfoBuilder().build();

        when(reviewService.updateReview(reviewInfo)).thenThrow(new EmptyTargetException(""));

        mockMvc.perform(put("/review/updateReview")
                .content(asJsonString(reviewInfo))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = admin)
    @Test
    public void updateReviewTest() throws Exception {
        ReviewInfo update = new ReviewInfoBuilder().withRating(2).build();

        when(reviewService.updateReview(update)).thenReturn(update);

        mockMvc.perform(put("/review/updateReview")
                .content(asJsonString(update))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(update)));
    }

    @WithMockUser(roles = admin)
    @Test
    public void deleteReviewEmptyTest() throws Exception {
        when(reviewService.deleteReview("0")).thenThrow(new EmptyTargetException(""));

        mockMvc.perform(delete("/review/deleteReview/{reviewId}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = admin)
    @Test
    public void deleteReviewTest() throws Exception {
        Review review = new ReviewBuilder().build(0L);

        when(reviewService.deleteReview("0")).thenReturn(review);

        mockMvc.perform(delete("/review/deleteReview/{reviewId}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(review)));
    }

    @WithMockUser(roles = admin)
    @Test
    public void getReviewTest() throws Exception {
        Review review = new ReviewBuilder().build(0L);

        when(reviewService.getReviewByCourse(review.getUsername(),
                review.getCourseCode())).thenReturn(review);

        mockMvc.perform(get("/review/getReview/{username}/{courseCode}",
                review.getUsername(), review.getCourseCode())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(review)));
    }

    @WithMockUser(roles = admin)
    @Test
    public void getNoReviewTest() throws Exception {
        Review review = new ReviewBuilder().build(0L);

        when(reviewService.getReviewByCourse(review.getUsername(),
                review.getCourseCode())).thenThrow(EmptyTargetException.class);

        mockMvc.perform(get("/review/getReview/{username}/{courseCode}",
                review.getUsername(), review.getCourseCode())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = admin)
    @Test
    public void createReviewTest() throws Exception {
        when(reviewService.createReview(reviewInfo1)).thenReturn(review1);

        mockMvc.perform(post("/review/create")
                .content(asJsonString(reviewInfo1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(review1)));
    }

    @WithMockUser(roles = admin)
    @Test
    public void createReviewFailTest() throws Exception {
        when(reviewService.createReview(reviewInfo1)).thenThrow(new IOException(""));

        mockMvc.perform(post("/review/create")
                .content(asJsonString(reviewInfo1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = admin)
    @Test
    public void getRatingsByEmptyCourseTest() throws Exception {

        when(reviewService.getRatingsByCourse("CSE1400")).thenThrow(new EmptyTargetException(""));

        mockMvc.perform(get("/review/getRatings/course/{courseCode}", "CSE1400")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = admin)
    @Test
    public void getRatingsByUsernameTest() throws Exception {
        when(reviewService.getRatingsByUsername("Alex")).thenReturn(Arrays.asList(5, 1));

        mockMvc.perform(get("/review/getRatings/user/{username}", "Alex")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(Arrays.asList(5, 1))));
    }

    @WithMockUser(roles = admin)
    @Test
    public void getRatingsByEmptyUsernameTest() throws Exception {
        when(reviewService.getRatingsByUsername("Radu")).thenThrow(new EmptyTargetException(""));

        mockMvc.perform(get("/review/getRatings/user/{username}", "Radu")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
