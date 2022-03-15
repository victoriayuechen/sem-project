package nl.tudelft.sem.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import nl.tudelft.sem.ApplyInfo;
import nl.tudelft.sem.Criteria;
import nl.tudelft.sem.ErrorString;
import nl.tudelft.sem.SelectInfo;
import nl.tudelft.sem.Status;
import nl.tudelft.sem.entities.Application;
import nl.tudelft.sem.exceptions.EmptyTargetElementException;
import nl.tudelft.sem.exceptions.InvalidApplicationException;
import nl.tudelft.sem.service.ApplicationDataService;
import nl.tudelft.sem.service.ApplicationService;
import nl.tudelft.sem.service.FilterService;
import nl.tudelft.sem.service.SelectApplicantService;
import nl.tudelft.sem.util.ApplicationBuilder;
import nl.tudelft.sem.util.FilterParameters;
import nl.tudelft.sem.util.GradeRecommendation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ApplicationController.class)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class ApplicationControllerTest {
    @MockBean
    private transient ApplicationService applicationService;
    @MockBean
    private transient ApplicationDataService applicationDataService;
    @MockBean
    private transient SelectApplicantService selectApplicantService;
    @MockBean
    private transient FilterService filterService;
    @Autowired
    private transient MockMvc mockMvc;
    private transient ApplyInfo info;
    private transient SelectInfo selectInfo;
    private transient SelectInfo rejectInfo;
    private transient SelectInfo withdrawInfo;
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJz"
        + "dWIiOiJhbm5pYmFsZSIsImV4cCI6MTY0MDMw"
        + "NjkyMywiaWF0IjoxNjQwMjcwOTIzfQ.Ab3qsQdzo"
        + "U8viZwWtnFf9NqIG9GDsSssTxrjyXj_8Dg";
    private static final String AUTHORIZATION = "Authorization";

    /** Initializes multiple instances of info at before each test.
     */
    @BeforeEach
    public void setup() {
        info = new ApplyInfo();
        info.setUsername("Victoria");
        info.setCourseCode("CSE2115");

        selectInfo = new SelectInfo();
        selectInfo.setUsername("Willy Wonka");
        selectInfo.setCourseCode("CSE4242");
        selectInfo.setStatus(Status.APPROVED);

        rejectInfo = new SelectInfo();
        rejectInfo.setUsername("Charlie");
        rejectInfo.setCourseCode("CSE4242");
        rejectInfo.setStatus(Status.REJECTED);

        withdrawInfo = new SelectInfo();
        withdrawInfo.setUsername("Oompa Loompa");
        withdrawInfo.setCourseCode("CSE4242");
        withdrawInfo.setStatus(Status.REVOKED);
    }

    /** Generic JSON parser.
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
    public void successfulSavedApplicationTest() throws Exception {
        Application app = new ApplicationBuilder()
            .withGrade(10.0)
            .build(0L);

        when(applicationService.createApplication(TOKEN, info.getCourseCode(), info.getUsername()))
            .thenReturn(app);

        mockMvc.perform(post("/application/applyForPosition")
                .header(AUTHORIZATION, TOKEN)
            .content(ApplicationControllerTest.asJsonString(info))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(ApplicationControllerTest.asJsonString(app)));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void exceedThreeApplicationsSaveApplicationTest() throws Exception {
        when(applicationService.createApplication(TOKEN, info.getCourseCode(), info.getUsername()))
            .thenThrow(new InvalidApplicationException("Too many applications."));

        mockMvc.perform(post("/application/applyForPosition")
                .header(AUTHORIZATION, TOKEN)
            .content(ApplicationControllerTest.asJsonString(info))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void appliedToSamePositionSaveApplicationTest() throws Exception {
        when(applicationService.createApplication(TOKEN, info.getCourseCode(), info.getUsername()))
            .thenThrow(new InvalidApplicationException("Cannot apply more than three times"));

        mockMvc.perform(post("/application/applyForPosition")
                .header(AUTHORIZATION, TOKEN)
                .content(ApplicationControllerTest.asJsonString(info))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void internalServerErrorCreateApplicationTest() throws Exception {
        when(applicationService.createApplication(TOKEN, info.getCourseCode(), info.getUsername()))
            .thenThrow(new IOException());

        mockMvc.perform(post("/application/applyForPosition")
                .header(AUTHORIZATION, TOKEN)
                .content(ApplicationControllerTest.asJsonString(info))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is5xxServerError());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void insufficientGradeSaveApplicationTest() throws Exception {
        when(applicationService.createApplication(TOKEN, info.getCourseCode(), info.getUsername()))
            .thenThrow(new InvalidApplicationException("Insufficient grade."));

        mockMvc.perform(post("/application/applyForPosition")
                .header(AUTHORIZATION, TOKEN)
                .content(ApplicationControllerTest.asJsonString(info))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void retrieveSuccessApplicationsTest() throws Exception {
        Application app3 = new ApplicationBuilder()
            .withStatus(Status.PENDING)
            .build(2L);

        when(applicationService.obtainApplicationsByCourse(info.getCourseCode()))
            .thenReturn(List.of(app3));

        mockMvc.perform(get("/application/getApplications/" + info.getCourseCode())
                .header(AUTHORIZATION, TOKEN)
                .content(asJsonString(info))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(content().json("[" + asJsonString(app3) + "]"));

    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void retrieveEmptyApplicationsTest() throws Exception {
        when(applicationService.obtainApplicationsByCourse(info.getCourseCode()))
            .thenThrow(new EmptyTargetElementException("No open applications."));

        mockMvc.perform(get("/application/getApplications/" + info.getCourseCode())
                .header(AUTHORIZATION, TOKEN)
                .content(asJsonString(info))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void selectTaSuccessTest() throws Exception {
        Application app = new ApplicationBuilder()
            .withName(selectInfo.getUsername())
            .withCourse(selectInfo.getCourseCode())
            .withStatus(Status.PENDING)
            .build(0L);

        when(selectApplicantService.selectApplicant(selectInfo, TOKEN)).thenReturn(app);

        app.setStatus(Status.APPROVED);
        mockMvc.perform(post("/application/selectTA")
                .header(AUTHORIZATION, TOKEN)
                .content(asJsonString(selectInfo))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(asJsonString(app)))
            .andDo(print());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void selectTaNotOpenForRecruitment() throws Exception {
        when(selectApplicantService.selectApplicant(selectInfo, TOKEN))
            .thenThrow(new InvalidApplicationException("Not open for recruitment."));

        mockMvc.perform(post("/application/selectTA")
                .header(AUTHORIZATION, TOKEN)
            .content(asJsonString(selectInfo))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void selectTaNoSuchApplicationTest() throws Exception {
        when(selectApplicantService.selectApplicant(selectInfo, TOKEN))
            .thenThrow(new EmptyTargetElementException("No such application."));

        mockMvc.perform(post("/application/selectTA")
                .header(AUTHORIZATION, TOKEN)
                .content(asJsonString(selectInfo))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void selectTaCommunicationErrorIoTest() throws Exception {
        when(selectApplicantService.selectApplicant(selectInfo, TOKEN))
            .thenThrow(new IOException());

        mockMvc.perform(post("/application/selectTA")
                .header(AUTHORIZATION, TOKEN)
                .content(asJsonString(selectInfo))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is5xxServerError());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void selectTaCommunicationErrorInterruptedTest() throws Exception {
        when(selectApplicantService.selectApplicant(selectInfo, TOKEN))
            .thenThrow(new InterruptedException());

        mockMvc.perform(post("/application/selectTA")
                .header(AUTHORIZATION, TOKEN)
                .content(asJsonString(selectInfo))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is5xxServerError());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void rejectApplicantSuccessTest() throws Exception {
        Application app = new ApplicationBuilder()
            .withName(rejectInfo.getUsername())
            .withCourse(rejectInfo.getCourseCode())
            .withStatus(Status.PENDING)
            .build(0L);

        when(applicationService.rejectApplicant(rejectInfo, TOKEN))
            .thenReturn(app);

        mockMvc.perform(post("/application/rejectApplicant")
                .header(AUTHORIZATION, TOKEN)
                .content(asJsonString(rejectInfo))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(asJsonString(app)));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void rejectApplicantNoSuchApplicationTest() throws Exception {
        when(applicationService.rejectApplicant(rejectInfo, TOKEN))
            .thenThrow(EmptyTargetElementException.class);

        mockMvc.perform(post("/application/rejectApplicant")
                .header(AUTHORIZATION, TOKEN)
                .content(asJsonString(rejectInfo))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }


    @WithMockUser(roles = "ADMIN")
    @Test
    public void rejectApplicantInterruptedTest() throws Exception {
        when(applicationService.rejectApplicant(rejectInfo, TOKEN))
            .thenThrow(InterruptedException.class);

        mockMvc.perform(post("/application/rejectApplicant")
                .header(AUTHORIZATION, TOKEN)
                .content(asJsonString(rejectInfo))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is5xxServerError());
    }


    @WithMockUser(roles = "ADMIN")
    @Test
    public void rejectApplicantIoErrorTest() throws Exception {
        when(applicationService.rejectApplicant(rejectInfo, TOKEN))
            .thenThrow(IOException.class);

        mockMvc.perform(post("/application/rejectApplicant")
                .header(AUTHORIZATION, TOKEN)
                .content(asJsonString(rejectInfo))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is5xxServerError());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void gradeOverviewOfCandidate() throws Exception {
        List<String> testExperiences = Collections.emptyList();
        HashMap<String, Object> testHmap = new HashMap<String, Object>();
        testHmap.put("grade", 8.0);
        testHmap.put("experience", testExperiences);

        when(applicationDataService.obtainApplicationData(0L, TOKEN))
                .thenReturn(testHmap);

        mockMvc.perform(get("/application/getApplicationData/{applicationId}", 0L)
                .content(ApplicationControllerTest.asJsonString(info))
                .header(AUTHORIZATION, TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(testHmap)));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void experiencesOverviewOfCandidate() throws Exception {
        List<String> testExperiences = new ArrayList<>();
        testExperiences.add("CSE2115");
        testExperiences.add("CSE4224");
        HashMap<String, Object> testHmap = new HashMap<String, Object>();
        testHmap.put("grade", 8.0);
        testHmap.put("experience", testExperiences);

        when(applicationDataService.obtainApplicationData(0L, TOKEN))
                .thenReturn(testHmap);

        mockMvc.perform(get("/application/getApplicationData/{applicationId}", 0L)
                        .content(ApplicationControllerTest.asJsonString(info))
                        .header(AUTHORIZATION, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(testHmap)));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void withdrawApplicationSuccessTest() throws Exception {
        Application app = new ApplicationBuilder()
                .withName(withdrawInfo.getUsername())
                .withCourse(withdrawInfo.getCourseCode())
                .withStatus(Status.PENDING)
                .build(0L);

        when(applicationService.withdrawApp(withdrawInfo, TOKEN))
                .thenReturn(app);

        mockMvc.perform(post("/application/withdraw")
                        .content(asJsonString(withdrawInfo))
                .header(AUTHORIZATION, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(app)));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void withdrawApplicationNoSuchApplicationTest() throws Exception {

        when(applicationService.withdrawApp(withdrawInfo, TOKEN))
                .thenThrow(new InvalidApplicationException("Not found."));

        mockMvc.perform(post("/application/withdraw")
                        .content(asJsonString(withdrawInfo))
                        .header(AUTHORIZATION, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void withdrawApplicationInternalErrorTest() throws Exception {

        when(applicationService.withdrawApp(withdrawInfo, TOKEN))
                .thenThrow(new IOException());

        mockMvc.perform(post("/application/withdraw")
                        .content(asJsonString(withdrawInfo))
                        .header(AUTHORIZATION, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void withdrawApplicationInterruptedTest() throws Exception {

        when(applicationService.withdrawApp(withdrawInfo, TOKEN))
                .thenThrow(new InterruptedException());

        mockMvc.perform(post("/application/withdraw")
                        .content(asJsonString(withdrawInfo))
                        .header(AUTHORIZATION, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void withdrawApplicationAlreadyApprovedTest() throws Exception {

        when(applicationService.withdrawApp(withdrawInfo, TOKEN))
                .thenThrow(new InvalidApplicationException("Application already approved."));

        mockMvc.perform(post("/application/withdraw")
                        .content(asJsonString(withdrawInfo))
                        .header(AUTHORIZATION, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void recommendationTest() throws Exception {
        Application app1 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName("Bob")
                .build(0L);
        Application app2 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName("Jack")
                .build(1L);
        Application app3 = new ApplicationBuilder()
                .withStatus(Status.PENDING)
                .withName("Alice")
                .build(2L);

        List<Application> applications = List.of(app1, app2, app3);
        List<Criteria> criterias = new ArrayList<>();
        criterias.add(Criteria.EXPERIENCE);

        when(applicationDataService.recommendApplicants("CSE2115", criterias, TOKEN))
                .thenReturn(applications);
        mockMvc.perform(get("/application/recommend/{courseCode}",
                "CSE2115")
                        .header(AUTHORIZATION, TOKEN)
                        .content(asJsonString(criterias))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, TOKEN)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(applications)));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void autoRejectNoErrors() throws Exception {
        mockMvc.perform(get("/application/autoReject/{courseCode}",
                        "CSE2115")
                        .header(AUTHORIZATION, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Applications successfully rejected."));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void autoRejectNumberFormatException() throws Exception {
        doThrow(new NumberFormatException()).when(filterService)
                .autoReject("CSE2115", new FilterParameters(null, null,
                        null, null), TOKEN);
        mockMvc.perform(get("/application/autoReject/{courseCode}",
                        "CSE2115")
                         .header(AUTHORIZATION, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(asJsonString(
                        new ErrorString("Wrong number format in parameters."))));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void autoRejectEmptyTargetElementException() throws Exception {
        doThrow(new EmptyTargetElementException("No open applications with given course code"))
                .when(filterService).autoReject("CSE2115", new FilterParameters(null, null,
                        null, null), TOKEN);
        mockMvc.perform(get("/application/autoReject/{courseCode}",
                        "CSE2115")
                        .header(AUTHORIZATION, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(asJsonString(
                        new ErrorString("No open applications with given course code"))));
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @WithMockUser(roles = "ADMIN")
    @Test
    public void autoRejectIoException() throws Exception {
        doThrow(new IOException("Something went wrong with sending a notification."))
                .when(filterService).autoReject("CSE2115", new FilterParameters(null, null,
                        null, null), TOKEN);
        mockMvc.perform(get("/application/autoReject/{courseCode}",
                        "CSE2115")
                        .header(AUTHORIZATION, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(asJsonString(
                        new ErrorString("Something went wrong when sending the notifications."))));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void autoRejectInterruptedException() throws Exception {
        doThrow(new InterruptedException("Something went wrong with sending a notification."))
                .when(filterService).autoReject("CSE2115", new FilterParameters(null, null,
                        null, null), TOKEN);
        mockMvc.perform(get("/application/autoReject/{courseCode}",
                        "CSE2115")
                        .header(AUTHORIZATION, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(asJsonString(
                        new ErrorString("Something went wrong when sending the notifications."))));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void applyAlgorithmNoException() throws Exception {
        GradeRecommendation recommendation = mock(GradeRecommendation.class);
        when(filterService.applyAlgorithm("CSE2115",
                new FilterParameters(null, null, null, null), TOKEN))
                .thenReturn(recommendation);
        when(recommendation.recommend(TOKEN)).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/application/applyAlgorithm/{courseCode}",
                        "CSE2115")
                        .header(AUTHORIZATION, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(
                        new ArrayList<>())));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void applyAlgorithmNumberFormatException() throws Exception {
        doThrow(new NumberFormatException()).when(filterService)
                .applyAlgorithm("CSE2115", new FilterParameters(null, null, null, null), TOKEN);
        mockMvc.perform(get("/application/applyAlgorithm/{courseCode}",
                        "CSE2115")
                        .header(AUTHORIZATION, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(asJsonString(
                        new ErrorString("Wrong number format in parameters."))));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void applyAlgorithmEmptyTargetElementException() throws Exception {
        doThrow(new EmptyTargetElementException("No open applications with given course code"))
                .when(filterService).applyAlgorithm("CSE2115",
                        new FilterParameters(null, null, null, null), TOKEN);

        mockMvc.perform(get("/application/applyAlgorithm/{courseCode}",
                        "CSE2115")
                        .header(AUTHORIZATION, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(asJsonString(
                        new ErrorString("No open applications with given course code"))));
    }
}