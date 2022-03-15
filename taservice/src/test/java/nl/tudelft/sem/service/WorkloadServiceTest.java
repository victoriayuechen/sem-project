package nl.tudelft.sem.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import nl.tudelft.sem.SelectInfo;
import nl.tudelft.sem.Status;
import nl.tudelft.sem.WorkloadInfo;
import nl.tudelft.sem.communication.CentralCommunicator;
import nl.tudelft.sem.entities.Workload;
import nl.tudelft.sem.exceptions.EmptyTargetException;
import nl.tudelft.sem.repositories.WorkloadRepository;
import nl.tudelft.sem.util.WorkloadBuilder;
import nl.tudelft.sem.util.WorkloadInfoBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class WorkloadServiceTest {
    private final transient WorkloadRepository workloadRepository =
            Mockito.mock(WorkloadRepository.class);
    private final transient CentralCommunicator centralCommunicator =
            Mockito.mock(CentralCommunicator.class);
    private transient WorkloadService workloadService =
            new WorkloadService(workloadRepository, centralCommunicator);

    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJz"
            + "dWIiOiJhbm5pYmFsZSIsImV4cCI6MTY0MDMw"
            + "NjkyMywiaWF0IjoxNjQwMjcwOTIzfQ.Ab3qsQdzo"
            + "U8viZwWtnFf9NqIG9GDsSssTxrjyXj_8Dg";

    @Test
    public void overviewEmptyTest() throws Exception {
        String courseCode = "CSE2020";

        when(workloadRepository.findAllByCourseCode(courseCode)).thenReturn(List.of());

        assertThrows(EmptyTargetException.class, () -> workloadService.courseOverview(courseCode));
    }

    @Test
    public void viewTaTest() throws Exception {
        Workload w1 = new WorkloadBuilder().withName("jan").build(0L);
        Workload w2 = new WorkloadBuilder().withName("klaas").build(1L);
        Workload w3 = new WorkloadBuilder().withName("piet").build(2L);
        Workload w4 = new WorkloadBuilder().withName("henk").build(3L);
        Workload w5 = new WorkloadBuilder().withName("gert").build(4L);
        String courseCode = w1.getCourseCode();

        when(workloadRepository.findAllByCourseCode(courseCode))
                .thenReturn(List.of(w1, w2, w3, w4, w5));

        Set<String> expected = Set.of(w1.getUsername(),
                w2.getUsername(),
                w3.getUsername(),
                w4.getUsername(),
                w5.getUsername());

        assertEquals(expected, new HashSet<>(workloadService.viewTas(courseCode)));
    }

    @Test
    public void overviewTest() throws Exception {
        Workload w1 = new WorkloadBuilder().withHours(10).build(0L);
        Workload w2 = new WorkloadBuilder().withName("tdevalckz").withHours(8).build(1L);
        Workload w3 = new WorkloadBuilder().withHours(30).build(2L);
        Workload w4 = new WorkloadBuilder().withName("tdevalckz").withHours(12).build(3L);
        Workload w5 = new WorkloadBuilder().withName("tdevalckz").withHours(4).build(4L);
        String courseCode = w1.getCourseCode();

        when(workloadRepository.findAllByCourseCode(courseCode))
                .thenReturn(List.of(w1, w2, w3, w4, w5));

        String expectedString1 = "Name: rmihalachiuta; Hours/Week: 4.0";
        String expectedString2 = "Name: tdevalckz; Hours/Week: 2.4";

        assertEquals(List.of(expectedString1, expectedString2),
                workloadService.courseOverview(courseCode));
    }

    @Test
    public void updateWorkloadEmptyTest() throws Exception {
        WorkloadInfo workloadInfo = new WorkloadInfoBuilder().build();

        when(workloadRepository
                .findWorkloadByUsernameAndCourseCode(
                        workloadInfo.getUsername(),
                        workloadInfo.getCourseCode()))
                .thenReturn(Optional.empty());

        assertThrows(EmptyTargetException.class, () ->
                workloadService.updateWorkload(workloadInfo));

        verify(workloadRepository, never()).save(any(Workload.class));
    }

    @Test
    public void averageTaTest() throws Exception {
        String name = "tdevalckr";
        Workload w1 = new WorkloadBuilder().withHours(10).build(0L);
        Workload w2 = new WorkloadBuilder().withName(name).withHours(8).build(1L);
        Workload w3 = new WorkloadBuilder().withHours(30).build(2L);
        Workload w4 = new WorkloadBuilder().withName(name).withHours(12).build(3L);
        Workload w5 = new WorkloadBuilder().withName(name).withHours(4).build(4L);
        String courseCode = w1.getCourseCode();

        when(workloadRepository.findAllByCourseCode(courseCode))
                .thenReturn(List.of(w1, w2, w3, w4, w5));

        float expected = 2.4f;

        Assertions.assertEquals(expected, workloadService.averageTa(courseCode, name));
    }

    @Test
    public void deleteWorkloadTest() throws Exception {
        Workload workload = new WorkloadBuilder().build(0L);

        when(workloadRepository.findById(0L)).thenReturn(Optional.of(workload));

        assertEquals(workload, workloadService.deleteWorkload("0"));

        verify(workloadRepository, times(1)).deleteById(0L);
    }

    @Test
    public void viewTaDuplicateTest() throws Exception {
        Workload w1 = new WorkloadBuilder().build(0L);
        Workload w2 = new WorkloadBuilder().build(1L);
        Workload w3 = new WorkloadBuilder().build(2L);
        Workload w4 = new WorkloadBuilder().build(3L);
        Workload w5 = new WorkloadBuilder().build(4L);
        String courseCode = w1.getCourseCode();

        when(workloadRepository.findAllByCourseCode(courseCode))
                .thenReturn(List.of(w1, w2, w3, w4, w5));

        assertEquals(List.of(w1.getUsername()), workloadService.viewTas(courseCode));
    }

    @Test
    public void deleteWorkloadEmptyTest() throws Exception {
        when(workloadRepository.findById(0L)).thenReturn(Optional.empty());

        assertThrows(EmptyTargetException.class, () -> workloadService.deleteWorkload("0"));

        verify(workloadRepository, never()).delete(any(Workload.class));
    }

    @Test
    public void updateWorkloadTest() throws Exception {
        WorkloadInfo workloadInfo = new WorkloadInfoBuilder().build();

        Workload workload = new WorkloadBuilder(workloadInfo).build(0L);
        WorkloadInfo update = new WorkloadInfoBuilder()
                .withHours(16)
                .withStatus(Status.REVOKED).build();

        Workload expected = new WorkloadBuilder(update).build(0L);

        when(workloadRepository
                .findWorkloadByUsernameAndCourseCode(
                        workloadInfo.getUsername(),
                        workloadInfo.getCourseCode()))
                .thenReturn(Optional.of(workload));

        assertEquals(update, workloadService.updateWorkload(update));

        verify(workloadRepository, times(1)).save(expected);
    }

    @Test
    public void viewTaEmptyTest() throws Exception {
        String courseCode = "CSE2021s";

        when(workloadRepository.findAllByCourseCode(courseCode)).thenReturn(List.of());

        assertThrows(EmptyTargetException.class, () -> workloadService.viewTas(courseCode));
    }

    @Test
    public void rejectHours() throws Exception {
        Workload w1 = new WorkloadBuilder()
                .withHours(30)
                .build(0L);

        when(workloadRepository.findById(0L))
                .thenReturn(Optional.of(w1));

        assertEquals(w1, workloadService.rejectHours("0", TOKEN));
        assertEquals(w1.getStatus(), Status.REJECTED);


        SelectInfo selectInfo = new SelectInfo();
        selectInfo.setStatus(Status.REJECTED);
        selectInfo.setCourseCode(w1.getCourseCode());
        selectInfo.setUsername(w1.getUsername());
        verify(centralCommunicator).sendNotification(selectInfo, TOKEN);
    }

    @Test
    public void rejectHoursEmptyTest() {
        when(workloadRepository.findById(0L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(EmptyTargetException.class, () ->
                workloadService.rejectHours("0", TOKEN)
        );
    }

    @Test
    public void getWorkloadsPerCourseSuccessTest() throws Exception {
        Workload w1 = new WorkloadBuilder().withHours(2).build(1L);
        Workload w2 =  new WorkloadBuilder().withHours(4).build(2L);

        when(workloadRepository.findAllByCourseCode("CSE2112"))
                .thenReturn(List.of(w1, w2));

        Set<Integer> expected = new HashSet<>();
        expected.add(2);
        expected.add(4);

        assertEquals(expected, new HashSet<>(workloadService.getWorkloadHoursPerCourse("CSE2112")));
    }

    @Test
    public void getWorkloadsPerCourseNoWorkloadsTest() {
        when(workloadRepository.findAllByCourseCode("CSE2115")).thenReturn(List.of());

        assertThrows(EmptyTargetException.class, () -> {
            workloadService.getWorkloadHoursPerCourse("CSE2115");
        });
    }
}
