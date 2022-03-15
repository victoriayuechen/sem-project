package nl.tudelft.sem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.Criteria;
import nl.tudelft.sem.Status;
import nl.tudelft.sem.communication.TaCommunicator;
import nl.tudelft.sem.entities.Application;
import nl.tudelft.sem.repositories.ApplicationRepository;
import nl.tudelft.sem.util.ApplicationBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ApplicationDataTest {
    private transient ApplicationRepository applicationRepository =
            Mockito.mock(ApplicationRepository.class);
    private transient TaCommunicator taCommunicator = Mockito.mock(TaCommunicator.class);
    private final transient ApplicationDataService applicationDataService =
            new ApplicationDataService(applicationRepository,
                    taCommunicator);

    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJz"
            + "dWIiOiJhbm5pYmFsZSIsImV4cCI6MTY0MDMw"
            + "NjkyMywiaWF0IjoxNjQwMjcwOTIzfQ.Ab3qsQdzo"
            + "U8viZwWtnFf9NqIG9GDsSssTxrjyXj_8Dg";
    private final transient String courseCode = "CSE2115";
    private final transient String name = "Bob";
    private final transient String name1 = "Alice";
    private final transient String name2 = "Jack";

    @Test
    public void gradeOverviewOfCandidate() throws Exception {
        Application testApplication = new ApplicationBuilder()
                .withGrade(8.0)
                .build(0L);
        List<String> testExperiences = Collections.emptyList();
        HashMap<String, Object> testHmap = new HashMap<String, Object>();
        testHmap.put("grade", 8.0);
        testHmap.put("experience", testExperiences);

        when(applicationRepository
                .findApplicationsByApplicationId(0L))
                .thenReturn(Optional.of(testApplication));
        when(taCommunicator
                .obtainExperiences("Victoria", TOKEN))
                .thenReturn(testExperiences);

        assertEquals(applicationDataService
                .obtainApplicationData(testApplication.getApplicationId(), TOKEN), testHmap);
    }

    @Test
    public void experiencesOverviewOfCandidate() throws Exception {

        List<String> testExperiences = new ArrayList<>();
        testExperiences.add(courseCode);
        testExperiences.add("CSE4224");
        HashMap<String, Object> testHmap = new HashMap<String, Object>();
        testHmap.put("grade", 8.0);
        testHmap.put("experience", testExperiences);
        Application testApplication = new ApplicationBuilder()
                .withGrade(8.0)
                .build(0L);
        when(applicationRepository
                .findApplicationsByApplicationId(0L))
                .thenReturn(Optional.of(testApplication));
        when(taCommunicator
                .obtainExperiences("Victoria", TOKEN))
                .thenReturn(testExperiences);

        assertEquals(applicationDataService
                .obtainApplicationData(testApplication.getApplicationId(), TOKEN), testHmap);
    }

    @Test
    public void experienceRecommendationTest() throws Exception {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(name)
                .build(0L);
        Application app2 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(name2)
                .build(1L);
        Application app3 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(name1)
                .build(2L);

        List<Application> applications = List.of(app1, app2, app3);
        List<String> exp1 = List.of("CSE2225", "CSE2233", "CSE2244");
        List<String> exp2 = List.of("CSE2242", "CSE2233");
        List<String> exp3 = List.of("CSE2322", "CSE2233", "CSE2244", "CSE1222");

        when(applicationRepository
                .findApplicationsByCourseCode(courseCode))
                .thenReturn(applications);
        when(taCommunicator
                .obtainExperiences(name, TOKEN))
                .thenReturn(exp1);
        when(taCommunicator
                .obtainExperiences(name2, TOKEN))
                .thenReturn(exp2);
        when(taCommunicator
                .obtainExperiences(name1, TOKEN))
                .thenReturn(exp3);

        List<Criteria> criterias = new ArrayList<>();
        criterias.add(Criteria.EXPERIENCE);
        List<Application> applicationsResult = List.of(app3, app1, app2);
        assertEquals(applicationsResult, applicationDataService
                .recommendApplicants(courseCode, criterias, TOKEN));
    }

    @Test
    public void ratingRecommendationTest() throws Exception {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(name)
                .build(0L);
        Application app2 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(name2)
                .build(1L);
        Application app3 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(name1)
                .build(2L);

        List<Application> applications = List.of(app1, app2, app3);
        List<Integer> rat1 = List.of(8, 6, 7);
        List<Integer> rat2 = List.of(5, 4, 6);
        List<Integer> rat3 = List.of(9, 10, 8, 9);

        when(applicationRepository
                .findApplicationsByCourseCode(courseCode))
                .thenReturn(applications);
        when(taCommunicator
                .obtainRatings(name, TOKEN))
                .thenReturn(rat1);
        when(taCommunicator
                .obtainRatings(name2, TOKEN))
                .thenReturn(rat2);
        when(taCommunicator
                .obtainRatings(name1, TOKEN))
                .thenReturn(rat3);

        List<Criteria> criterias = new ArrayList<>();
        criterias.add(Criteria.RATING);
        List<Application> applicationsResult = List.of(app3, app1, app2);
        assertEquals(applicationsResult, applicationDataService
                .recommendApplicants(courseCode, criterias, TOKEN));
    }

    @Test
    public void ratingAndExperienceRecommendationTest() throws Exception {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(name)
                .build(0L);
        Application app2 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(name2)
                .build(1L);
        Application app3 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(name1)
                .build(2L);

        List<Application> applications = List.of(app1, app2, app3);
        List<Integer> rat1 = List.of(8, 6, 7);
        List<Integer> rat2 = List.of(5, 4, 6);
        List<Integer> rat3 = List.of(9, 10, 8, 9);
        List<String> exp1 = List.of("CSE2222", "CSE2263", "CSE2274");
        List<String> exp2 = List.of("CSE2222", "CSE2273");
        List<String> exp3 = List.of("CSE2222", "CSE2283", "CSE2284", "CSE1222");

        when(applicationRepository
                .findApplicationsByCourseCode(courseCode))
                .thenReturn(applications);
        when(taCommunicator
                .obtainRatings(name, TOKEN))
                .thenReturn(rat1);
        when(taCommunicator
                .obtainRatings(name2, TOKEN))
                .thenReturn(rat2);
        when(taCommunicator
                .obtainRatings(name1, TOKEN))
                .thenReturn(rat3);
        when(taCommunicator
                .obtainExperiences(name, TOKEN))
                .thenReturn(exp1);
        when(taCommunicator
                .obtainExperiences(name2, TOKEN))
                .thenReturn(exp2);
        when(taCommunicator
                .obtainExperiences(name1, TOKEN))
                .thenReturn(exp3);


        List<Criteria> criterias = new ArrayList<>();
        criterias.add(Criteria.RATING);
        criterias.add(Criteria.EXPERIENCE);
        List<Application> applicationsResult = List.of(app3, app1, app2);
        assertEquals(applicationsResult, applicationDataService
                .recommendApplicants(courseCode, criterias, TOKEN));
    }
}
