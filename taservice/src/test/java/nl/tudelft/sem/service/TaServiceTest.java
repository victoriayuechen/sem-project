package nl.tudelft.sem.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;
import nl.tudelft.sem.AverageWorkload;
import nl.tudelft.sem.SelectInfo;
import nl.tudelft.sem.Status;
import nl.tudelft.sem.communication.CentralCommunicator;
import nl.tudelft.sem.entities.Contract;
import nl.tudelft.sem.entities.Ta;
import nl.tudelft.sem.entities.Workload;
import nl.tudelft.sem.exceptions.AddRoleFailureException;
import nl.tudelft.sem.exceptions.EmptyTargetException;
import nl.tudelft.sem.repositories.ContractRepository;
import nl.tudelft.sem.repositories.TaRepository;
import nl.tudelft.sem.repositories.WorkloadRepository;
import nl.tudelft.sem.util.ContractBuilder;
import nl.tudelft.sem.util.WorkloadBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@SuppressWarnings({"PMD.DataflowAnomalyAnalysis", "PMD.AvoidDuplicateLiterals"})
public class TaServiceTest {
    private final transient ContractRepository contractRepository =
            Mockito.mock(ContractRepository.class);
    private final transient TaRepository taRepository =
            Mockito.mock(TaRepository.class);
    private final transient WorkloadRepository workloadRepository =
            Mockito.mock(WorkloadRepository.class);
    private final transient CentralCommunicator centralCommunicator =
        Mockito.mock(CentralCommunicator.class);
    private transient TaService taService = new TaService(taRepository,
            contractRepository,
            workloadRepository,
            centralCommunicator);

    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJz"
            + "dWIiOiJhbm5pYmFsZSIsImV4cCI6MTY0MDMw"
            + "NjkyMywiaWF0IjoxNjQwMjcwOTIzfQ.Ab3qsQdzo"
            + "U8viZwWtnFf9NqIG9GDsSssTxrjyXj_8Dg";

    @Test
    public void approveHours() throws Exception {
        Workload w1 = new WorkloadBuilder()
                .withHours(30)
                .build(0L);

        Contract contract = new ContractBuilder()
                .withHours(35)
                .build(1L);

        when(workloadRepository.findById(0L))
                .thenReturn(Optional.of(w1));

        when(contractRepository.findByUsernameAndCourseCode(
                w1.getUsername(), w1.getCourseCode()
        )).thenReturn(Optional.of(contract));

        assertEquals(w1, taService.checkHours("0", TOKEN));
        assertEquals(w1.getStatus(), Status.APPROVED);

        SelectInfo selectInfo = new SelectInfo();
        selectInfo.setStatus(Status.APPROVED);
        selectInfo.setCourseCode(w1.getCourseCode());
        selectInfo.setUsername(w1.getUsername());
        verify(centralCommunicator).sendNotification(selectInfo, TOKEN);
    }

    @Test
    public void approveHours2() throws Exception {
        Workload w1 = new WorkloadBuilder()
                .withHours(30)
                .build(0L);

        Contract contract = new ContractBuilder()
                .withHours(30)
                .build(1L);

        when(workloadRepository.findById(0L))
                .thenReturn(Optional.of(w1));

        when(contractRepository.findByUsernameAndCourseCode(
                w1.getUsername(), w1.getCourseCode()
        )).thenReturn(Optional.of(contract));

        assertEquals(w1, taService.checkHours("0", TOKEN));
        assertEquals(w1.getStatus(), Status.APPROVED);
    }

    @Test
    public void emptyHoursTest() {

        when(workloadRepository.findById(0L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(EmptyTargetException.class, () ->
                taService.checkHours("0", TOKEN)
        );
    }

    @Test
    public void emptyContractTest() {
        Workload w1 = new WorkloadBuilder()
                .withHours(30)
                .build(0L);

        when(contractRepository.findByUsernameAndCourseCode(
                w1.getUsername(), w1.getCourseCode()
        ))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(EmptyTargetException.class, () ->
                taService.checkHours("0", TOKEN)
        );
    }

    @Test
    public void checkHoursAndRejectTest() throws Exception {
        Workload w1 = new WorkloadBuilder()
                .withHours(30)
                .build(0L);

        Contract contract = new ContractBuilder()
                .withHours(20)
                .build(1L);

        when(workloadRepository.findById(0L))
                .thenReturn(Optional.of(w1));

        when(contractRepository.findByUsernameAndCourseCode(
                w1.getUsername(), w1.getCourseCode()
        )).thenReturn(Optional.of(contract));

        assertEquals(w1, taService.checkHours("0", TOKEN));
        assertEquals(w1.getStatus(), Status.REJECTED);

        SelectInfo selectInfo = new SelectInfo();
        selectInfo.setStatus(Status.REJECTED);
        selectInfo.setCourseCode(w1.getCourseCode());
        selectInfo.setUsername(w1.getUsername());

        verify(centralCommunicator).sendNotification(selectInfo, TOKEN);
    }

    @Test
    public void getAverageHoursCourseTest() throws IOException, InterruptedException {
        Workload workload = new WorkloadBuilder().build(0L);
        when(centralCommunicator.getAverageHoursCourse(workload.getCourseCode(), TOKEN))
                .thenReturn(20);
        Assertions.assertEquals(20, taService
                .getAverageHoursCourse(workload.getCourseCode(), TOKEN));
    }

    @Test
    public void saveTaToDbSuccess() throws Exception {
        when(taRepository.findByUsername("oompa")).thenReturn(Optional.empty());
        when(centralCommunicator.addTaRole("oompa", TOKEN)).thenReturn(true);
        Ta oompa = new Ta();
        oompa.setUsername("oompa");

        assertTrue(taService.saveTaToDatabase("oompa", TOKEN));

        verify(taRepository, times(1)).save(oompa);
    }

    @Test
    public void saveTaToDbExistsTest() throws Exception {
        Ta charlie = new Ta();
        when(taRepository.findByUsername("charlie")).thenReturn(Optional.of(charlie));
        assertTrue(taService.saveTaToDatabase("charlie", TOKEN));
    }

    @Test
    public void saveTaAddRoleFailedTest() throws Exception {
        when(taRepository.findByUsername("oompa")).thenReturn(Optional.empty());
        when(centralCommunicator.addTaRole("oompa", TOKEN)).thenReturn(false);

        assertThrows(AddRoleFailureException.class, () -> {
            taService.saveTaToDatabase("oompa", TOKEN);
        });

        verify(taRepository, never()).save(any(Ta.class));
    }

    @Test
    public void declareHoursWorkedSuccessTest() throws Exception {
        AverageWorkload averageWorkload = new AverageWorkload();
        averageWorkload.setCourseCode("CSE2115");
        averageWorkload.setUsername("Willy Wonka");
        averageWorkload.setAverageHours(10);

        Workload w1 = new WorkloadBuilder()
                .withHours(10)
                .withCourse("CSE2115")
                .withName("Willy Wonka")
                .withStatus(Status.PENDING).build(0L);

        Contract contract = new Contract();

        when(contractRepository
            .findByUsernameAndCourseCode("Willy Wonka", "CSE2115"))
            .thenReturn(Optional.of(contract));
        assertEquals(w1, taService.declareHoursWorked(averageWorkload));
        verify(workloadRepository, times(1)).save(w1);
    }

    @Test
    public void declareHoursWorkedNoContractTest() {
        AverageWorkload averageWorkload = new AverageWorkload();
        averageWorkload.setUsername("Willy Wonka");
        averageWorkload.setAverageHours(10);

        when(contractRepository
            .findByUsernameAndCourseCode("Willy Wonka", "CSE2115"))
            .thenReturn(Optional.empty());

        assertThrows(EmptyTargetException.class, () -> {
            taService.declareHoursWorked(averageWorkload);
        });

        verify(workloadRepository, never()).save(any(Workload.class));
    }
}
