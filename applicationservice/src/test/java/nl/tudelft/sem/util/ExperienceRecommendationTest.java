package nl.tudelft.sem.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertTrue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import nl.tudelft.sem.Status;
import nl.tudelft.sem.communication.TaCommunicator;
import nl.tudelft.sem.entities.Application;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;


public class ExperienceRecommendationTest {
    private final transient TaCommunicator taCommunicator = Mockito.mock(TaCommunicator.class);
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJz"
            + "dWIiOiJhbm5pYmFsZSIsImV4cCI6MTY0MDMw"
            + "NjkyMywiaWF0IjoxNjQwMjcwOTIzfQ.Ab3qsQdzo"
            + "U8viZwWtnFf9NqIG9GDsSssTxrjyXj_8Dg";

    @Test
    public void experienceSortTest() throws Exception {
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

        List<Application> applications = new LinkedList<>();
        applications.add(app1);
        applications.add(app2);
        applications.add(app3);
        List<String> exp1 = List.of("CSE2222", "CSE2233", "CSE2244");
        List<String> exp2 = List.of("CSE2222", "CSE2233");
        List<String> exp3 = List.of("CSE2222", "CSE2233", "CSE2244", "CSE1222");

        when(taCommunicator
                .obtainExperiences("Bob", TOKEN))
            .thenReturn(exp1);
        when(taCommunicator
                .obtainExperiences("Jack", TOKEN))
                .thenReturn(exp2);
        when(taCommunicator
                .obtainExperiences("Alice", TOKEN))
                .thenReturn(exp3);

        Recommendation recommendation =
                new ExperienceRecommendation(applications, taCommunicator, TOKEN);
        List<Application> result = recommendation.recommend(TOKEN);

        assertThat(result).containsExactly(app3, app1, app2);

    }
}
