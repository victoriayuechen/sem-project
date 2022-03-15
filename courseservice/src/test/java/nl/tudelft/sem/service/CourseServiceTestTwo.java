package nl.tudelft.sem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.AverageWorkload;
import nl.tudelft.sem.communicators.TaCommunicator;
import nl.tudelft.sem.entities.Course;
import nl.tudelft.sem.exceptions.EmptyTargetException;
import nl.tudelft.sem.exceptions.InvalidCourseException;
import nl.tudelft.sem.repositories.CourseRepository;
import nl.tudelft.sem.util.CourseBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.test.context.support.WithMockUser;


public class CourseServiceTestTwo {
    private final transient CourseRepository courseRepository =
            Mockito.mock(CourseRepository.class);
    private final transient TaCommunicator taCommunicator = Mockito.mock(TaCommunicator.class);
    private transient CourseService courseService =
        new CourseService(courseRepository, taCommunicator);
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJz"
        + "dWIiOiJhbm5pYmFsZSIsImV4cCI6MTY0MDMw"
        + "NjkyMywiaWF0IjoxNjQwMjcwOTIzfQ.Ab3qsQdzo"
        + "U8viZwWtnFf9NqIG9GDsSssTxrjyXj_8Dg";
    private transient Course course1;
    private transient Course course2;
    private transient Course course3;
    private transient Course course4;
    private final transient String courseCode1 = "CSE2115";
    private final transient String courseCode2 = "CSE2525";

    /**
     * Sets up all the objects for testing before each test.
     */
    @BeforeEach
    public void setup() {
        course1 = new Course();
        course1.setCourseCode(courseCode1);
        course1.setCourseName("Software Engineering Methods");
        course1.setDuration(10);
        course1.setAverageTaHour(10.0);
        course1.setOpen(true);
        course1.setNumberOfStudents(200);
        course1.setNumberOfTas(20);
        course1.setQuarter(2);
        course1.setStudentTaRatio(20);

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

        //ZonedDateTime startDate4 = ZonedDateTime.of(
        //        2022, 01, 20, 23, 45, 59, 1234, ZoneId.of("UTC+1")
        //);
        ZonedDateTime startDate4 = ZonedDateTime.now().plus(1, ChronoUnit.YEARS);
        course4.setStartDate(startDate4);
    }

    @Test
    public void getCourseQuarterSuccessTest() throws EmptyTargetException {
        Course sem = new CourseBuilder()
            .withQuarter(2)
            .build(courseCode1);
        when(courseRepository.findById(courseCode1)).thenReturn(Optional.of(sem));

        assertEquals(2, courseService.getCourseQuarter(courseCode1));
    }

    @Test
    public void getCourseQuarterNoCourseTest() {
        when(courseRepository.findById(courseCode1)).thenReturn(Optional.empty());
        assertThrows(EmptyTargetException.class, () -> {
            courseService.getCourseQuarter(courseCode1);
        });
    }

    @Test
    public void openRecruitmentTrueTest() {
        Course datamining = new CourseBuilder()
            .withOpen(true).build(courseCode2);

        when(courseRepository.findById(courseCode2))
            .thenReturn(Optional.of(datamining));

        assertTrue(courseService.openForRecruitment(courseCode2));
    }

    @Test
    public void openRecruitmentFalseNoCourseTest() {
        when(courseRepository.findById(courseCode2))
            .thenReturn(Optional.empty());

        assertFalse(courseService.openForRecruitment(courseCode2));
    }

    @Test
    public void openRecruitmentFalseNotOpenTest() {
        Course datamining = new CourseBuilder()
            .withOpen(false).build(courseCode2);

        when(courseRepository.findById(courseCode2))
            .thenReturn(Optional.of(datamining));

        assertFalse(courseService.openForRecruitment(courseCode2));
    }

    @Test
    public void successObtainWorkloadTest() throws Exception {
        AverageWorkload w = new AverageWorkload();
        w.setCourseCode(courseCode1);
        w.setAverageHours(1);

        Course course = new CourseBuilder().build(courseCode1);
        when(courseRepository.findById(courseCode1)).thenReturn(Optional.of(course));
        when(taCommunicator.obtainWorkLoadHours(courseCode1, TOKEN)).thenReturn(List.of(10));
        when(taCommunicator.taCount(courseCode1, TOKEN)).thenReturn(1);

        assertEquals(w, courseService.obtainAverageWorkloadPerCourse(courseCode1, TOKEN));
    }

    @Test
    public void obtainWorkloadNoSuchCourseTest() throws Exception {
        when(courseRepository.findById(courseCode1)).thenReturn(Optional.empty());
        assertThrows(EmptyTargetException.class, () -> {
            courseService.obtainAverageWorkloadPerCourse(courseCode1, TOKEN);
        });

        verify(taCommunicator, never()).obtainWorkLoadHours(courseCode1, TOKEN);
        verify(taCommunicator, never()).taCount(courseCode1, TOKEN);
    }

    @Test
    public void obtainWorkloadNoHoursTest() throws Exception {
        Course course = new CourseBuilder().build(courseCode1);
        when(courseRepository.findById(courseCode1)).thenReturn(Optional.of(course));
        when(taCommunicator.obtainWorkLoadHours(courseCode1, TOKEN)).thenReturn(List.of());

        assertThrows(InvalidCourseException.class, () -> {
            courseService.obtainAverageWorkloadPerCourse(courseCode1, TOKEN);
        });

        verify(taCommunicator, never()).taCount(courseCode1, TOKEN);
    }

    @Test
    public void obtainWorkloadNoTasTest() throws Exception {
        Course course = new CourseBuilder().build(courseCode1);
        when(courseRepository.findById(courseCode1)).thenReturn(Optional.of(course));
        when(taCommunicator.obtainWorkLoadHours(courseCode1, TOKEN)).thenReturn(List.of(1));
        when(taCommunicator.taCount(courseCode1, TOKEN)).thenReturn(0);

        assertThrows(InvalidCourseException.class, () -> {
            courseService.obtainAverageWorkloadPerCourse(courseCode1, TOKEN);
        });

    }


    @Test
    public void addCourseTest() throws IOException, InterruptedException, InvalidCourseException {
        when(courseRepository.findById(course1.getCourseCode())).thenReturn(Optional.empty());

        when(courseRepository.save(course1)).thenReturn(course1);

        assertEquals(course1, courseService.addCourse(course1));
    }

    @Test
    public void addExistingCourseTest() throws Exception {
        when(courseRepository.findById(course1.getCourseCode())).thenReturn(Optional.of(course1));

        assertThrows(InvalidCourseException.class, () -> courseService.addCourse(course1));
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    public void closeCourseTest() throws IOException, InterruptedException {
        when(courseRepository.findById(courseCode1))
                .thenReturn(Optional.of(course1));

        Course course1Copy = course1;

        course1Copy.setOpen(false);

        assertEquals(course1Copy, courseService.closeCourse(course1.getCourseCode()));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void closeCourseEmptyTest() throws Exception {
        when(courseRepository.findById("CSE2222"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> courseService.closeCourse("CSE2222"));
        verify(courseRepository, never()).save(any(Course.class));
    }
}
