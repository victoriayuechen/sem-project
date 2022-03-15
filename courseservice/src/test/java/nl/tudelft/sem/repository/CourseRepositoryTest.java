package nl.tudelft.sem.repository;

import static org.assertj.core.api.Assertions.assertThat;

import nl.tudelft.sem.entities.Course;
import nl.tudelft.sem.repositories.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CourseRepositoryTest {

    @Autowired
    private transient TestEntityManager entityManager;

    @Autowired
    private transient CourseRepository courseRepository;

    private transient Course course;

    /**
     * Initialises the required objects.
     */
    @BeforeEach
    public void setup() {
        course = new Course();
        course.setCourseCode("CSE2115");
        course.setCourseName("Software Engineering Methods");
        course.setDuration(10);
        course.setAverageTaHour(10.0);
        course.setOpen(true);
        course.setNumberOfStudents(200);
        course.setNumberOfTas(20);
        course.setQuarter(2);
        course.setStudentTaRatio(20);
    }

    @Test
    public void findByIdTest() {
        entityManager.merge(course);
        entityManager.flush();

        assertThat(course.equals(courseRepository.findById(course.getCourseCode())));
    }

    @Test
    public void findAllByQuarter() {
        entityManager.merge(course);
        entityManager.flush();

        assertThat(course.equals(courseRepository
                .findAllByQuarter(course.getQuarter())));
    }

    @Test
    public void getRecruitmentTest() {
        entityManager.merge(course);
        entityManager.flush();

        assertThat(course.equals(courseRepository
                .getRecruitment(course.getCourseCode())));
    }
}
