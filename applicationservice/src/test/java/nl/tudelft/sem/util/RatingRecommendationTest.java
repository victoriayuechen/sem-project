package nl.tudelft.sem.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertTrue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import nl.tudelft.sem.Status;
import nl.tudelft.sem.communication.TaCommunicator;
import nl.tudelft.sem.controller.ApplicationController;
import nl.tudelft.sem.entities.Application;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(RatingRecommendation.class)
class RatingRecommendationTest {

    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJz"
            + "dWIiOiJhbm5pYmFsZSIsImV4cCI6MTY0MDMw"
            + "NjkyMywiaWF0IjoxNjQwMjcwOTIzfQ.Ab3qsQdzo"
            + "U8viZwWtnFf9NqIG9GDsSssTxrjyXj_8Dg";

    @MockBean
    private transient TaCommunicator taCommunicator;

    @Test
    public void ratingSortTest() throws Exception {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName("Bob")
                .build(0L);
        Application app2 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName("Jack")
                .build(1L);
        Application app3 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName("Alice")
                .build(2L);

        List<Application> applications = List.of(app1, app2, app3);
        applications = new ArrayList<>(applications);
        List<Integer> rat1 = List.of(8, 6, 7);
        List<Integer> rat2 = List.of(5, 4, 6);
        List<Integer> rat3 = List.of(9, 10, 8, 9);

        when(taCommunicator
                .obtainRatings("Bob", TOKEN))
                .thenReturn(rat1);
        when(taCommunicator
                .obtainRatings("Jack", TOKEN))
                .thenReturn(rat2);
        when(taCommunicator
                .obtainRatings("Alice", TOKEN))
                .thenReturn(rat3);

        Recommendation recommendation =
                new RatingRecommendation(applications, taCommunicator, TOKEN);
        List<Application> result = recommendation.recommend(TOKEN);

        assertThat(result).containsExactly(app3, app1, app2);
    }
}