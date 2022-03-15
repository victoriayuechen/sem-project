package nl.tudelft.sem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.SelectInfo;
import nl.tudelft.sem.Status;
import nl.tudelft.sem.communication.NotificationCommunicator;
import nl.tudelft.sem.communication.TaCommunicator;
import nl.tudelft.sem.entities.Application;
import nl.tudelft.sem.exceptions.EmptyTargetElementException;
import nl.tudelft.sem.repositories.ApplicationRepository;
import nl.tudelft.sem.util.ApplicationBuilder;
import nl.tudelft.sem.util.FilterParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class FilterServiceTest {
    private transient ApplicationRepository applicationRepository =
            Mockito.mock(ApplicationRepository.class);
    private transient TaCommunicator taCommunicator = Mockito.mock(TaCommunicator.class);
    private transient NotificationCommunicator notificationCommunicator =
            Mockito.mock(NotificationCommunicator.class);
    private final transient FilterService filterService =
            new FilterService(applicationRepository, taCommunicator, notificationCommunicator);

    private transient SelectInfo selectInfo;
    private transient SelectInfo rejectInfo;
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJz"
            + "dWIiOiJhbm5pYmFsZSIsImV4cCI6MTY0MDMw"
            + "NjkyMywiaWF0IjoxNjQwMjcwOTIzfQ.Ab3qsQdzo"
            + "U8viZwWtnFf9NqIG9GDsSssTxrjyXj_8Dg";
    private final transient String courseCode = "CSE2115";
    private final transient String name = "Bob";

    /** Initializes multiple instances of info at before each test.
     */
    @BeforeEach
    public void setup() {
        selectInfo = new SelectInfo();
        selectInfo.setUsername("Willy Wonka");
        selectInfo.setCourseCode("CSE4242");
        selectInfo.setStatus(Status.APPROVED);

        rejectInfo = new SelectInfo();
        rejectInfo.setUsername("Charlie");
        rejectInfo.setCourseCode("CSE4242");
        rejectInfo.setStatus(Status.REJECTED);
    }

    @Test
    public void applyAlgorithmEmptyTest() {
        when(applicationRepository
                .findApplicationsByCourseCode(courseCode)).thenReturn(new ArrayList<>());

        assertThrows(EmptyTargetElementException.class, () ->
                filterService.applyAlgorithm(courseCode,
                        new FilterParameters(null, null, null, null), TOKEN)
        );
    }

    @Test
    public void applyAlgorithmNumberFormat1Test() {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(name)
                .build(0L);
        List<Application> list = new ArrayList<>();
        list.add(app1);
        when(applicationRepository
                .findApplicationsByCourseCode(app1.getCourseCode())).thenReturn(list);

        assertThrows(NumberFormatException.class, () ->
                filterService.applyAlgorithm(courseCode,
                        new FilterParameters("leters", null, null, null), TOKEN)
        );
    }

    @Test
    public void applyAlgorithmNumberFormat2Test() {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(name)
                .build(0L);
        List<Application> list = new ArrayList<>();
        list.add(app1);
        when(applicationRepository
                .findApplicationsByCourseCode(app1.getCourseCode())).thenReturn(list);

        assertThrows(NumberFormatException.class, () ->
                filterService.applyAlgorithm(courseCode,
                        new FilterParameters(null, "letter", null, null), TOKEN)
        );
    }

    @Test
    public void applyAlgorithmNumberFormat3Test() {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(name)
                .build(0L);
        List<Application> list = new ArrayList<>();
        list.add(app1);
        when(applicationRepository
                .findApplicationsByCourseCode(app1.getCourseCode())).thenReturn(list);

        assertThrows(NumberFormatException.class, () ->
                filterService.applyAlgorithm(courseCode,
                        new FilterParameters(null, null, "letrs", null), TOKEN)
        );
    }

    @Test
    public void applyAlgorithmNumberFormat4Test() {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(name)
                .build(0L);
        List<Application> list = new ArrayList<>();
        list.add(app1);
        when(applicationRepository
                .findApplicationsByCourseCode(app1.getCourseCode())).thenReturn(list);

        assertThrows(NumberFormatException.class, () ->
                filterService.applyAlgorithm(courseCode,
                        new FilterParameters(null, null, null, "lers"), TOKEN)
        );
    }

    @Test
    public void applyAlgorithmNoRequirements() throws Exception {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(name)
                .build(0L);
        List<Application> list = new ArrayList<>();
        list.add(app1);
        when(applicationRepository
                .findApplicationsByCourseCode(app1.getCourseCode())).thenReturn(list);

        assertEquals(list, filterService.applyAlgorithm(courseCode,
                new FilterParameters(null, null, null, null), TOKEN).recommend(TOKEN));
    }

    @Test
    public void applyAlgorithmMinGradeRemoved() throws Exception {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(name)
                .withGrade(5.5)
                .build(0L);
        List<Application> list = new ArrayList<>();
        list.add(app1);
        when(applicationRepository
                .findApplicationsByCourseCode(app1.getCourseCode())).thenReturn(list);

        assertTrue(filterService.applyAlgorithm(courseCode,
                new FilterParameters("6.1", null, null, null), TOKEN).recommend(TOKEN).isEmpty());
    }

    @Test
    public void applyAlgorithmMinGradeKept() throws Exception {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(name)
                .withGrade(6.5)
                .build(0L);
        List<Application> list = new ArrayList<>();
        list.add(app1);
        when(applicationRepository
                .findApplicationsByCourseCode(app1.getCourseCode())).thenReturn(list);

        assertEquals(1, filterService.applyAlgorithm(courseCode,
                new FilterParameters("6.2", null, null, null), TOKEN).recommend(TOKEN).size());
    }

    @Test
    public void applyAlgorithmMinGradeSame() throws Exception {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(name)
                .withGrade(6.0)
                .build(0L);
        List<Application> list = new ArrayList<>();
        list.add(app1);
        when(applicationRepository
                .findApplicationsByCourseCode(app1.getCourseCode())).thenReturn(list);

        assertEquals(1, filterService.applyAlgorithm(courseCode,
                new FilterParameters("6.0", null, null, null), TOKEN).recommend(TOKEN).size());
    }

    @Test
    public void applyAlgorithmMinRatingKept() throws Exception {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(name)
                .build(0L);
        List<Application> list = new ArrayList<>();
        list.add(app1);
        when(applicationRepository
                .findApplicationsByCourseCode(app1.getCourseCode())).thenReturn(list);
        when(taCommunicator.obtainRatings(app1.getUsername(), TOKEN)).thenReturn(List.of(4));

        assertEquals(1, filterService.applyAlgorithm(courseCode,
                new FilterParameters(null, "3", null, null), TOKEN).recommend(TOKEN).size());
    }

    @Test
    public void applyAlgorithmMinRatingRemoved() throws Exception {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(name)
                .withGrade(6.0)
                .build(0L);
        List<Application> list = new ArrayList<>();
        list.add(app1);
        when(applicationRepository
                .findApplicationsByCourseCode(courseCode)).thenReturn(list);
        when(taCommunicator.obtainRatings(app1.getUsername(), TOKEN)).thenReturn(List.of(2));

        assertTrue(filterService.applyAlgorithm(courseCode,
                new FilterParameters(null, "3", null, null), TOKEN).recommend(TOKEN).isEmpty());
    }

    @Test
    public void applyAlgorithmMinRatingSame() throws Exception {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(name)
                .withGrade(6.0)
                .build(0L);
        List<Application> list = new ArrayList<>();
        list.add(app1);
        when(applicationRepository
                .findApplicationsByCourseCode(courseCode)).thenReturn(list);
        when(taCommunicator.obtainRatings(app1.getUsername(), TOKEN)).thenReturn(List.of(3));

        assertEquals(1, filterService.applyAlgorithm(courseCode,
                new FilterParameters(null, "3", null, null), TOKEN).recommend(TOKEN).size());
    }

    @Test
    public void applyAlgorithmMinAvgRatingKept() throws Exception {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(name)
                .build(0L);
        List<Application> list = new ArrayList<>();
        list.add(app1);
        when(applicationRepository
                .findApplicationsByCourseCode(app1.getCourseCode())).thenReturn(list);
        when(taCommunicator.obtainRatings(app1.getUsername(), TOKEN)).thenReturn(List.of(4, 5, 4));

        assertEquals(1, filterService.applyAlgorithm(courseCode,
                new FilterParameters(null, null, "3", null), TOKEN).recommend(TOKEN).size());
    }

    @Test
    public void applyAlgorithmMinAvgRatingRemoved() throws Exception {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(name)
                .build(0L);
        List<Application> list = new ArrayList<>();
        list.add(app1);
        when(applicationRepository
                .findApplicationsByCourseCode(courseCode)).thenReturn(list);
        when(taCommunicator.obtainRatings(app1.getUsername(), TOKEN)).thenReturn(List.of(2, 3, 2));

        assertTrue(filterService.applyAlgorithm(courseCode,
                new FilterParameters(null, null, "3", null), TOKEN).recommend(TOKEN).isEmpty());
    }

    @Test
    public void applyAlgorithmMinAvgRatingSame() throws Exception {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(name)
                .build(0L);
        List<Application> list = new ArrayList<>();
        list.add(app1);
        when(applicationRepository
                .findApplicationsByCourseCode(courseCode)).thenReturn(list);
        when(taCommunicator.obtainRatings(app1.getUsername(), TOKEN)).thenReturn(List.of(3, 2, 4));

        assertEquals(1, filterService.applyAlgorithm(courseCode,
                new FilterParameters(null, null, "3", null), TOKEN).recommend(TOKEN).size());
    }

    @Test
    public void applyAlgorithmMinTaKept() throws Exception {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(name)
                .build(0L);
        List<Application> list = new ArrayList<>();
        list.add(app1);
        when(applicationRepository
                .findApplicationsByCourseCode(app1.getCourseCode())).thenReturn(list);
        when(taCommunicator.obtainExperiences(app1.getUsername(), TOKEN))
                .thenReturn(List.of("Experience1", "Experience2"));

        assertEquals(1, filterService.applyAlgorithm(courseCode,
                new FilterParameters(null, null, null, "1"), TOKEN).recommend(TOKEN).size());
    }

    @Test
    public void applyAlgorithmMinTaRemoved() throws Exception {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(name)
                .build(0L);
        List<Application> list = new ArrayList<>();
        list.add(app1);
        when(applicationRepository
                .findApplicationsByCourseCode(courseCode)).thenReturn(list);
        when(taCommunicator.obtainExperiences(app1.getUsername(), TOKEN)).thenReturn(List.of());

        assertTrue(filterService.applyAlgorithm(courseCode,
                new FilterParameters(null, null, null, "1"), TOKEN).recommend(TOKEN).isEmpty());
    }

    @Test
    public void applyAlgorithmMinTaSame() throws Exception {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(name)
                .build(0L);
        List<Application> list = new ArrayList<>();
        list.add(app1);
        when(applicationRepository
                .findApplicationsByCourseCode(courseCode)).thenReturn(list);
        when(taCommunicator.obtainExperiences(app1.getUsername(), TOKEN))
                .thenReturn(List.of("Experience"));

        assertEquals(1, filterService.applyAlgorithm(courseCode,
                new FilterParameters(null, null, null, "1"), TOKEN).recommend(TOKEN).size());
    }

    @Test
    public void autoRejectEmptyTest() throws Exception {
        when(applicationRepository
                .findApplicationsByCourseCode(courseCode)).thenReturn(new ArrayList<>());

        assertThrows(EmptyTargetElementException.class, () ->
                filterService.autoReject(courseCode, null, TOKEN)
        );
    }

    @Test
    public void autoRejectNumberFormat1Test() {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(name)
                .build(0L);
        List<Application> list = new ArrayList<>();
        list.add(app1);
        when(applicationRepository
                .findApplicationsByCourseCode(app1.getCourseCode())).thenReturn(list);

        assertThrows(NumberFormatException.class, () ->
                filterService.autoReject(courseCode, new FilterParameters("letters", null,
                        null, null), TOKEN)
        );
    }

    @Test
    public void autoRejectNoRequirements() throws Exception {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(name)
                .build(0L);
        List<Application> list = new ArrayList<>();
        list.add(app1);
        when(applicationRepository
                .findApplicationsByCourseCode(app1.getCourseCode())).thenReturn(list);
        filterService.autoReject(courseCode, new FilterParameters(null, null,
                null, null), TOKEN);
        verify(notificationCommunicator, never()).sendNotification(selectInfo, TOKEN);
    }

    @Test
    public void autoRejectMinGradeKept() throws Exception {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(name)
                .withGrade(6.5)
                .build(0L);
        List<Application> list = new ArrayList<>();
        list.add(app1);
        when(applicationRepository
                .findApplicationsByCourseCode(app1.getCourseCode())).thenReturn(list);
        filterService.autoReject(courseCode, new FilterParameters("6.1", null,
                null, null), TOKEN);
        verify(notificationCommunicator, never()).sendNotification(selectInfo, TOKEN);
    }

    @Test
    public void autoRejectMinGradeRejected() throws Exception {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(rejectInfo.getUsername())
                .withGrade(5.5)
                .withCourse(rejectInfo.getCourseCode())
                .build(0L);
        List<Application> list = new ArrayList<>();
        list.add(app1);
        when(applicationRepository
                .findApplicationsByCourseCode(app1.getCourseCode())).thenReturn(list);

        filterService.autoReject(rejectInfo.getCourseCode(), new FilterParameters("6.3", null,
                null, null), TOKEN);
        app1.setStatus(Status.REJECTED);
        verify(applicationRepository).save(app1);
        verify(notificationCommunicator, times(1)).sendNotification(rejectInfo, TOKEN);
    }

    @Test
    public void autoRejectMinGradeSame() throws Exception {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(name)
                .withGrade(6.0)
                .build(0L);
        List<Application> list = new ArrayList<>();
        list.add(app1);
        when(applicationRepository
                .findApplicationsByCourseCode(app1.getCourseCode())).thenReturn(list);

        filterService.autoReject(courseCode, new FilterParameters("6.0", null,
                null, null), TOKEN);
        verify(notificationCommunicator, never()).sendNotification(selectInfo, TOKEN);
    }

    @Test
    public void autoRejectNotificationError() throws Exception {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(rejectInfo.getUsername())
                .withGrade(5.5)
                .withCourse(rejectInfo.getCourseCode())
                .build(0L);
        List<Application> list = new ArrayList<>();
        list.add(app1);
        when(applicationRepository
                .findApplicationsByCourseCode(app1.getCourseCode())).thenReturn(list);
        SelectInfo selectInfo = new SelectInfo();
        selectInfo.setStatus(Status.REJECTED);
        selectInfo.setCourseCode(app1.getCourseCode());
        selectInfo.setUsername(app1.getUsername());
        doThrow(new IOException()).when(notificationCommunicator)
                .sendNotification(selectInfo, TOKEN);

        assertThrows(IOException.class, () ->
                filterService.autoReject(rejectInfo.getCourseCode(),
                        new FilterParameters("6.4", null,
                        null, null), TOKEN));
        app1.setStatus(Status.REJECTED);
        verify(applicationRepository).save(app1);
    }

    @Test
    public void applyAlgorithmMinRatingError() throws Exception {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(name)
                .withGrade(6.0)
                .build(0L);
        List<Application> list = new ArrayList<>();
        list.add(app1);
        when(applicationRepository
                .findApplicationsByCourseCode(courseCode)).thenReturn(list);
        doThrow(new IOException()).when(taCommunicator).obtainRatings(app1.getUsername(), TOKEN);

        assertTrue(filterService.applyAlgorithm(courseCode,
                        new FilterParameters(null, "3", null, null), TOKEN)
                .recommend(TOKEN).isEmpty());
    }

    @Test
    public void applyAlgorithmMinAvgRatingError() throws Exception {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(name)
                .build(0L);
        List<Application> list = new ArrayList<>();
        list.add(app1);
        when(applicationRepository
                .findApplicationsByCourseCode(courseCode)).thenReturn(list);
        doThrow(new IOException()).when(taCommunicator).obtainRatings(app1.getUsername(), TOKEN);

        assertTrue(filterService.applyAlgorithm(courseCode,
                        new FilterParameters(null, null, "3.0", null), TOKEN)
                .recommend(TOKEN).isEmpty());
    }

    @Test
    public void applyAlgorithmMinTaError() throws Exception {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName(name)
                .build(0L);
        List<Application> list = new ArrayList<>();
        list.add(app1);
        when(applicationRepository
                .findApplicationsByCourseCode(courseCode)).thenReturn(list);
        doThrow(new IOException()).when(taCommunicator)
                .obtainExperiences(app1.getUsername(), TOKEN);

        assertTrue(filterService.applyAlgorithm(courseCode,
                        new FilterParameters(null, null, null, "1"), TOKEN)
                .recommend(TOKEN).isEmpty());
    }
}
