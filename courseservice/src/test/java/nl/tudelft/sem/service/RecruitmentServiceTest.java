package nl.tudelft.sem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.entities.Course;
import nl.tudelft.sem.entities.Grade;
import nl.tudelft.sem.repositories.CourseRepository;
import nl.tudelft.sem.repositories.GradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class RecruitmentServiceTest {
    private final transient GradeRepository gradeRepository =
            Mockito.mock(GradeRepository.class);
    private final transient CourseRepository courseRepository =
            Mockito.mock(CourseRepository.class);
    private transient RecruitmentService recruitmentService =
            new RecruitmentService(courseRepository, gradeRepository);
    private transient Course course1;
    private transient Course course2;
    private transient Course course3;
    private transient Grade grade1;
    private transient Grade grade2;

    /**
     * Sets up all the objects for testing before each test.
     */
    @BeforeEach
    public void setup() {
        course1 = new Course();
        course1.setCourseCode("CSE2115");
        course1.setCourseName("Software Engineering Methods");
        course1.setDuration(10);
        course1.setAverageTaHour(10.0);
        course1.setOpen(true);
        course1.setNumberOfStudents(200);
        course1.setNumberOfTas(20);
        course1.setQuarter(2);
        course1.setStudentTaRatio(20);

        ZonedDateTime startDate1 = ZonedDateTime.of(
                2022, 01, 22, 23, 45, 59, 1234, ZoneId.of("UTC+1")
        );
        course1.setStartDate(startDate1);

        course2 = new Course();
        course2.setCourseCode("CSE1105");
        course2.setCourseName("OOP Project");
        course2.setDuration(10);
        course2.setAverageTaHour(20.0);
        course2.setOpen(false);
        course2.setNumberOfStudents(500);
        course2.setNumberOfTas(25);
        course2.setQuarter(3);
        course2.setStudentTaRatio(20);

        course3 = new Course();
        course3.setCourseCode("CSE1100");
        course3.setCourseName("Object-oriented Programming");
        course3.setDuration(10);
        course3.setAverageTaHour(5.0);
        course3.setOpen(true);
        course3.setNumberOfStudents(600);
        course3.setNumberOfTas(25);
        course3.setQuarter(1);
        course3.setStudentTaRatio(20);

        ZonedDateTime startDate3 = ZonedDateTime.of(
                2021, 12, 25, 23, 45, 59, 1234, ZoneId.of("UTC+1")
        );
        course3.setStartDate(startDate3);

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
    public void singleRecruitmentTest() {
        when(gradeRepository.selectApplicableCoursesByUsername(grade1.getUserName()))
                .thenReturn(List.of(grade1.getCourseCode(), grade2.getCourseCode()));
        when(courseRepository.getRecruitment(course1.getCourseCode()))
                .thenReturn(Optional.of(course1.isOpen()));
        when(courseRepository.getRecruitment(course2.getCourseCode()))
                .thenReturn(Optional.of(course2.isOpen()));

        assertEquals(recruitmentService.getRecruitment(grade1.getUserName()),
                toOverviewString(List.of(course1.getCourseCode())));
    }

    @Test
    public void multipleRecruitmentTest() {
        course2.setOpen(true);
        when(gradeRepository.selectApplicableCoursesByUsername(grade1.getUserName()))
                .thenReturn(List.of(grade1.getCourseCode(), grade2.getCourseCode()));
        when(courseRepository.getRecruitment(course1.getCourseCode()))
                .thenReturn(Optional.of(course1.isOpen()));
        when(courseRepository.getRecruitment(course2.getCourseCode()))
                .thenReturn(Optional.of(course2.isOpen()));

        assertEquals(recruitmentService.getRecruitment(grade1.getUserName()),
                toOverviewString(List.of(course1.getCourseCode(),
                        course2.getCourseCode())));
    }

    @Test
    public void nullRecruitmentTest() {
        when(gradeRepository.selectApplicableCoursesByUsername(grade1.getUserName()))
                .thenReturn(List.of(grade1.getCourseCode(), grade2.getCourseCode()));
        when(courseRepository.getRecruitment(course1.getCourseCode()))
                .thenReturn(Optional.empty());
        when(courseRepository.getRecruitment(course2.getCourseCode()))
                .thenReturn(Optional.of(course2.isOpen()));

        assertEquals("No applicable courses found with this username.",
                recruitmentService.getRecruitment(grade1.getUserName())
        );
    }

    @Test
    public void noRecruitmentTest() {
        course1.setOpen(false);
        course3.setOpen(false);
        when(gradeRepository.selectApplicableCoursesByUsername(grade1.getUserName()))
                .thenReturn(List.of(grade1.getCourseCode(), grade2.getCourseCode()));
        when(courseRepository.getRecruitment(course1.getCourseCode()))
                .thenReturn(Optional.of(course1.isOpen()));
        when(courseRepository.getRecruitment(course2.getCourseCode()))
                .thenReturn(Optional.of(course2.isOpen()));

        assertEquals("No applicable courses found with this username.",
                recruitmentService.getRecruitment(grade1.getUserName())
        );
    }

    @Test
    public void nonexistentStudent() {
        when(gradeRepository.selectApplicableCoursesByUsername(grade1.getUserName()))
                .thenReturn(List.of());
        assertEquals("No applicable courses found with this username.",
                recruitmentService.getRecruitment(grade1.getUserName() + "b")
        );
    }

    /**
     * Turns a list of course codes into a clear overview.
     *
     * @param courseCodes Collection of course code strings.
     * @return String containing the overview of the course codes.
     */
    private static String toOverviewString(Collection<String> courseCodes) {
        StringBuilder total = new StringBuilder();
        total.append("You are able to apply for:\n");
        for (String string : courseCodes) {
            total.append("\t").append(string).append("\n");
        }
        return total.toString();
    }
}
