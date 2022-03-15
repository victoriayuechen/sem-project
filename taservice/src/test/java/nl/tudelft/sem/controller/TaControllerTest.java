package nl.tudelft.sem.controller;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import nl.tudelft.sem.AverageWorkload;
import nl.tudelft.sem.SelectInfo;
import nl.tudelft.sem.Status;
import nl.tudelft.sem.entities.Workload;
import nl.tudelft.sem.exceptions.AddRoleFailureException;
import nl.tudelft.sem.exceptions.EmptyTargetException;
import nl.tudelft.sem.service.ContractService;
import nl.tudelft.sem.service.ReviewService;
import nl.tudelft.sem.service.TaService;
import nl.tudelft.sem.service.WorkloadService;
import nl.tudelft.sem.util.WorkloadBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TaController.class)
@SuppressWarnings({"PMD.DataflowAnomalyAnalysis", "PMD.AvoidDuplicateLiterals"})
public class TaControllerTest {
    @MockBean
    private transient TaService taService;
    @MockBean
    private transient ContractService contractService;
    @MockBean
    private transient WorkloadService workloadService;
    @MockBean
    private transient ReviewService reviewService;

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

    @WithMockUser(roles = "ADMIN")
    @Test
    public void approveWorkloadTest() throws Exception {
        Workload w1 = new WorkloadBuilder()
                .withHours(20)
                .build(0L);

        when(taService.checkHours("0", TOKEN))
                .thenReturn(w1);

        mockMvc.perform(post("/ta/workload/validate/{workloadId}", 0L)
                        .header(AUTHORIZATION, TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(w1)));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void validateWorkloadEmptyWorkloadTest() throws Exception {

        when(taService.checkHours("0", TOKEN))
                .thenThrow(new EmptyTargetException("Not found."));

        mockMvc.perform(post("/ta/workload/validate/{workloadId}", 0L)
                        .header(AUTHORIZATION, TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void validateWorkloadExceptionTest() throws Exception {

        when(taService.checkHours("0", TOKEN))
                .thenThrow(new IOException());

        mockMvc.perform(post("/ta/workload/validate/{workloadId}", 0L)
                        .header(AUTHORIZATION, TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void validateWorkloadInterruptedTest() throws Exception {

        when(taService.checkHours("0", TOKEN))
                .thenThrow(new InterruptedException());

        mockMvc.perform(post("/ta/workload/validate/{workloadId}", 0L)
                        .header(AUTHORIZATION, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void declareHoursSuccessTest() throws Exception {
        AverageWorkload sentWorkload = new AverageWorkload();
        sentWorkload.setAverageHours(10);
        sentWorkload.setCourseCode("CSE2115");
        sentWorkload.setUsername("annibale");

        Workload workload = new WorkloadBuilder()
                .withCourse("CSE2115")
                .withHours(10)
                .withStatus(Status.PENDING)
                .withName("annibale").build(0L);

        when(taService.declareHoursWorked(sentWorkload))
                .thenReturn(workload);

        mockMvc.perform(post("/ta/declareHours")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(sentWorkload)))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(workload)));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void noContractWithWorkloadTest() throws Exception {
        AverageWorkload sentWorkload = new AverageWorkload();
        sentWorkload.setAverageHours(10);
        sentWorkload.setCourseCode("Chocolate Factory");
        sentWorkload.setUsername("Mr. Wonka");

        when(taService.declareHoursWorked(sentWorkload))
            .thenThrow(EmptyTargetException.class);

        mockMvc.perform(post("/ta/declareHours")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(sentWorkload)))
            .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void averageHoursTest() throws Exception {
        AverageWorkload workload = new AverageWorkload();
        workload.setAverageHours(10);
        workload.setCourseCode("CSE2115");
        when(taService.getAverageHoursCourse(workload.getCourseCode(), TOKEN))
                .thenReturn(workload.getAverageHours());

        mockMvc.perform(get("/ta/workload/getAverage/" + workload.getCourseCode())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().json(Integer.toString(workload.getAverageHours())));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void noAverageHoursTest() throws Exception {
        AverageWorkload workload = new AverageWorkload();
        workload.setAverageHours(10);
        workload.setCourseCode("CSE2115");
        when(taService.getAverageHoursCourse(workload.getCourseCode(), TOKEN))
                .thenThrow(InterruptedException.class);

        mockMvc.perform(get("/ta/workload/getAverage/" + workload.getCourseCode())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", TOKEN))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void saveTaToDbSuccessTest()
        throws Exception {
        SelectInfo selectInfo = new SelectInfo();
        selectInfo.setUsername("Willy Wonka");
        selectInfo.setStatus(Status.APPROVED);
        when(taService.saveTaToDatabase(selectInfo.getUsername(), TOKEN))
            .thenReturn(true);

        mockMvc.perform(post("/ta/save-ta")
                .header("Authorization", TOKEN)
                .content(asJsonString(selectInfo))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("true"));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void saveTaToDbInterruptedTest() throws Exception {
        SelectInfo selectInfo = new SelectInfo();
        selectInfo.setUsername("Willy Wonka");
        selectInfo.setStatus(Status.APPROVED);
        when(taService.saveTaToDatabase("Willy Wonka", TOKEN))
            .thenThrow(InterruptedException.class);

        mockMvc.perform(post("/ta/save-ta")
                .header("Authorization", TOKEN)
                .content(asJsonString(selectInfo))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("false"));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void saveTaToDbIoTest() throws Exception {
        SelectInfo selectInfo = new SelectInfo();
        selectInfo.setUsername("Willy Wonka");
        selectInfo.setStatus(Status.APPROVED);
        when(taService.saveTaToDatabase("Willy Wonka", TOKEN))
            .thenThrow(IOException.class);

        mockMvc.perform(post("/ta/save-ta")
                .header("Authorization", TOKEN)
                .content(asJsonString(selectInfo))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("false"));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void saveTaAddRoleErrorTest() throws Exception {
        SelectInfo selectInfo = new SelectInfo();
        selectInfo.setUsername("Willy Wonka");
        selectInfo.setStatus(Status.APPROVED);
        when(taService.saveTaToDatabase("Willy Wonka", TOKEN))
            .thenThrow(AddRoleFailureException.class);

        mockMvc.perform(post("/ta/save-ta")
                .header("Authorization", TOKEN)
                .content(asJsonString(selectInfo))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("false"));
    }
}
