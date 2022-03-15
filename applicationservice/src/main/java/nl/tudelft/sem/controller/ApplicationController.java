package nl.tudelft.sem.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.ApplyInfo;
import nl.tudelft.sem.Criteria;
import nl.tudelft.sem.ErrorString;
import nl.tudelft.sem.SelectInfo;
import nl.tudelft.sem.entities.Application;
import nl.tudelft.sem.exceptions.EmptyTargetElementException;
import nl.tudelft.sem.exceptions.InvalidApplicationException;
import nl.tudelft.sem.service.ApplicationDataService;
import nl.tudelft.sem.service.ApplicationService;
import nl.tudelft.sem.service.FilterService;
import nl.tudelft.sem.service.SelectApplicantService;
import nl.tudelft.sem.util.FilterParameters;
import nl.tudelft.sem.util.Recommendation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/application")
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.DataflowAnomalyAnalysis"})
public class ApplicationController {
    @Autowired
    private transient ApplicationService applicationService;
    @Autowired
    private transient FilterService filterService;
    @Autowired
    private transient ApplicationDataService applicationDataService;
    @Autowired
    private transient SelectApplicantService selectApplicantService;
    private static final double PASS = 5.75;
    private static final int CAPACITY = 3;
    private static final String AUTHORIZATION = "Authorization";

    /**
     * Constructor that allows dependencies to be injected.
     *
     * @param applicationService application service for handling all related functionality.
     */
    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }


    /**
     * Creates an application for a TA position.
     *
     * @param request   The request that was made.
     * @param applyInfo The sent apply info DTO.
     * @return The application object made.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT', 'ROLE_ADMIN')")
    @PostMapping("/applyForPosition")
    public ResponseEntity<?> createApplication(HttpServletRequest request,
                                               @RequestBody ApplyInfo applyInfo) {
        String token = request.getHeader(AUTHORIZATION);

        try {
            Application application = applicationService
                    .createApplication(token, applyInfo.getCourseCode(),
                    applyInfo.getUsername());
            return ResponseEntity
                .ok()
                .body(application);
        } catch (IOException | InterruptedException e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorString("Error occurred in corresponding service."));
        } catch (InvalidApplicationException e) {
            return ResponseEntity
                .badRequest()
                .body(new ErrorString(e.getMessage()));
        }

    }

    /**
     * Obtains the list of application associated with a course, only displaying
     * applications that are open.
     *
     * @param request    The request object.
     * @param courseCode The course code of the course in question.
     * @return The list of applications for this course.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_LECTURER', 'ROLE_ADMIN')")
    @GetMapping("/getApplications/{courseCode}")
    public ResponseEntity<?> obtainApplications(HttpServletRequest request,
                                                @PathVariable String courseCode) {
        String token = request.getHeader(AUTHORIZATION);
        try {
            List<Application> applications = applicationService
                .obtainApplicationsByCourse(courseCode);
            return ResponseEntity
                .ok()
                .body(applications);

        } catch (EmptyTargetElementException e) {
            return ResponseEntity
                .badRequest()
                .body(new ErrorString(e.getMessage()));
        }
    }

    /**
     * Obtains the application data associated with an id.
     *
     * @param request       The request object.
     * @param applicationId The application id of the application in question.
     * @return The data for of the user from this application.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_LECTURER', 'ROLE_ADMIN')")
    @GetMapping("/getApplicationData/{applicationId}")
    public ResponseEntity<?> obtainApplicationData(HttpServletRequest request,
                                                   @PathVariable Long applicationId)
        throws IOException, InterruptedException {
        String token = request.getHeader(AUTHORIZATION);
        try {
            HashMap<String, Object> hmap = applicationDataService
                .obtainApplicationData(applicationId, token);
            return ResponseEntity
                .ok()
                .body(hmap);

        } catch (EmptyTargetElementException e) {
            return ResponseEntity
                .badRequest()
                .body(new ErrorString(e.getMessage()));
        }
    }

    /**
     * This endpoint adds a new selected TA to a course.
     *
     * @param request    The request of the client.
     * @param selectInfo The selection information sent along.
     * @return The response entity depending on whether the selection was successful.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_LECTURER', 'ROLE_ADMIN')")
    @PostMapping("/selectTA")
    public ResponseEntity<?> selectTa(HttpServletRequest request,
                                      @RequestBody SelectInfo selectInfo) {
        String token = request.getHeader(AUTHORIZATION);
        try {
            Application application = selectApplicantService.selectApplicant(selectInfo, token);
            return ResponseEntity
                .ok()
                .body(application);
        } catch (InvalidApplicationException | EmptyTargetElementException e) {
            return ResponseEntity
                .badRequest()
                .body(new ErrorString(e.getMessage()));
        } catch (IOException | InterruptedException e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorString("Error when communicating with other services."));
        }
    }

    /**
     * This method allows the client to reject an application for a TA position.
     *
     * @param request    The request of the client.
     * @param selectInfo The selection information associated with the application.
     * @return The response entity depending on whether the update was successful.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_LECTURER', 'ROLE_ADMIN')")
    @PostMapping("/rejectApplicant")
    public ResponseEntity<?> rejectTa(HttpServletRequest request,
                                      @RequestBody SelectInfo selectInfo) {
        String token = request.getHeader(AUTHORIZATION);
        try {
            Application application = applicationService.rejectApplicant(selectInfo, token);
            return ResponseEntity
                .ok()
                .body(application);
        } catch (EmptyTargetElementException e) {
            return ResponseEntity
                .badRequest()
                .body(new ErrorString(e.getMessage()));
        } catch (InterruptedException | IOException e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorString("Error when communicating with other services."));
        }
    }

    /**
     * This method allows the user to revoke their application,
     * if the application has not already been approved.
     * TODO: Refactor this
     *
     * @param request    the request of the client.
     * @param selectInfo The selection information associated with the application.
     * @return The response entity depending on whether the update was successful.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT', 'ROLE_ADMIN')")
    @PostMapping("/withdraw")
    public ResponseEntity<?> withdrawApplication(HttpServletRequest request,
                                                 @RequestBody SelectInfo selectInfo)
        throws IOException, InterruptedException {
        String token = request.getHeader(AUTHORIZATION);
        try {
            Application application = applicationService.withdrawApp(selectInfo, token);
            return ResponseEntity
                .ok()
                .body(application);
        } catch (InvalidApplicationException e) {
            return ResponseEntity
                .badRequest()
                .body(new ErrorString(e.getMessage()));
        } catch (IOException | InterruptedException e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorString("Error when communicating with other services."));
        }
    }

    /**
     * Recommends potential candidates.
     *
     * @param request    The HTTP request of the client.
     * @param courseCode The course code for which to run the recommendation algorithm.
     * @param criteria   A list of criteria to recommend on.
     * @return The list of recommendations.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_LECTURER', 'ROLE_ADMIN')")
    @GetMapping("/recommend/{courseCode}")
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public ResponseEntity<?> recommendApplicants(HttpServletRequest request,
                                                 @PathVariable String courseCode,
                                                 @RequestBody List<Criteria> criteria) {
        String token = request.getHeader(AUTHORIZATION);
        try {
            // String token = request.getHeader("Authorization");
            List<Application> applications =
                applicationDataService.recommendApplicants(courseCode, criteria, token);
            return ResponseEntity
                .ok()
                .body(applications);
        } catch (EmptyTargetElementException e) {
            return ResponseEntity
                .badRequest()
                .body(new ErrorString(e.getMessage()));
        } catch (InterruptedException | IOException e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorString("Error when communicating with other services."));
        }
    }

    /**
     * Shows the standard recommendation but filtered on certain parameters.
     *
     * @param request      The request of the client.
     * @param courseCode   Course code of the course.
     * @param minGrade     Minimum grade to filter on.
     * @param minRating    Minimum rating to filter on.
     * @param minAvgRating Minimum average rating to filter on.
     * @param minReqTa     Minimum amount of times a TA must have been a TA before on other
     *                     courses to filter on.
     * @return filtered list of applications.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_LECTURER', 'ROLE_ADMIN')")
    @GetMapping("/applyAlgorithm/{courseCode}")
    public ResponseEntity<?> applyAlgorithm(HttpServletRequest request,
                                            @PathVariable String courseCode,
                                            @RequestParam(required = false) String minGrade,
                                            @RequestParam(required = false) String minRating,
                                            @RequestParam(required = false) String minAvgRating,
                                            @RequestParam(required = false) String minReqTa) {
        String token = request.getHeader(AUTHORIZATION);
        try {
            Recommendation recommendation = filterService
                .applyAlgorithm(courseCode,
                        new FilterParameters(minGrade, minRating, minAvgRating, minReqTa), token);
            return ResponseEntity.ok(recommendation.recommend(token));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorString("Wrong number format in parameters."));
        } catch (EmptyTargetElementException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorString(e.getMessage()));
        }
    }

    /**
     * Rejects applications that do not meet a certain set of requirements.
     *
     * @param request      The request of the client.
     * @param courseCode   Course code of the course.
     * @param minGrade     Minimum grade to filter on.
     * @param minRating    Minimum rating to filter on.
     * @param minAvgRating Minimum average rating to filter on.
     * @param minReqTa     Minimum amount of times a TA must have been a TA before on other
     *                     courses to filter on.
     * @return ok message.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_LECTURER', 'ROLE_ADMIN')")
    @GetMapping("/autoReject/{courseCode}")
    public ResponseEntity<?> autoReject(HttpServletRequest request,
                                        @PathVariable String courseCode,
                                        @RequestParam(required = false) String minGrade,
                                        @RequestParam(required = false) String minRating,
                                        @RequestParam(required = false) String minAvgRating,
                                        @RequestParam(required = false) String minReqTa) {
        String token = request.getHeader(AUTHORIZATION);
        try {
            filterService
                    .autoReject(courseCode, new FilterParameters(minGrade, minRating,
                            minAvgRating, minReqTa), token);
            return ResponseEntity.ok("Applications successfully rejected.");
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorString("Wrong number format in parameters."));
        } catch (EmptyTargetElementException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorString(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorString("Something went wrong when sending"
                            + " the notifications."));
        }
    }
}
