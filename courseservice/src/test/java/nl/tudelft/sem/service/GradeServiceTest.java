package nl.tudelft.sem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import nl.tudelft.sem.GradeInfo;
import nl.tudelft.sem.entities.Course;
import nl.tudelft.sem.entities.Grade;
import nl.tudelft.sem.exceptions.EmptyTargetException;
import nl.tudelft.sem.repositories.GradeRepository;
import nl.tudelft.sem.util.GradeBuilder;
import nl.tudelft.sem.util.GradeInfoBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.test.context.support.WithMockUser;

public class GradeServiceTest {
    private final transient GradeRepository gradeRepository =
            Mockito.mock(GradeRepository.class);
    private transient GradeService gradeService =
            new GradeService(gradeRepository);
    private final transient String admin = "ADMIN";

    @WithMockUser(roles = admin)
    @Test
    public void deleteGradeTest() throws Exception {
        Grade grade = new GradeBuilder().build(0L);

        when(gradeRepository.findById(0L)).thenReturn(Optional.of(grade));

        assertEquals(grade, gradeService.deleteGrade("0"));

        verify(gradeRepository, times(1)).deleteById(0L);
    }

    @WithMockUser(roles = admin)
    @Test
    public void updateGradeTest() throws Exception {
        GradeInfo gradeInfo = new GradeInfoBuilder().build();

        Grade grade = new GradeBuilder(gradeInfo).build(0L);
        GradeInfo update = new GradeInfoBuilder().withValue(2.3).build();

        Grade expected = new GradeBuilder(update).build(0L);

        when(gradeRepository
                .findGradeByCourseCodeAndAndUserName(gradeInfo.getCourseCode(),
                        gradeInfo.getUsername()))
                .thenReturn(Optional.of(grade));

        assertEquals(update, gradeService.updateGrade(update));

        verify(gradeRepository, times(1)).save(expected);
    }

    @WithMockUser(roles = admin)
    @Test
    public void updateGradeEmptyTest() {
        GradeInfo gradeInfo = new GradeInfoBuilder().build();

        when(gradeRepository
                .findGradeByCourseCodeAndAndUserName(gradeInfo.getCourseCode(),
                        gradeInfo.getUsername()))
                .thenReturn(Optional.empty());

        assertThrows(EmptyTargetException.class, () -> gradeService.updateGrade(gradeInfo));

        verify(gradeRepository, never()).save(any(Grade.class));
    }

    @WithMockUser(roles = admin)
    @Test
    public void deleteGradeEmptyTest() {
        when(gradeRepository.findById(0L)).thenReturn(Optional.empty());

        assertThrows(EmptyTargetException.class, () -> gradeService.deleteGrade("0"));

        verify(gradeRepository, never()).delete(any(Grade.class));
    }

    @Test
    public void getGradeSuccessTest() throws EmptyTargetException {
        Grade g = new Grade();
        g.setValue(10.0);
        when(gradeRepository
            .findGradeByCourseCodeAndAndUserName("CSE4100", "wwonka"))
            .thenReturn(Optional.of(g));

        assertEquals(g, gradeService.getGradeForStudent("wwonka", "CSE4100"));
    }

    @Test
    public void getGradeNoneTest() {
        when(gradeRepository
            .findGradeByCourseCodeAndAndUserName("CSE4300", "oompa"))
            .thenReturn(Optional.empty());
        assertThrows(EmptyTargetException.class, () -> {
            gradeService.getGradeForStudent("oompa", "CSE4300");
        });
    }
}
