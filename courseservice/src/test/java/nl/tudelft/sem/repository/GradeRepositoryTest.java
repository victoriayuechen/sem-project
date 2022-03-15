package nl.tudelft.sem.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import nl.tudelft.sem.entities.Grade;
import nl.tudelft.sem.repositories.GradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GradeRepositoryTest {

    @Autowired
    private transient TestEntityManager entityManager;

    @Autowired
    private transient GradeRepository gradeRepository;

    private transient Grade grade1;
    private transient Grade grade2;

    /**
     * Initialises all the required objects.
     */
    @BeforeEach
    public void setup() {
        grade1 = new Grade();
        grade1.setCourseCode("CSE2115");
        grade1.setId(1);
        grade1.setUserName("ljpdeswart");
        grade1.setValue(9.0);

        grade2 = new Grade();
        grade2.setCourseCode("CSE1105");
        grade2.setId(2);
        grade2.setUserName("ljpdeswart");
        grade2.setValue(6.0);
    }

    @Test
    public void findGradeByCourseCodeAndAndUserNameTest() {
        entityManager.merge(grade1);
        entityManager.flush();

        assertThat(grade1.equals(gradeRepository
                .findGradeByCourseCodeAndAndUserName(grade1.getCourseCode(),
                        grade1.getUserName())));
    }

    @Test
    public void selectApplicableCoursesByUsernameTest() {
        entityManager.merge(grade1);
        entityManager.merge(grade2);
        entityManager.flush();

        assertThat(Arrays.asList(grade1.getCourseCode(), grade2.getCourseCode())
                .equals(gradeRepository.selectApplicableCoursesByUsername(grade1.getUserName())));
    }
}
