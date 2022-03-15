package nl.tudelft.sem.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import nl.tudelft.sem.Status;
import nl.tudelft.sem.entities.Application;
import nl.tudelft.sem.repositories.ApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ApplicationRepositoryTest {

    private transient Application application1;
    private transient Application application2;
    private transient Application application3;

    @Autowired
    private transient TestEntityManager entityManager;

    @Autowired
    private transient ApplicationRepository applicationRepository;

    /**
     * Initialises all the necessary objects.
     */
    @BeforeEach
    public void setup() {
        application1 = new Application();
        application1.setStatus(Status.PENDING);
        application1.setUsername("Alex");
        application1.setCourseCode("CSE2115");
        application1.setQuarter(2);

        application2 = new Application();
        application2.setStatus(Status.PENDING);
        application2.setUsername("Alex");
        application2.setCourseCode("CSE1305");
        application2.setQuarter(2);

        application3 = new Application();
        application3.setStatus(Status.PENDING);
        application3.setUsername("Tony Stark");
        application3.setCourseCode("CSE2115");
        application3.setQuarter(2);
    }

    @Test
    public void findByCourseCodeTest() {
        entityManager.merge(application2);
        entityManager.flush();

        assertEquals(List.of(application2), applicationRepository
                .findByCourseCode(application2.getCourseCode()));
    }

    //@Test
    //public void findApplicationsByApplicationIdTest() {
    //    application2.setApplicationId(1L);
    //
    //    entityManager.merge(application2);
    //    entityManager.flush();
    //
    //    assertEquals(application2, applicationRepository
    //            .findApplicationsByApplicationId(application2.getApplicationId()).get());
    //}

    @Test
    public void findApplicationsByCourseCodeTest() {
        entityManager.merge(application1);
        entityManager.merge(application3);
        entityManager.flush();

        assertThat(Arrays.asList(application1, application3)
                .equals(applicationRepository.findByCourseCode(application1.getCourseCode())));
    }

    @Test
    public void findApplicationsByUsername() {
        entityManager.merge(application1);
        entityManager.merge(application2);
        entityManager.flush();

        assertThat(Arrays.asList(application1, application2)
                .equals(applicationRepository.findApplicationsByUsername(
                        application1.getUsername())));
    }

    @Test
    public void findApplicationsByUsernameAndCourseCode() {
        entityManager.merge(application2);
        entityManager.flush();

        assertThat(application2.equals(applicationRepository
                .findApplicationsByUsernameAndCourseCode(application2.getUsername(),
                        application2.getCourseCode())));
    }

    @Test
    public void findApplicationsByUsernameAndQuarterTest() {
        entityManager.merge(application1);
        entityManager.merge(application2);
        entityManager.flush();

        assertThat(Arrays.asList(application1, application2)
                .equals(applicationRepository.findApplicationsByUsernameAndQuarter(
                        application1.getUsername(),
                        application1.getQuarter())));
    }
}
