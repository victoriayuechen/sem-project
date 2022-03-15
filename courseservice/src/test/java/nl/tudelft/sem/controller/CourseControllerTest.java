package nl.tudelft.sem.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import nl.tudelft.sem.AverageWorkload;
import nl.tudelft.sem.CourseInfo;
import nl.tudelft.sem.GradeInfo;
import nl.tudelft.sem.entities.Course;
import nl.tudelft.sem.entities.Grade;
import nl.tudelft.sem.exceptions.EmptyTargetException;
import nl.tudelft.sem.exceptions.InvalidCourseException;
import nl.tudelft.sem.repositories.CourseRepository;
import nl.tudelft.sem.repositories.GradeRepository;
import nl.tudelft.sem.service.CourseService;
import nl.tudelft.sem.service.GradeService;
import nl.tudelft.sem.service.RecruitmentService;
import nl.tudelft.sem.util.CourseBuilder;
import nl.tudelft.sem.util.CourseInfoBuilder;
import nl.tudelft.sem.util.GradeBuilder;
import nl.tudelft.sem.util.GradeInfoBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CourseController.class)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class CourseControllerTest {
    @MockBean
    private transient CourseRepository courseRepository;
    @MockBean
    private transient GradeRepository gradeRepository;
    @MockBean
    private transient CourseService courseService;
    @MockBean
    private transient GradeService gradeService;
    @MockBean
    private transient RecruitmentService recruitmentService;
    @Autowired
    private transient MockMvc mockMvc;
    private transient Course course1;
    private transient Course course2;
    private transient Course course3;
    private transient Course course4;
    private transient Grade grade1;
    private transient Grade grade2;
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJz"
            + "dWIiOiJhbm5pYmFsZSIsImV4cCI6MTY0MDMw"
            + "NjkyMywiaWF0IjoxNjQwMjcwOTIzfQ.Ab3qsQdzo"
            + "U8viZwWtnFf9NqIG9GDsSssTxrjyXj_8Dg";

    private static final String AUTHORIZATION = "Authorization";

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

    /**
     * Generic JSON parser.
     *
     * @param obj Object of any class
     * @return JSON String used for requests/response
     */
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "-";
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void getRecruitmentNoExceptionsTest() throws Exception {
        when(recruitmentService.getRecruitment(grade1.getUserName())).thenReturn("list");

        mockMvc.perform(get("/courses/getRecruitment/{username}", grade1.getUserName()))
                .andExpect(status().isOk())
                .andExpect(content().string("list"));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void closeCourseEnoughTasTest() throws Exception {
        when(courseRepository.findById("CSE2115"))
                .thenReturn(Optional.of(course1));

        when(courseService.closeCourseEnoughTas("CSE2115"))
                .thenReturn("CSE2115");

        mockMvc.perform(put("/courses/closeCourseEnoughTas/{courseCode}", course1.getCourseCode()))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void closeCourseEmptyTasTest() throws Exception {
        when(courseRepository.findById("CSE1105"))
                .thenReturn(Optional.empty());

        when(courseService.closeCourseEnoughTas("CSE1105"))
                .thenThrow(new InvalidCourseException("Course not found."));

        mockMvc.perform(put("/courses/closeCourseEnoughTas/{courseCode}", course2.getCourseCode()))
                .andExpect(status().isBadRequest());

        verify(courseRepository, never()).save(any(Course.class));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void closeCourseNotEnoughTasTest() throws Exception {
        when(courseRepository.findById("CSE1305"))
                .thenReturn(Optional.of(course4));

        when(courseService.closeCourseEnoughTas("CSE1305"))
                .thenThrow(new InvalidCourseException(
                        "The course does not have enough TAs."));

        mockMvc.perform(put("/courses/closeCourseEnoughTas/{courseCode}", course4.getCourseCode()))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void closeCourseExceptionTest() throws Exception {

        when(courseService.closeCourseEnoughTas("CSE1305"))
                .thenThrow(new IOException());

        mockMvc.perform(put("/courses/closeCourseEnoughTas/{courseCode}", course4.getCourseCode()))
                .andExpect(status().is5xxServerError());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void closeCourseInterruptedTest() throws Exception {

        when(courseService.closeCourseEnoughTas("CSE1305"))
                .thenThrow(new InterruptedException());

        mockMvc.perform(put("/courses/closeCourseEnoughTas/{courseCode}", course4.getCourseCode()))
                .andExpect(status().is5xxServerError());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void closeCourseExceptionDeadlineTest() throws Exception {

        when(courseService.closeCourseDeadlinePassed("CSE1305"))
                .thenThrow(new IOException());

        mockMvc.perform(put("/courses/closeCourseDeadline/{courseCode}", course4.getCourseCode()))
                .andExpect(status().is5xxServerError());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void closeCourseInterruptedDeadlineTest() throws Exception {

        when(courseService.closeCourseDeadlinePassed("CSE1305"))
                .thenThrow(new InterruptedException());

        mockMvc.perform(put("/courses/closeCourseDeadline/{courseCode}", course4.getCourseCode()))
                .andExpect(status().is5xxServerError());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void closeCourseDeadlinePassedTest() throws Exception {
        when(courseRepository.findById("CSE1100"))
                .thenReturn(Optional.of(course3));

        when(courseService.closeCourseDeadlinePassed("CSE1100"))
                .thenReturn("CSE1100");

        mockMvc.perform(put("/courses/closeCourseDeadline/{courseCode}", course3.getCourseCode()))
                .andExpect(status().isOk());

    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void closeCourseDeadlineNotPassedTest() throws Exception {
        when(courseService.closeCourseDeadlinePassed("CSE1305"))
                .thenThrow(new InvalidCourseException(
                        "Deadline not passed yet."));

        mockMvc.perform(put("/courses/closeCourseDeadline/{courseCode}", course4.getCourseCode()))
                .andExpect(status().isBadRequest());

        verify(courseRepository, never()).save(any(Course.class));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void closeCourseEmptyDeadlineTest() throws Exception {
        when(courseRepository.findById("CSE2115"))
                .thenReturn(Optional.empty());

        when(courseService.closeCourseDeadlinePassed("CSE1105"))
                .thenThrow(new InvalidCourseException("Course not found."));

        mockMvc.perform(put("/courses/closeCourseDeadline/{courseCode}", course2.getCourseCode()))
                .andExpect(status().isBadRequest());

        verify(courseRepository, never()).save(any(Course.class));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void closeCourseTest() throws Exception {
        when(courseRepository.findById("CSE2115"))
                .thenReturn(Optional.of(course1));

        when(courseService.closeCourse("CSE2115")).thenReturn(course1);

        course1.setOpen(false);

        mockMvc.perform(put("/courses/closeCourse/{courseCode}", "CSE2115"))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(course1)));

    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void closeCourseEmptyTest() throws Exception {
        when(courseRepository.findById("CSE2222"))
                .thenReturn(Optional.empty());

        when(courseService.closeCourse("CSE2222")).thenThrow(new IllegalArgumentException(""));

        mockMvc.perform(put("/courses/closeCourse/{courseCode}", "CSE2222"))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void closeCourseErrorTest() throws Exception {
        when(courseRepository.findById("CSE2222"))
                .thenReturn(Optional.empty());

        when(courseService.closeCourse("CSE2222")).thenThrow(new IOException(""));

        mockMvc.perform(put("/courses/closeCourse/{courseCode}", "CSE2222"))
                .andExpect(status().is5xxServerError());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void averageWorkLoadSuccessTest() throws Exception {
        AverageWorkload workload = new AverageWorkload();
        workload.setAverageHours(11);
        workload.setCourseCode("CSE2115");

        when(courseService.obtainAverageWorkloadPerCourse("CSE2115", TOKEN))
                .thenReturn(workload);

        mockMvc.perform(get("/courses/averageWorkload/{courseCode}", "CSE2115")
                        .header(AUTHORIZATION, TOKEN)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().json(asJsonString(workload)));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void averageWorkloadInvalidCourseTest() throws Exception {
        when(courseService.obtainAverageWorkloadPerCourse("Chocolate Factory", TOKEN))
                .thenThrow(EmptyTargetException.class);

        mockMvc.perform(get("/courses/averageWorkload/{courseCode}",
                        "Chocolate Factory")
                        .header(AUTHORIZATION, TOKEN)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void noDeclaredWorkloadsTest() throws Exception {
        when(courseService.obtainAverageWorkloadPerCourse("CSE2530", TOKEN))
                .thenThrow(InvalidCourseException.class);

        mockMvc.perform(get("/courses/averageWorkload/{courseCode}",
                        "CSE2530")
                        .header(AUTHORIZATION, TOKEN)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void noTasForWorkloads() throws Exception {
        when(courseService.obtainAverageWorkloadPerCourse("CSE2530", TOKEN))
                .thenThrow(InvalidCourseException.class);

        mockMvc.perform(get("/courses/averageWorkload/{courseCode}",
                        "CSE2530").accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, TOKEN))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void interruptedExceptionObtainWorkload() throws Exception {
        when(courseService.obtainAverageWorkloadPerCourse("CSE2530", TOKEN))
                .thenThrow(InterruptedException.class);

        mockMvc.perform(get("/courses/averageWorkload/{courseCode}",
                        "CSE2530")
                        .header(AUTHORIZATION, TOKEN)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void ioExceptionObtainWorkload() throws Exception {
        when(courseService.obtainAverageWorkloadPerCourse("CSE2530", TOKEN))
                .thenThrow(IOException.class);

        mockMvc.perform(get("/courses/averageWorkload/{courseCode}",
                        "CSE2530")
                        .header(AUTHORIZATION, TOKEN)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }


    @WithMockUser(roles = "ADMIN")
    @Test
    public void obtainGradeSuccessTest() throws Exception {
        Grade grade = new Grade();
        grade.setCourseCode("CSE2115");
        grade.setUserName("annibale");
        grade.setId(0);
        grade.setValue(42.0);

        when(gradeService.getGradeForStudent("annibale", "CSE2115"))
                .thenReturn(grade);

        mockMvc.perform(get("/courses/courseGrade/{courseCode}/{userName}",
                        "CSE2115", "annibale")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("42.0"));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void noSuchGradeTest() throws Exception {
        when(gradeService.getGradeForStudent("lofi", "CSE2525"))
                .thenThrow(EmptyTargetException.class);

        mockMvc.perform(get("/courses/courseGrade/{courseCode}/{userName}",
                        "CSE2525", "lofi")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void checkForRecruitment() throws Exception {
        when(courseService.openForRecruitment("CSE2115"))
                .thenReturn(true);

        mockMvc.perform(get("/courses/courseOpen/CSE2115"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void getCourseQuarterSuccessTest() throws Exception {
        when(courseService.getCourseQuarter("CSE2115"))
                .thenReturn(2);

        mockMvc.perform(get("/courses/getCourseQuarter/CSE2115"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void getCourseQuarterNoCourseTest() throws Exception {
        when(courseService.getCourseQuarter("CSE2115"))
                .thenThrow(EmptyTargetException.class);

        mockMvc.perform(get("/courses/getCourseQuarter/CSE2115"))
                .andExpect(status().isBadRequest());
    }


    @WithMockUser(roles = "ADMIN")
    @Test
    public void updateCourseNullTest() throws Exception {
        mockMvc.perform(put("/courses/course/updateCourse")
                        .content(asJsonString(null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(courseRepository, never()).save(any(Course.class));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void updateCourseEmptyTest() throws Exception {
        CourseInfo courseInfo = new CourseInfoBuilder().build("CSE2022");

        when(courseService.updateCourse(courseInfo)).thenThrow(new EmptyTargetException(""));

        mockMvc.perform(put("/courses/course/updateCourse")
                        .content(asJsonString(courseInfo))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void updateCourseTest() throws Exception {
        CourseInfo update = new CourseInfoBuilder()
                .withIsOpen(true)
                .withStartDate(null)
                .build("CSE2023");

        when(courseService.updateCourse(update)).thenReturn(update);

        mockMvc.perform(put("/courses/course/updateCourse")
                        .content(asJsonString(update))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(update)));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void deleteCourseEmptyTest() throws Exception {
        when(courseService.deleteCourse("CSE2025")).thenThrow(new EmptyTargetException(""));

        mockMvc.perform(delete("/courses/course/deleteCourse/{courseCode}", "CSE2025")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void deleteCourseTest() throws Exception {
        Course course = new CourseBuilder().build("CSE2021");

        when(courseService.deleteCourse("CSE2021")).thenReturn(course);

        mockMvc.perform(delete("/courses/course/deleteCourse/{courseCode}", "CSE2021")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(course)));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void updateGradeNullTest() throws Exception {
        mockMvc.perform(put("/courses/grade/updateGrade")
                        .content(asJsonString(null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(gradeRepository, never()).save(any(Grade.class));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void updateGradeEmptyTest() throws Exception {
        GradeInfo gradeInfo = new GradeInfoBuilder().build();

        when(gradeService.updateGrade(gradeInfo)).thenThrow(new EmptyTargetException(""));

        mockMvc.perform(put("/courses/grade/updateGrade")
                        .content(asJsonString(gradeInfo))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void updateGradeTest() throws Exception {
        GradeInfo update = new GradeInfoBuilder().withValue(2.3).build();

        when(gradeService.updateGrade(update)).thenReturn(update);

        mockMvc.perform(put("/courses/grade/updateGrade")
                        .content(asJsonString(update))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(update)));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void deleteGradeEmptyTest() throws Exception {
        when(gradeService.deleteGrade("0")).thenThrow(new EmptyTargetException(""));

        mockMvc.perform(delete("/courses/grade/deleteGrade/{stringId}", 0)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void deleteGradeTest() throws Exception {
        Grade grade = new GradeBuilder().build(0L);

        when(gradeService.deleteGrade("0")).thenReturn(grade);

        mockMvc.perform(delete("/courses/grade/deleteGrade/{stringId}", 0)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(grade)));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void changeRatio() throws Exception {
        mockMvc.perform(put("/courses/changeRatio/{courseCode}/{ratio}",
                        "CSE2115", 25)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("25"));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void addCourseTest() throws Exception {
        when(courseRepository.findById(course1.getCourseCode())).thenReturn(Optional.empty());

        when(courseRepository.save(course1)).thenReturn(course1);

        when(courseService.addCourse(course1)).thenReturn(course1);

        mockMvc.perform(post("/courses/addCourse")
                        .content(asJsonString(course1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void addExistingCourseTest() throws Exception {
        when(courseRepository.findById(course1.getCourseCode())).thenReturn(Optional.of(course1));

        when(courseService.addCourse(course1)).thenThrow(new InvalidCourseException(""));

        mockMvc.perform(post("/courses/addCourse")
                        .content(asJsonString(course1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void addCourseErrorTest() throws Exception {
        when(courseRepository.findById(course1.getCourseCode())).thenReturn(Optional.of(course1));

        when(courseService.addCourse(course1)).thenThrow(new IOException(""));

        mockMvc.perform(post("/courses/addCourse")
                        .content(asJsonString(course1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }
}