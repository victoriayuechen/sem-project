package nl.tudelft.sem.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import nl.tudelft.sem.WorkloadInfo;
import nl.tudelft.sem.entities.Workload;
import nl.tudelft.sem.exceptions.EmptyTargetException;
import nl.tudelft.sem.service.WorkloadService;
import nl.tudelft.sem.util.WorkloadBuilder;
import nl.tudelft.sem.util.WorkloadInfoBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(WorkloadController.class)
public class WorkloadControllerTest {
    @MockBean
    private transient WorkloadService workloadService;

    @Autowired
    private transient MockMvc mockMvc;
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJz"
            + "dWIiOiJhbm5pYmFsZSIsImV4cCI6MTY0MDMw"
            + "NjkyMywiaWF0IjoxNjQwMjcwOTIzfQ.Ab3qsQdzo"
            + "U8viZwWtnFf9NqIG9GDsSssTxrjyXj_8Dg";
    private static final String AUTHORIZATION = "Authorization";
    private final transient String admin = "ADMIN";

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
    public void overviewEmptyTest() throws Exception {
        String courseCode = "CSE2020";
        when(workloadService.courseOverview(courseCode)).thenThrow(new EmptyTargetException(""));

        mockMvc.perform(get("/workload/overview/allAverage/{courseCode}", courseCode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = admin)
    @Test
    public void obtainWorkloadsTest() throws Exception {
        when(workloadService.getWorkloadHoursPerCourse("CSE2115"))
                .thenReturn(List.of(10, 20, 42));

        mockMvc.perform(get("/workload/workload-hours/{courseCode}", "CSE2115")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[10, 20, 42]"));
    }

    @WithMockUser(roles = admin)
    @Test
    public void viewTaTest() throws Exception {
        Workload w1 = new WorkloadBuilder().withName("jan").build(0L);
        Workload w2 = new WorkloadBuilder().withName("klaas").build(1L);
        Workload w3 = new WorkloadBuilder().withName("piet").build(2L);
        Workload w4 = new WorkloadBuilder().withName("henk").build(3L);
        Workload w5 = new WorkloadBuilder().withName("gert").build(4L);
        String courseCode = w1.getCourseCode();

        List<String> expected = List.of(w1.getUsername(),
                w2.getUsername(),
                w3.getUsername(),
                w4.getUsername(),
                w5.getUsername());

        when(workloadService.viewTas(courseCode)).thenReturn(expected);

        mockMvc.perform(get("/workload/overview/viewTAs/{courseCode}", courseCode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(expected)));
    }

    @WithMockUser(roles = admin)
    @Test
    public void overviewTest() throws Exception {
        Workload w1 = new WorkloadBuilder().withHours(10).build(0L);
        String courseCode = w1.getCourseCode();

        String expectedString1 = "Name: rmihalachiuta; Hours/Week: 4.0";
        String expectedString2 = "Name: tdevalck; Hours/Week: 2.4";

        when(workloadService.courseOverview(courseCode))
                .thenReturn(List.of(expectedString1, expectedString2));

        mockMvc.perform(get("/workload/overview/allAverage/{courseCode}", courseCode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(List.of(expectedString1, expectedString2))));
    }

    @WithMockUser(roles = admin)
    @Test
    public void rejectWorkloadExceptionTest() throws Exception {

        when(workloadService.rejectHours("0", TOKEN))
                .thenThrow(new IOException());

        mockMvc.perform(post("/workload/reject/{workloadId}", 0L)
                .header(AUTHORIZATION, TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

    }

    @WithMockUser(roles = admin)
    @Test
    public void averageTaTest() throws Exception {
        Workload w1 = new WorkloadBuilder().withHours(10).build(0L);
        String courseCode = w1.getCourseCode();

        float expected = 2.4f;

        when(workloadService.averageTa(courseCode, "tdevalck")).thenReturn(expected);

        mockMvc.perform(get("/workload/overview/average/{courseCode}/{username}",
                courseCode, "tdevalck")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(expected)));
    }

    @WithMockUser(roles = admin)
    @Test
    public void viewTaEmptyTest() throws Exception {
        String courseCode = "CSE2021";
        when(workloadService.viewTas(courseCode)).thenThrow(new EmptyTargetException(""));

        mockMvc.perform(get("/workload/overview/viewTAs/{courseCode}", courseCode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = admin)
    @Test
    public void viewTaDuplicateTest() throws Exception {
        Workload w1 = new WorkloadBuilder().build(0L);
        String courseCode = w1.getCourseCode();

        when(workloadService.viewTas(courseCode)).thenReturn(List.of(w1.getUsername()));

        mockMvc.perform(get("/workload/overview/viewTAs/{courseCode}", courseCode)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(List.of(w1.getUsername()))));
    }

    @WithMockUser(roles = admin)
    @Test
    public void updateWorkloadNullTest() throws Exception {
        mockMvc.perform(put("/workload/updateWorkload")
                .content(asJsonString(null))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = admin)
    @Test
    public void updateWorkloadEmptyTest() throws Exception {
        WorkloadInfo workloadInfo = new WorkloadInfoBuilder().build();
        when(workloadService.updateWorkload(workloadInfo)).thenThrow(new EmptyTargetException(""));

        mockMvc.perform(put("/workload/updateWorkload")
                .content(asJsonString(workloadInfo))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = admin)
    @Test
    public void updateWorkloadTest() throws Exception {
        WorkloadInfo update = new WorkloadInfoBuilder().withHours(16).build();

        when(workloadService.updateWorkload(update)).thenReturn(update);

        mockMvc.perform(put("/workload/updateWorkload")
                .content(asJsonString(update))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(update)));
    }

    @WithMockUser(roles = admin)
    @Test
    public void deleteWorkloadEmptyTest() throws Exception {

        when(workloadService.deleteWorkload("0")).thenThrow(new EmptyTargetException(""));

        mockMvc.perform(delete("/workload/deleteWorkload/{stringId}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = admin)
    @Test
    public void deleteWorkloadTest() throws Exception {
        Workload workload = new WorkloadBuilder().build(0L);

        when(workloadService.deleteWorkload("0")).thenReturn(workload);

        mockMvc.perform(delete("/workload/deleteWorkload/{stringId}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(workload)));
    }

    @WithMockUser(roles = admin)
    @Test
    public void rejectWorkloadTest() throws Exception {
        Workload w1 = new WorkloadBuilder()
                .withHours(30)
                .build(0L);

        when(workloadService.rejectHours("0", TOKEN))
                .thenReturn(w1);

        mockMvc.perform(post("/workload/reject/{workloadId}", 0L)
                .header(AUTHORIZATION, TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(w1)));
    }

    @WithMockUser(roles = admin)
    @Test
    public void rejectWorkloadEmptyWorkloadTest() throws Exception {

        when(workloadService.rejectHours("0", TOKEN))
                .thenThrow(new EmptyTargetException("Not found."));

        mockMvc.perform(post("/workload/reject/{workloadId}", 0L)
                .header(AUTHORIZATION, TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
