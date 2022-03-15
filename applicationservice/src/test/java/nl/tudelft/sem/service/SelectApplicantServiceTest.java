package nl.tudelft.sem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;
import nl.tudelft.sem.SelectInfo;
import nl.tudelft.sem.Status;
import nl.tudelft.sem.communication.CourseCommunicator;
import nl.tudelft.sem.communication.NotificationCommunicator;
import nl.tudelft.sem.communication.TaCommunicator;
import nl.tudelft.sem.entities.Application;
import nl.tudelft.sem.exceptions.EmptyTargetElementException;
import nl.tudelft.sem.exceptions.InvalidApplicationException;
import nl.tudelft.sem.repositories.ApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;




public class SelectApplicantServiceTest {
    private transient ApplicationRepository applicationRepository =
            Mockito.mock(ApplicationRepository.class);
    private transient TaCommunicator taCommunicator = Mockito.mock(TaCommunicator.class);
    private transient NotificationCommunicator notificationCommunicator =
            Mockito.mock(NotificationCommunicator.class);
    private transient CourseCommunicator courseCommunicator =
            Mockito.mock(CourseCommunicator.class);
    private final transient SelectApplicantService selectApplicantService =
            new SelectApplicantService(applicationRepository, taCommunicator,
                    notificationCommunicator, courseCommunicator);

    private transient SelectInfo selectInfo;
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJz"
            + "dWIiOiJhbm5pYmFsZSIsImV4cCI6MTY0MDMw"
            + "NjkyMywiaWF0IjoxNjQwMjcwOTIzfQ.Ab3qsQdzo"
            + "U8viZwWtnFf9NqIG9GDsSssTxrjyXj_8Dg";

    /** Initializes multiple instances of info at before each test.
     */
    @BeforeEach
    public void setup() {
        selectInfo = new SelectInfo();
        selectInfo.setUsername("Willy Wonka");
        selectInfo.setCourseCode("CSE4242");
        selectInfo.setStatus(Status.APPROVED);
    }

    @Test
    public void selectApplicantSuccessTest()
            throws IOException, InterruptedException, EmptyTargetElementException,
            InvalidApplicationException {
        Application app = new Application();
        when(courseCommunicator.courseOpenForRecruitment(selectInfo, TOKEN))
                .thenReturn(true);
        when(applicationRepository
                .findApplicationsByUsernameAndCourseCode(selectInfo.getUsername(),
                        selectInfo.getCourseCode())).thenReturn(Optional.of(app));
        when(taCommunicator.addTaToCourse(selectInfo, TOKEN)).thenReturn(true);
        app.setStatus(Status.APPROVED);

        assertEquals(app, selectApplicantService.selectApplicant(selectInfo, TOKEN));

        verify(applicationRepository, times(1)).save(app);
        verify(notificationCommunicator, times(1))
                .sendNotification(selectInfo, TOKEN);
    }

    @Test
    public void selectApplicantNotOpenTest() throws IOException, InterruptedException {
        when(courseCommunicator.courseOpenForRecruitment(selectInfo, TOKEN))
                .thenReturn(false);

        assertThrows(InvalidApplicationException.class, () -> {
            selectApplicantService.selectApplicant(selectInfo, TOKEN);
        });

        verify(applicationRepository, never())
                .findApplicationsByUsernameAndCourseCode(selectInfo.getUsername(),
                selectInfo.getCourseCode());
        verify(taCommunicator, never()).addTaToCourse(selectInfo, TOKEN);
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    public void selectApplicantNoApplicationTest() throws IOException, InterruptedException {
        when(courseCommunicator.courseOpenForRecruitment(selectInfo, TOKEN))
                .thenReturn(true);
        when(applicationRepository.findApplicationsByUsernameAndCourseCode(
                selectInfo.getUsername(), selectInfo.getCourseCode())).thenReturn(Optional.empty());

        assertThrows(EmptyTargetElementException.class, () -> {
            selectApplicantService.selectApplicant(selectInfo, TOKEN);
        });

        verify(taCommunicator, never()).addTaToCourse(selectInfo, TOKEN);
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    public void selectApplicantCouldNotSaveTaTest() throws IOException, InterruptedException {
        Application app = new Application();
        when(courseCommunicator.courseOpenForRecruitment(selectInfo, TOKEN))
                .thenReturn(true);
        when(applicationRepository
                .findApplicationsByUsernameAndCourseCode(selectInfo.getUsername(),
                        selectInfo.getCourseCode())).thenReturn(Optional.of(app));
        when(taCommunicator.addTaToCourse(selectInfo, TOKEN)).thenReturn(false);

        assertThrows(InvalidApplicationException.class, () -> {
            selectApplicantService.selectApplicant(selectInfo, TOKEN);
        });

        verify(applicationRepository, never()).save(app);
        verify(notificationCommunicator, never())
                .sendNotification(selectInfo, TOKEN);
    }
}
