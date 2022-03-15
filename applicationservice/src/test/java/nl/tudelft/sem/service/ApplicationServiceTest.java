package nl.tudelft.sem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.ApplyInfo;
import nl.tudelft.sem.Criteria;
import nl.tudelft.sem.SelectInfo;
import nl.tudelft.sem.Status;
import nl.tudelft.sem.communication.CourseCommunicator;
import nl.tudelft.sem.communication.NotificationCommunicator;
import nl.tudelft.sem.communication.TaCommunicator;
import nl.tudelft.sem.entities.Application;
import nl.tudelft.sem.exceptions.EmptyTargetElementException;
import nl.tudelft.sem.exceptions.InvalidApplicationException;
import nl.tudelft.sem.repositories.ApplicationRepository;
import nl.tudelft.sem.util.ApplicationBuilder;
import nl.tudelft.sem.util.FilterParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ApplicationServiceTest {
    private transient ApplicationRepository applicationRepository =
        Mockito.mock(ApplicationRepository.class);
    private transient NotificationCommunicator notificationCommunicator =
        Mockito.mock(NotificationCommunicator.class);
    private transient CourseCommunicator courseCommunicator =
            Mockito.mock(CourseCommunicator.class);
    private final transient ApplicationService applicationService =
        new ApplicationService(applicationRepository,
            notificationCommunicator, courseCommunicator);

    private transient ApplyInfo info;
    private transient SelectInfo rejectInfo;
    private transient SelectInfo withdrawInfo;
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJz"
        + "dWIiOiJhbm5pYmFsZSIsImV4cCI6MTY0MDMw"
        + "NjkyMywiaWF0IjoxNjQwMjcwOTIzfQ.Ab3qsQdzo"
        + "U8viZwWtnFf9NqIG9GDsSssTxrjyXj_8Dg";
    private final transient String courseCode = "CSE2115";

    /** Initializes multiple instances of info at before each test.
     */
    @BeforeEach
    public void setup() {
        info = new ApplyInfo();
        info.setUsername("Victoria");
        info.setCourseCode(courseCode);

        rejectInfo = new SelectInfo();
        rejectInfo.setUsername("Charlie");
        rejectInfo.setCourseCode("CSE4242");
        rejectInfo.setStatus(Status.REJECTED);

        withdrawInfo = new SelectInfo();
        withdrawInfo.setUsername("Oompa Loompa");
        withdrawInfo.setCourseCode("CSE4242");
        withdrawInfo.setStatus(Status.REVOKED);
    }

    @Test
    public void withdrawAppSuccessTest() throws Exception {
        Application app = new ApplicationBuilder()
                .withName(withdrawInfo.getUsername())
                .withCourse(withdrawInfo.getCourseCode())
                .withStatus(Status.PENDING)
                .build(0L);

        when(applicationRepository
                .findApplicationsByUsernameAndCourseCode(app.getUsername(),
                                        app.getCourseCode()))
                .thenReturn(Optional.of(app));

        assertEquals(app, applicationService.withdrawApp(withdrawInfo, TOKEN));
    }

    @Test
    public void withdrawAppAlreadyApprovedTest() throws Exception {
        Application app = new ApplicationBuilder()
                .withName(withdrawInfo.getUsername())
                .withCourse(withdrawInfo.getCourseCode())
                .withStatus(Status.APPROVED)
                .build(0L);

        when(applicationRepository
                .findApplicationsByUsernameAndCourseCode(app.getUsername(),
                        app.getCourseCode()))
                .thenReturn(Optional.of(app));

        assertThrows(InvalidApplicationException.class, () ->
                applicationService.withdrawApp(withdrawInfo, TOKEN)
        );
    }

    @Test
    public void withdrawAppEmptyTest() throws Exception {
        Application app = new ApplicationBuilder()
                .withName(withdrawInfo.getUsername())
                .withCourse(withdrawInfo.getCourseCode())
                .withStatus(Status.APPROVED)
                .build(0L);

        when(applicationRepository
                .findApplicationsByUsernameAndCourseCode(app.getUsername(),
                        app.getCourseCode()))
                .thenReturn(Optional.empty());

        assertThrows(InvalidApplicationException.class, () ->
                applicationService.withdrawApp(withdrawInfo, TOKEN)
        );
    }

    @Test
    public void createApplicationSuccessTest()
            throws IOException, InterruptedException, InvalidApplicationException {
        when(courseCommunicator.obtainCourseQuarter(info.getCourseCode(), TOKEN))
                .thenReturn(2);
        when(applicationRepository.findApplicationsByUsernameAndQuarter(info.getUsername(),
                2)).thenReturn(List.of());
        when(applicationRepository.findApplicationsByUsernameAndCourseCode(info.getUsername(),
                info.getCourseCode())).thenReturn(Optional.empty());
        when(courseCommunicator.getGradeForCourse(info.getCourseCode(), info.getUsername(), TOKEN))
                .thenReturn(10.0);
        Application app = new ApplicationBuilder()
                .withName("Victoria")
                .withGrade(10.0)
                .withQuarter(2)
                .build(0);
        assertEquals(app, applicationService.createApplication(TOKEN, info.getCourseCode(),
                info.getUsername()));

        verify(applicationRepository, times(1)).save(app);
    }

    @Test
    public void createApplicationTooManyApplicationsTest()
            throws IOException, InterruptedException, InvalidApplicationException {
        Application app1 = new Application();
        Application app2 = new Application();
        Application app3 = new Application();
        when(courseCommunicator.obtainCourseQuarter(info.getCourseCode(), TOKEN)).thenReturn(2);
        when(applicationRepository.findApplicationsByUsernameAndQuarter(info.getUsername(),
                2)).thenReturn(List.of(app1, app2, app3));

        assertThrows(InvalidApplicationException.class, () -> {
            applicationService.createApplication(TOKEN, info.getCourseCode(), info.getUsername());
        });

        verify(applicationRepository, never())
                .findApplicationsByUsernameAndCourseCode(info.getUsername(), info.getCourseCode());
        verify(courseCommunicator, never())
                .getGradeForCourse(info.getCourseCode(), info.getUsername(), TOKEN);
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    public void createApplicationAlreadyAppliedTest() throws IOException, InterruptedException {
        Application app1 = new Application();
        when(courseCommunicator.obtainCourseQuarter(info.getCourseCode(), TOKEN))
                .thenReturn(2);
        when(applicationRepository
                .findApplicationsByUsernameAndQuarter(info.getUsername(), 2))
                .thenReturn(List.of());
        when(applicationRepository
                .findApplicationsByUsernameAndCourseCode(info.getUsername(), info.getCourseCode()))
                .thenReturn(Optional.of(app1));

        assertThrows(InvalidApplicationException.class, () -> {
            applicationService.createApplication(TOKEN, info.getCourseCode(), info.getUsername());
        });

        verify(courseCommunicator, never()).getGradeForCourse(info.getCourseCode(),
                info.getUsername(), TOKEN);
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    public void createApplicationDidNotPassTest() throws IOException, InterruptedException {
        when(courseCommunicator.obtainCourseQuarter(info.getCourseCode(), TOKEN))
                .thenReturn(2);
        when(applicationRepository
                .findApplicationsByUsernameAndQuarter(info.getUsername(), 2))
                .thenReturn(List.of());
        when(applicationRepository
                .findApplicationsByUsernameAndCourseCode(info.getUsername(), info.getCourseCode()))
                .thenReturn(Optional.empty());
        when(courseCommunicator.getGradeForCourse(info.getCourseCode(), info.getUsername(), TOKEN))
                .thenReturn(Double.MIN_VALUE);

        assertThrows(InvalidApplicationException.class, () -> {
            applicationService.createApplication(TOKEN, info.getCourseCode(), info.getUsername());
        });

        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    public void obtainOpenApplicationsTest() throws EmptyTargetElementException {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withCourse(courseCode).build(0L);
        Application app2 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withCourse(courseCode).build(1L);
        Application app3 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withCourse(courseCode).build(2L);
        when(applicationRepository.findApplicationsByCourseCode(courseCode))
                .thenReturn(List.of(app1, app2, app3));

        assertThat(applicationService.obtainApplicationsByCourse(courseCode))
                .containsExactlyInAnyOrder(app1, app2, app3);
    }

    @Test
    public void obtainOpenApplicationsEmptyTest() {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.APPROVED)
                .withCourse(courseCode).build(0L);
        Application app2 = new ApplicationBuilder()
                .withStatus(Status.REJECTED)
                .withCourse(courseCode).build(1L);
        when(applicationRepository.findApplicationsByCourseCode(courseCode))
                .thenReturn(List.of(app1, app2));

        assertThrows(EmptyTargetElementException.class, () -> {
            applicationService.obtainApplicationsByCourse(courseCode);
        });
    }

    @Test
    public void rejectApplicantSuccessTest()
            throws EmptyTargetElementException, IOException, InterruptedException {
        Application app = new Application();
        when(applicationRepository
                .findApplicationsByUsernameAndCourseCode(
                        rejectInfo.getUsername(), rejectInfo.getCourseCode()))
                .thenReturn(Optional.of(app));
        app.setStatus(Status.REJECTED);

        assertEquals(app, applicationService.rejectApplicant(rejectInfo, TOKEN));

        verify(applicationRepository, times(1)).save(app);
        verify(notificationCommunicator, times(1))
                .sendNotification(rejectInfo, TOKEN);
    }

    @Test
    public void rejectApplicantEmptyApplicationTest() throws IOException, InterruptedException {
        when(applicationRepository.findApplicationsByUsernameAndCourseCode(
                rejectInfo.getUsername(), rejectInfo.getCourseCode())).thenReturn(Optional.empty());

        assertThrows(EmptyTargetElementException.class, () -> {
            applicationService.rejectApplicant(rejectInfo, TOKEN);
        });

        verify(applicationRepository, never()).save(any(Application.class));
        verify(notificationCommunicator, never()).sendNotification(rejectInfo, TOKEN);
    }
}
