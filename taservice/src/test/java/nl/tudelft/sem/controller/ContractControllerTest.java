package nl.tudelft.sem.controller;

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
import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.ContractInfo;
import nl.tudelft.sem.Status;
import nl.tudelft.sem.entities.Contract;
import nl.tudelft.sem.exceptions.DuplicateObjectException;
import nl.tudelft.sem.exceptions.EmptyTargetException;
import nl.tudelft.sem.exceptions.InvalidStatusException;
import nl.tudelft.sem.service.ContractService;
import nl.tudelft.sem.util.ContractBuilder;
import nl.tudelft.sem.util.ContractInfoBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ContractController.class)
public class ContractControllerTest {
    @MockBean
    private transient ContractService contractService;
    private final transient String admin = "ADMIN";

    @Autowired
    private transient MockMvc mockMvc;
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJz"
            + "dWIiOiJhbm5pYmFsZSIsImV4cCI6MTY0MDMw"
            + "NjkyMywiaWF0IjoxNjQwMjcwOTIzfQ.Ab3qsQdzo"
            + "U8viZwWtnFf9NqIG9GDsSssTxrjyXj_8Dg";
    private static final String AUTHORIZATION = "Authorization";

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

    @WithMockUser(roles = admin)
    @Test
    public void revokeContractTest() throws Exception {
        Contract revokedContract = new ContractBuilder().withStatus(Status.REVOKED).build(0L);

        when(contractService.revokeContract("0")).thenReturn(revokedContract);

        mockMvc.perform(post("/contract/revokeContract/{contractId}", 0L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(revokedContract)));
    }

    @WithMockUser(roles = admin)
    @Test
    public void updateContractEmptyTest() throws Exception {
        ContractInfo contractInfo = new ContractInfoBuilder().build();
        when(contractService.updateContract(contractInfo)).thenThrow(new EmptyTargetException(""));

        mockMvc.perform(put("/contract/updateContract")
                .content(asJsonString(contractInfo))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = admin)
    @Test
    public void rejectContractEmptyTest() throws Exception {
        when(contractService.rejectContract("0")).thenThrow(new EmptyTargetException(""));

        mockMvc.perform(post("/contract/rejectContract/{contractId}", 0L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = admin)
    @Test
    public void rejectContractSignedTest() throws Exception {
        when(contractService.rejectContract("0")).thenThrow(new InvalidStatusException(""));

        mockMvc.perform(post("/contract/rejectContract/{contractId}", 0L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = admin)
    @Test
    public void rejectContractTest() throws Exception {
        Contract rejectedContract = new ContractBuilder().withStatus(Status.REJECTED).build(0L);
        when(contractService.rejectContract("0")).thenReturn(rejectedContract);

        mockMvc.perform(post("/contract/rejectContract/{contractId}", 0L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(rejectedContract)));
    }

    @WithMockUser(roles = admin)
    @Test
    public void revokeContractEmptyTest() throws Exception {

        when(contractService.revokeContract("0")).thenThrow(new EmptyTargetException(""));

        mockMvc.perform(post("/contract/revokeContract/{contractId}", 0L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = admin)
    @Test
    public void revokeContractRevokedTest() throws Exception {
        when(contractService.revokeContract("0")).thenThrow(new InvalidStatusException(""));

        mockMvc.perform(post("/contract/revokeContract/{contractId}", 0L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = admin)
    @Test
    public void getDescriptionEmptyTest() throws Exception {
        when(contractService.getTaDescription("tdevalckk", "CSE2025"))
                .thenThrow(new EmptyTargetException(""));

        mockMvc.perform(get("/contract/getTaDescription/{username}/{courseCode}",
                "tdevalckk", "CSE2025")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = admin)
    @Test
    public void getDescriptionNullTest() throws Exception {
        when(contractService.getTaDescription("tdevalckc", "CSE20212"))
                .thenThrow(new NullPointerException(""));

        mockMvc.perform(get("/contract/getTaDescription/{username}/{courseCode}",
                "tdevalckc", "CSE20212")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = admin)
    @Test
    public void getCourseDescriptionNullTest() throws Exception {
        Contract contract = new ContractBuilder().withExperience(null).build(0L);
        when(contractService.getCourseDescriptions(contract.getCourseCode()))
                .thenThrow(new NullPointerException(""));

        mockMvc.perform(get("/contract/getCourseDescriptions/{courseCode}",
                contract.getCourseCode())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = admin)
    @Test
    public void getCourseDescriptionTest() throws Exception {
        Contract c1 = new ContractBuilder().withExperience("I had fun").build(0L);
        Contract c4 = new ContractBuilder().withExperience("I earned a lot of money").build(0L);
        Contract c5 = new ContractBuilder().withExperience("It fcking sucked").build(0L);

        String courseCode = c1.getCourseCode();

        List<String> expected = List.of(c1.getTaDescription(),
                c4.getTaDescription(),
                c5.getTaDescription());

        when(contractService.getCourseDescriptions(courseCode)).thenReturn(expected);

        mockMvc.perform(get("/contract/getCourseDescriptions/{courseCode}", courseCode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(expected)));
    }

    @WithMockUser(roles = admin)
    @Test
    public void getCourseDescriptionsEmptyTest() throws Exception {
        when(contractService.getCourseDescriptions("CSE20218"))
                .thenThrow(new EmptyTargetException(""));

        mockMvc.perform(get("/contract/getCourseDescriptions/{courseCode}", "CSE20218")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = admin)
    @Test
    public void getContractTest() throws Exception {
        Contract contract = new ContractBuilder().build(25L);

        when(contractService.getContract("25")).thenReturn(contract);

        mockMvc.perform(get("/contract/getContract/{contractId}", 25L)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(contract)));
    }

    @WithMockUser(roles = admin)
    @Test
    public void getContractEmptyTest() throws Exception {

        when(contractService.getContract("25")).thenThrow(new EmptyTargetException(""));

        mockMvc.perform(get("/contract/getContract/{contractId}", 25L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = admin)
    @Test
    public void createContractNullTest() throws Exception {

        mockMvc.perform(post("/contract/createContract")
                .content((byte[]) null)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @WithMockUser(roles = admin)
    @Test
    public void createContractTest() throws Exception {
        ContractInfo contractInfo = new ContractInfoBuilder().build();

        when(contractService.createContract(contractInfo)).thenReturn(contractInfo);

        mockMvc.perform(post("/contract/createContract")
                .content(asJsonString(contractInfo))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(contractInfo)));
    }

    @WithMockUser(roles = admin)
    @Test
    public void createContractDuplicateTest() throws Exception {
        ContractInfo contractInfo = new ContractInfoBuilder().build();
        when(contractService.createContract(contractInfo))
                .thenThrow(new DuplicateObjectException(""));

        mockMvc.perform(post("/contract/createContract")
                .content(asJsonString(contractInfo))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = admin)
    @Test
    public void signContractEmptyTest() throws Exception {
        when(contractService.signContract("0")).thenThrow(new EmptyTargetException(""));

        mockMvc.perform(post("/contract/signContract/{contractId}", 0L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = admin)
    @Test
    public void signContractSignedTest() throws Exception {
        when(contractService.signContract("0")).thenThrow(new InvalidStatusException(""));

        mockMvc.perform(post("/contract/signContract/{contractId}", 0L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = admin)
    @Test
    public void signContractTest() throws Exception {
        Contract approvedContract = new ContractBuilder().withStatus(Status.APPROVED).build(0L);

        when(contractService.signContract("0")).thenReturn(approvedContract);

        mockMvc.perform(post("/contract/signContract/{contractId}", 0L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(approvedContract)));
    }

    @WithMockUser(roles = admin)
    @Test
    public void getDescriptionTest() throws Exception {
        Contract contract = new ContractBuilder().withExperience("I hated it").build(0L);
        when(contractService.getTaDescription(contract.getUsername(), contract.getCourseCode()))
                .thenReturn("I hated it");

        mockMvc.perform(get("/contract/getTaDescription/{username}/{courseCode}",
                contract.getUsername(), contract.getCourseCode())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("I hated it"));
    }

    @WithMockUser(roles = admin)
    @Test
    public void writeDescriptionEmptyTest() throws Exception {
        String experience = "test";
        when(contractService.writeTaDescription("tdevalck", "CSE2021", asJsonString(experience)))
                .thenThrow(new EmptyTargetException(""));

        mockMvc.perform(post("/contract/writeTaDescription/{username}/{courseCode}",
                "tdevalck", "CSE2021")
                .content(asJsonString(experience))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = admin)
    @Test
    public void writeDescriptionNullTest() throws Exception {
        Contract contract = new ContractBuilder().build(0L);

        when(contractService
                .writeTaDescription(contract.getUsername(),
                        contract.getCourseCode(),
                        asJsonString(null)))
                .thenThrow(new NullPointerException(""));

        mockMvc.perform(post("/contract/writeTaDescription/{username}/{courseCode}",
                contract.getUsername(), contract.getCourseCode())
                .content(asJsonString(null))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = admin)
    @Test
    public void writeDescriptionTest() throws Exception {
        Contract contract = new ContractBuilder().build(0L);
        String experience = "I had a lot of fun TA'ing for this course. 100% recommend";
        Contract newContract = new ContractBuilder().withExperience(experience).build(0L);
        when(contractService.writeTaDescription(contract.getUsername(),
                contract.getCourseCode(),
                asJsonString(experience)))
                .thenReturn(newContract);

        mockMvc.perform(post("/contract/writeTaDescription/{username}/{courseCode}",
                contract.getUsername(), contract.getCourseCode())
                .content(asJsonString(experience))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(newContract)));
    }


    @WithMockUser(roles = admin)
    @Test
    public void updateContractNullTest() throws Exception {
        mockMvc.perform(put("/contract/updateContract")
                .content(asJsonString(null))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = admin)
    @Test
    public void updateContractTest() throws Exception {
        ContractInfo update = new ContractInfoBuilder().withExperience("This is a change").build();

        when(contractService.updateContract(update)).thenReturn(update);

        mockMvc.perform(put("/contract/updateContract")
                .content(asJsonString(update))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(update)));
    }

    @WithMockUser(roles = admin)
    @Test
    public void deleteContractEmptyTest() throws Exception {
        when(contractService.deleteContract("0")).thenThrow(new EmptyTargetException(""));

        mockMvc.perform(delete("/contract/deleteContract/{contractId}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = admin)
    @Test
    public void deleteContractTest() throws Exception {
        Contract contract = new ContractBuilder().build(0L);
        when(contractService.deleteContract("0")).thenReturn(contract);

        mockMvc.perform(delete("/contract/deleteContract/{contractId}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(contract)));
    }

    @WithMockUser(roles = admin)
    @Test
    public void getBadExperiencesTest() throws Exception {
        when(contractService.getExperiences("bbobb"))
                .thenThrow(new EmptyTargetException(""));

        mockMvc.perform(get("/contract/getExperiences/{userName}", "bbobb")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = admin)
    @Test
    public void obtainNumberOfTas() throws Exception {
        when(contractService.numberOfTasCourse("CSE2115"))
                .thenReturn(2);

        mockMvc.perform(get("/contract/countTa/{courseCode}", "CSE2115")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().json("2"));
    }

    @WithMockUser(roles = admin)
    @Test
    public void getExperiencesTest() throws Exception {
        List<String> experience = new ArrayList<>();
        when(contractService.getExperiences("bbob"))
                .thenReturn(experience);

        mockMvc.perform(get("/contract/getExperiences/{userName}", "bbob")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(experience)));
    }
}
