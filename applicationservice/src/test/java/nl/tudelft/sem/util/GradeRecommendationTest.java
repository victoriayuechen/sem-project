package nl.tudelft.sem.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.AssertionErrors.assertTrue;

import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.Status;
import nl.tudelft.sem.entities.Application;
import org.junit.jupiter.api.Test;

public class GradeRecommendationTest {

    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJz"
            + "dWIiOiJhbm5pYmFsZSIsImV4cCI6MTY0MDMw"
            + "NjkyMywiaWF0IjoxNjQwMjcwOTIzfQ.Ab3qsQdzo"
            + "U8viZwWtnFf9NqIG9GDsSssTxrjyXj_8Dg";

    @Test
    public void gradeSortTest() {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName("Bob")
                .withGrade(8.0)
                .build(0L);
        Application app2 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName("Jack")
                .withGrade(5.0)
                .build(1L);
        Application app3 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName("Alice")
                .withGrade(6.0)
                .build(2L);

        List<Application> applications = List.of(app1, app2, app3);
        applications = new ArrayList<>(applications);
        Recommendation recommendation = new GradeRecommendation(applications);
        List<Application> result = recommendation.recommend(TOKEN);

        assertThat(result).containsExactly(app1, app3, app2);
    }
}
