package nl.tudelft.sem.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.ContractInfo;
import nl.tudelft.sem.Status;
import nl.tudelft.sem.entities.Contract;
import nl.tudelft.sem.exceptions.DuplicateObjectException;
import nl.tudelft.sem.exceptions.EmptyTargetException;
import nl.tudelft.sem.exceptions.InvalidStatusException;
import nl.tudelft.sem.repositories.ContractRepository;
import nl.tudelft.sem.util.ContractBuilder;
import nl.tudelft.sem.util.ContractInfoBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ContractServiceTest {
    private final transient ContractRepository contractRepository =
            Mockito.mock(ContractRepository.class);
    private transient ContractService contractService = new ContractService(contractRepository);

    @Test
    public void getContractTest() throws EmptyTargetException {
        Contract contract = new ContractBuilder().build(25L);

        when(contractRepository.findById(25L)).thenReturn(Optional.of(contract));

        assertEquals(contract, contractService.getContract("25"));
    }

    @Test
    public void getContractEmptyTest() throws EmptyTargetException {
        when(contractRepository.findById(25L)).thenReturn(Optional.empty());

        assertThrows(EmptyTargetException.class, () -> contractService.getContract("25"));
    }

    @Test
    public void revokeContractTest() throws Exception {
        Contract contract = new ContractBuilder().build(0L);
        Contract revokedContract = new ContractBuilder().withStatus(Status.REVOKED).build(0L);

        when(contractRepository.findById(0L))
                .thenReturn(Optional.of(contract));

        assertEquals(revokedContract, contractService.revokeContract("0"));

        verify(contractRepository, times(1)).save(contract);
    }

    @Test
    public void updateContractEmptyTest() throws Exception {
        ContractInfo contractInfo = new ContractInfoBuilder().build();

        when(contractRepository
                .findByUsernameAndCourseCode(
                        contractInfo.getUsername(),
                        contractInfo.getCourseCode()))
                .thenReturn(Optional.empty());


        assertThrows(EmptyTargetException.class, () ->
                contractService.updateContract(contractInfo));

        verify(contractRepository, never()).save(any(Contract.class));
    }

    @Test
    public void rejectContractSignedTest() throws Exception {
        Contract contract = new ContractBuilder().withStatus(Status.APPROVED).build(0L);

        when(contractRepository.findById(0L))
                .thenReturn(Optional.of(contract));

        assertThrows(InvalidStatusException.class, () -> contractService.rejectContract("0"));

        verify(contractRepository, never()).save(any(Contract.class));
    }

    @Test
    public void rejectContractEmptyTest() throws Exception {
        when(contractRepository.findById(0L))
                .thenReturn(Optional.empty());

        assertThrows(EmptyTargetException.class, () -> contractService.rejectContract("0"));

        verify(contractRepository, never()).save(any(Contract.class));
    }

    @Test
    public void getCourseDescriptionNullTest2() throws Exception {
        Contract contract = new ContractBuilder().withExperience(null).build(0L);
        Contract contract2 = new ContractBuilder().withExperience("").build(0L);

        when(contractRepository.findAllByCourseCode(contract.getCourseCode()))
                .thenReturn(List.of(contract, contract2));

        assertThrows(EmptyTargetException.class, () -> contractService.getCourseDescriptions("0"));
    }

    @Test
    public void getCourseDescriptionsEmptyTest() throws Exception {
        when(contractRepository.findAllByCourseCode("CSE2021o")).thenReturn(List.of());

        assertThrows(EmptyTargetException.class, () ->
                contractService.getCourseDescriptions("CSE2021o"));
    }

    @Test
    public void getDescriptionEmptyTest() throws Exception {
        when(contractRepository.findByUsernameAndCourseCode("tdevalckg", "CSE2021"))
                .thenReturn(Optional.empty());

        assertThrows(EmptyTargetException.class,
                () -> contractService.getTaDescription("tdevalckg", "CSE2021"));
    }

    @Test
    public void signContractSignedTest() throws Exception {
        Contract contract = new ContractBuilder().withStatus(Status.APPROVED).build(0L);

        when(contractRepository.findById(0L))
                .thenReturn(Optional.of(contract));

        assertThrows(InvalidStatusException.class, () -> contractService.signContract("0"));

        verify(contractRepository, never()).save(any(Contract.class));
    }

    @Test
    public void writeDescriptionNullTest() throws Exception {
        Contract contract = new ContractBuilder().build(0L);

        when(contractRepository.findByUsernameAndCourseCode(
                contract.getUsername(), contract.getCourseCode()))
                .thenReturn(Optional.of(contract));

        assertThrows(NullPointerException.class,
                () -> contractService.writeTaDescription(contract.getUsername(),
                        contract.getCourseCode(), null));

        verify(contractRepository, never()).save(any(Contract.class));
    }

    @Test
    public void createContractDuplicateTest() throws Exception {
        ContractInfo contractInfo = new ContractInfoBuilder().build();
        Contract contract = new ContractBuilder(contractInfo).build(0L);

        when(contractRepository
                .findByUsernameAndCourseCode(contractInfo.getUsername(),
                        contractInfo.getCourseCode()))
                .thenReturn(Optional.of(contract));

        assertThrows(DuplicateObjectException.class, () ->
                contractService.createContract(contractInfo));

        verify(contractRepository, never()).save(any(Contract.class));
    }

    @Test
    public void deleteContractEmptyTest() throws Exception {
        when(contractRepository.findById(0L)).thenReturn(Optional.empty());

        assertThrows(EmptyTargetException.class, () -> contractService.deleteContract("0"));

        verify(contractRepository, never()).delete(any(Contract.class));
    }

    @Test
    public void revokeContractRevokedTest() throws Exception {
        Contract contract = new ContractBuilder().withStatus(Status.REVOKED).build(0L);

        when(contractRepository.findById(0L))
                .thenReturn(Optional.of(contract));

        assertThrows(InvalidStatusException.class, () -> contractService.revokeContract("0"));

        verify(contractRepository, never()).save(any(Contract.class));
    }

    @Test
    public void getCourseDescriptionTest() throws Exception {
        Contract c1 = new ContractBuilder().withExperience("I had fun").build(0L);
        Contract c2 = new ContractBuilder().withExperience(null).build(0L);
        Contract c3 = new ContractBuilder().withExperience("").build(0L);
        Contract c4 = new ContractBuilder().withExperience("I earned a lot of money").build(0L);
        Contract c5 = new ContractBuilder().withExperience("It fcking sucked").build(0L);

        String courseCode = c1.getCourseCode();

        List<String> expected = List.of(c1.getTaDescription(),
                c4.getTaDescription(),
                c5.getTaDescription());

        when(contractRepository.findAllByCourseCode(courseCode))
                .thenReturn(List.of(c1, c2, c3, c4, c5));

        assertEquals(expected, contractService.getCourseDescriptions(courseCode));
    }

    @Test
    public void getDescriptionTest() throws Exception {
        Contract contract = new ContractBuilder().withExperience("I hated it").build(0L);

        when(contractRepository.findByUsernameAndCourseCode(
                contract.getUsername(), contract.getCourseCode()))
                .thenReturn(Optional.of(contract));

        assertEquals("I hated it",
                contractService.getTaDescription(contract.getUsername(), contract.getCourseCode()));
    }

    @Test
    public void createContractTest() throws Exception {
        ContractInfo contractInfo = new ContractInfoBuilder().build();
        Contract contract = new ContractBuilder(contractInfo).build(0L);

        when(contractRepository
                .findByUsernameAndCourseCode(contractInfo.getUsername(),
                        contractInfo.getCourseCode()))
                .thenReturn(Optional.empty());

        assertEquals(contractInfo, contractService.createContract(contractInfo));

        verify(contractRepository, times(1)).save(contract);
    }

    @Test
    public void writeDescriptionEmptyTest() throws Exception {
        when(contractRepository.findByUsernameAndCourseCode("tdevalckk", "CSE2021a"))
                .thenReturn(Optional.empty());

        assertThrows(EmptyTargetException.class,
                () -> contractService.writeTaDescription("tdevalckk", "CSE2021a", "test"));


        verify(contractRepository, never()).save(any(Contract.class));
    }

    @Test
    public void signContractTest() throws Exception {
        Contract contract = new ContractBuilder().build(0L);
        Contract approvedContract = new ContractBuilder().withStatus(Status.APPROVED).build(0L);

        when(contractRepository.findById(0L))
                .thenReturn(Optional.of(contract));

        assertEquals(approvedContract, contractService.signContract("0"));

        verify(contractRepository, times(1)).save(contract);
    }

    @Test
    public void revokeContractEmptyTest() throws Exception {
        when(contractRepository.findById(0L))
                .thenReturn(Optional.empty());

        assertThrows(EmptyTargetException.class, () -> contractService.revokeContract("0"));

        verify(contractRepository, never()).save(any(Contract.class));
    }

    @Test
    public void signContractEmptyTest() throws Exception {
        when(contractRepository.findById(0L))
                .thenReturn(Optional.empty());

        assertThrows(EmptyTargetException.class, () -> contractService.signContract("0"));

        verify(contractRepository, never()).save(any(Contract.class));
    }

    @Test
    public void rejectContractTest() throws Exception {
        Contract contract = new ContractBuilder().build(0L);
        Contract rejectedContract = new ContractBuilder().withStatus(Status.REJECTED).build(0L);

        when(contractRepository.findById(0L))
                .thenReturn(Optional.of(contract));

        assertEquals(rejectedContract, contractService.rejectContract("0"));

        verify(contractRepository, times(1)).save(contract);
    }

    @Test
    public void deleteContractTest() throws Exception {
        Contract contract = new ContractBuilder().build(0L);

        when(contractRepository.findById(0L)).thenReturn(Optional.of(contract));

        assertEquals(contract, contractService.deleteContract("0"));

        verify(contractRepository, times(1)).deleteById(0L);
    }

    @Test
    public void writeDescriptionTest() throws Exception {
        Contract contract = new ContractBuilder().build(0L);
        String experience = "I had a lot of fun TA'ing for this course. 100% recommend";
        Contract newContract = new ContractBuilder().withExperience(experience).build(0L);

        when(contractRepository.findByUsernameAndCourseCode(
                contract.getUsername(), contract.getCourseCode()))
                .thenReturn(Optional.of(contract));

        assertEquals(newContract, contractService.writeTaDescription(contract.getUsername(),
                contract.getCourseCode(), experience));

        verify(contractRepository, times(1)).save(newContract);
    }

    @Test
    public void updateContractTest() throws Exception {
        ContractInfo contractInfo = new ContractInfoBuilder().build();

        Contract contract = new ContractBuilder(contractInfo).build(0L);
        ContractInfo update = new ContractInfoBuilder()
                .withExperience("This is a change")
                .withHours(27)
                .withTextual("FREE CANDY")
                .withStatus(Status.REJECTED)
                .build();

        Contract expected = new ContractBuilder(update).build(0L);

        when(contractRepository
                .findByUsernameAndCourseCode(
                        contractInfo.getUsername(),
                        contractInfo.getCourseCode()))
                .thenReturn(Optional.of(contract));

        assertEquals(update, contractService.updateContract(update));

        verify(contractRepository, times(1)).save(expected);
    }

    @Test
    public void getDescriptionNullTest() throws Exception {
        Contract contract = new ContractBuilder().withExperience(null).build(0L);

        when(contractRepository.findByUsernameAndCourseCode("tdevalcks", "CSE2021z"))
                .thenReturn(Optional.of(contract));

        assertThrows(NullPointerException.class,
                () -> contractService.getTaDescription("tdevalcks", "CSE2021z"));
    }

    @Test
    public void getCourseDescriptionNullTest() throws Exception {
        Contract contract = new ContractBuilder().withExperience("Yes").build(0L);
        Contract contract2 = new ContractBuilder().withExperience(null).build(0L);
        Contract contract3 = new ContractBuilder().withExperience("").build(0L);

        String courseCode = contract.getCourseCode();

        List<String> expected = List.of(contract.getTaDescription());

        when(contractRepository.findAllByCourseCode(courseCode))
                .thenReturn(List.of(contract, contract2, contract3));

        assertEquals(expected, contractService.getCourseDescriptions(courseCode));
    }

    @Test
    public void numberTasSuccessTest() {
        Contract c1 = new Contract();
        Contract c2 = new Contract();
        when(contractRepository.findAllByCourseCode("CSE2115")).thenReturn(List.of(c1, c2));

        assertEquals(2, contractService.numberOfTasCourse("CSE2115"));
    }

    @Test
    public void getExperiencesEmptyTest() {
        String username = "bbobb";
        when(contractRepository.findByUsername(username)).thenReturn(List.of());

        assertThrows(EmptyTargetException.class, () -> contractService.getExperiences(username));
    }

    @Test
    public void getExperiencesTest() throws EmptyTargetException {
        Contract c1 = new ContractBuilder().withCourse("CSE1000").build(0L);
        Contract c2 = new ContractBuilder().withCourse("CSE2000").build(1L);
        Contract c3 = new ContractBuilder().withCourse("CSE3000").build(2L);

        when(contractRepository.findByUsername(c1.getUsername())).thenReturn(List.of(c1, c2, c3));

        List<String> expected = List.of(c1.getCourseCode(), c2.getCourseCode(), c3.getCourseCode());
        assertEquals(expected, contractService.getExperiences(c1.getUsername()));
    }
}
