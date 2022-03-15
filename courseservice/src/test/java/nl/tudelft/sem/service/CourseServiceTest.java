package nl.tudelft.sem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import nl.tudelft.sem.CourseInfo;
import nl.tudelft.sem.communicators.TaCommunicator;
import nl.tudelft.sem.entities.Course;
import nl.tudelft.sem.exceptions.EmptyTargetException;
import nl.tudelft.sem.exceptions.InvalidCourseException;
import nl.tudelft.sem.repositories.CourseRepository;
import nl.tudelft.sem.util.CourseBuilder;
import nl.tudelft.sem.util.CourseChecks;
import nl.tudelft.sem.util.CourseInfoBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.test.context.support.WithMockUser;

public class CourseServiceTest {
    private final transient CourseRepository courseRepository =
            Mockito.mock(CourseRepository.class);
    private final transient TaCommunicator taCommunicator =
            Mockito.mock(TaCommunicator.class);
    private transient CourseService courseService =
        new CourseService(courseRepository, taCommunicator);
    private transient Course course1;
    private transient Course course2;
    private transient Course course3;
    private transient Course course4;
    private final transient String admin = "ADMIN";

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

        course4 = new Course();
        course4.setCourseCode("CSE1305");
        course4.setCourseName("ADS");
        course4.setDuration(10);
        course4.setAverageTaHour(10.0);
        course4.setOpen(true);
        course4.setNumberOfStudents(600);
        course4.setNumberOfTas(25);
        course4.setQuarter(2);
        course4.setStudentTaRatio(20);

        ZonedDateTime startDate4 = ZonedDateTime.of(
                2022, 05, 20, 23, 45, 59, 1234, ZoneId.of("UTC+1")
        );
        course4.setStartDate(startDate4);
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    @WithMockUser(roles = admin)
    @Test
    public void updateCourseEmptyTest() {
        CourseInfo courseInfo = new CourseInfoBuilder().build("CSE2021");

        when(courseRepository.findById("CSE2021")).thenReturn(Optional.empty());

        assertThrows(EmptyTargetException.class, () -> courseService.updateCourse(courseInfo));

        verify(courseRepository, never()).save(any(Course.class));
    }

    @WithMockUser(roles = admin)
    @Test
    public void deleteCourseTest() throws Exception {
        String courseCode = "CSE2111";
        Course course = new CourseBuilder().build(courseCode);

        when(courseRepository.findById(courseCode)).thenReturn(Optional.of(course));

        assertEquals(course, courseService.deleteCourse(courseCode));

        verify(courseRepository, times(1)).deleteById(courseCode);
    }

    @WithMockUser(roles = admin)
    @Test
    public void updateCourseTest() throws Exception {
        String courseCode = "CSE2019";
        CourseInfo courseInfo = new CourseInfoBuilder().build(courseCode);

        Course course = new CourseBuilder(courseInfo).build(courseCode);
        CourseInfo update = new CourseInfoBuilder()
                .withIsOpen(true)
                .withStartDate(null)
                .build(courseCode);

        Course expected = new CourseBuilder(update).build(courseCode);

        when(courseRepository.findById(courseInfo.getCourseCode())).thenReturn(Optional.of(course));

        assertEquals(update, courseService.updateCourse(update));

        verify(courseRepository, times(1)).save(expected);
    }

    @WithMockUser(roles = admin)
    @Test
    public void deleteCourseEmptyTest() {
        when(courseRepository.findById("CSE20211")).thenReturn(Optional.empty());

        assertThrows(EmptyTargetException.class, () -> courseService.deleteCourse("CSE20211"));

        verify(courseRepository, never()).delete(any(Course.class));
    }

    @Test
    public void closeCourseEnoughTasTest() throws Exception {
        when(courseRepository.findById("CSE1115"))
                .thenReturn(Optional.of(course1));

        assertEquals(courseService.closeCourseEnoughTas("CSE1115"),
                course1.getCourseCode());
    }

    @Test
    public void closeCourseNotEnoughTasTest() {
        when(courseRepository.findById("CSE1400"))
                .thenReturn(Optional.of(course3));

        assertThrows(InvalidCourseException.class, () ->
                courseService.closeCourseEnoughTas(course3.getCourseCode())
        );
    }

    @Test
    public void closeCourseEmptyTasTest() {
        when(courseRepository.findById("CSE1200"))
                .thenReturn(Optional.empty());

        assertThrows(InvalidCourseException.class, () ->
                courseService.closeCourseEnoughTas(course3.getCourseCode())
        );
    }

    @Test
    public void closeCourseDeadlinePassedTest() throws Exception {
        when(courseRepository.findById("CSE1100"))
                .thenReturn(Optional.of(course3));

        assertEquals(courseService.closeCourseDeadlinePassed("CSE1100"),
                course3.getCourseCode());
    }

    @Test
    public void closeCourseDeadlineNotPassedTest() {
        when(courseRepository.findById("CSE1305"))
                .thenReturn(Optional.of(course4));

        assertThrows(InvalidCourseException.class, () ->
                courseService.closeCourseDeadlinePassed(course4.getCourseCode())
        );
    }

    @Test
    public void closeCourseEmptyDeadlineTest() {
        when(courseRepository.findById("CSE1500"))
                .thenReturn(Optional.empty());

        assertThrows(InvalidCourseException.class, () ->
                courseService.closeCourseDeadlinePassed(course3.getCourseCode())
        );
    }

    @Test
    public void courseHasEnoughTasTest() {
        assertTrue(CourseChecks.courseHasEnoughTas(course1));
    }

    @Test
    public void courseDoesNotHaveEnoughTasTest() {
        assertFalse(CourseChecks.courseHasEnoughTas(course4));
    }

    @Test
    public void noStartDate() {
        assertFalse(CourseChecks.hasDeadlinePassed(course2));
    }

    @Test
    public void biggerYearTest() {
        assertTrue(CourseChecks.hasDeadlinePassed(course3));
    }

    @Test
    public void biggerDayTest() {
        assertTrue(CourseChecks.hasDeadlinePassed(course1));
    }

    @Test
    public void deadlineNotPassed() {
        assertFalse(CourseChecks.hasDeadlinePassed(course4));
    }

    @Test
    public void changeRatio() throws Exception {
        Course tempCourse = new CourseBuilder()
                .build("CSE2115");
        when(courseRepository
                .findById("CSE2115"))
                .thenReturn(Optional.of(tempCourse));
        courseService.changeRatio(tempCourse.getCourseCode(), 25);
        verify(courseRepository, times(1)).save(tempCourse);
    }
}
