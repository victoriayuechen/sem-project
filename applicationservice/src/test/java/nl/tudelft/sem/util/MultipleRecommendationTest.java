package nl.tudelft.sem.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.LinkedList;
import java.util.List;
import nl.tudelft.sem.Status;
import nl.tudelft.sem.communication.TaCommunicator;
import nl.tudelft.sem.entities.Application;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class MultipleRecommendationTest {
    private final transient TaCommunicator taCommunicator = Mockito.mock(TaCommunicator.class);
    private final transient List<Integer> rat1 = List.of(8, 6, 7);
    private final transient List<Integer> rat2 = List.of(5, 4, 6);
    private final transient List<Integer> rat3 = List.of(9, 10, 8, 9);
    private final transient List<String> exp1 = List.of("CSE2222", "CSE2233", "CSE2244", "CSE1222");
    private final transient List<String> exp2 = List.of("CSE2222", "CSE2233", "CSE2244");
    private final transient List<String> exp3 = List.of("CSE2222", "CSE2233", "CSE2244");
    private final transient Application app1 = new ApplicationBuilder()
            .withStatus(Status.PENDING)
            .withName("Bob")
            .build(0L);
    private final transient Application app2 = new ApplicationBuilder()
            .withStatus(Status.PENDING)
            .withName("Jack")
            .build(1L);
    private final transient Application app3 = new ApplicationBuilder()
            .withStatus(Status.PENDING)
            .withName("Alice")
            .build(2L);
    private final transient String bob = "Bob";
    private final transient String jack = "Jack";
    private final transient String alice = "Alice";
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJz"
            + "dWIiOiJhbm5pYmFsZSIsImV4cCI6MTY0MDMw"
            + "NjkyMywiaWF0IjoxNjQwMjcwOTIzfQ.Ab3qsQdzo"
            + "U8viZwWtnFf9NqIG9GDsSssTxrjyXj_8Dg";


    @Test
    public void ratingAndExperienceRecommendationTest() throws Exception {
        List<Application> applications = new LinkedList<>();
        applications.add(app1);
        applications.add(app2);
        applications.add(app3);

        when(taCommunicator
                .obtainRatings(bob, TOKEN))
                .thenReturn(rat1);
        when(taCommunicator
                .obtainRatings(jack, TOKEN))
                .thenReturn(rat2);
        when(taCommunicator
                .obtainRatings(alice, TOKEN))
                .thenReturn(rat3);
        when(taCommunicator
                .obtainExperiences(bob, TOKEN))
                .thenReturn(exp1);
        when(taCommunicator
                .obtainExperiences(jack, TOKEN))
                .thenReturn(exp2);
        when(taCommunicator
                .obtainExperiences(alice, TOKEN))
                .thenReturn(exp3);


        Recommendation recommendation =
                new RatingRecommendation(applications, taCommunicator, TOKEN);
        List<Application> result = recommendation.recommend(TOKEN);
        recommendation = new ExperienceRecommendation(result, taCommunicator, TOKEN);
        List<Application> result2 = recommendation.recommend(TOKEN);

        assertThat(result2).containsExactly(app1, app3, app2);
    }

    @Test
    public void experienceAndRatingRecommendationTest() throws Exception {
        List<Application> applications = new LinkedList<>();
        applications.add(app1);
        applications.add(app2);
        applications.add(app3);

        when(taCommunicator
                .obtainRatings(bob, TOKEN))
                .thenReturn(rat1);
        when(taCommunicator
                .obtainRatings(jack, TOKEN))
                .thenReturn(rat2);
        when(taCommunicator
                .obtainRatings(alice, TOKEN))
                .thenReturn(rat3);
        when(taCommunicator
                .obtainExperiences(bob, TOKEN))
                .thenReturn(exp1);
        when(taCommunicator
                .obtainExperiences(jack, TOKEN))
                .thenReturn(exp2);
        when(taCommunicator
                .obtainExperiences(alice, TOKEN))
                .thenReturn(exp3);


        Recommendation recommendation =
                new ExperienceRecommendation(applications, taCommunicator, TOKEN);
        List<Application> result = recommendation.recommend(TOKEN);
        recommendation = new RatingRecommendation(result, taCommunicator, TOKEN);
        List<Application> result2 = recommendation.recommend(TOKEN);

        assertThat(result2).containsExactly(app3, app1, app2);
    }
}
