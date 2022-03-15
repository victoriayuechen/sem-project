package nl.tudelft.sem.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.ErrorString;
import nl.tudelft.sem.WorkloadInfo;
import nl.tudelft.sem.entities.Workload;
import nl.tudelft.sem.exceptions.EmptyTargetException;
import nl.tudelft.sem.service.WorkloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/workload")
public class WorkloadController {
    @Autowired
    private transient WorkloadService workloadService;
    private static final String AUTHORIZATION = "Authorization";
    private final transient String lectureAuthority =
            "hasAnyAuthority('ROLE_LECTURER', 'ROLE_ADMIN')";

    public WorkloadController(WorkloadService workloadService) {
        this.workloadService = workloadService;
    }

    /**
     * This method gives flexibility to the lecturer
     * to reject the amount of hours based on other criteria.
     *
     * @param request    the request of the client.
     * @param workloadId the Id of the requested workload.
     * @return a newly updated workload object with rejected hours.
     */
    @PreAuthorize(lectureAuthority)
    @PostMapping("/reject/{workloadId}")
    public ResponseEntity<?> rejectWorkloadHours(HttpServletRequest request,
                                                 @PathVariable String workloadId)
        throws IOException, InterruptedException {

        String token = request.getHeader(AUTHORIZATION);

        try {
            Workload workload = workloadService.rejectHours(workloadId, token);
            return ResponseEntity
                .ok()
                .body(workload);
        } catch (EmptyTargetException e) {
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
     * Returns the number of hours worked per course by a TA in total.
     * Note that this is only called by Course Service.
     *
     * @param courseCode The course code for which this is requested.
     * @return The List of integers.
     */
    @GetMapping("/workload-hours/{courseCode}")
    public List<Integer> workloadHoursByCourse(@PathVariable String courseCode) {
        try {
            return workloadService
                .getWorkloadHoursPerCourse(courseCode);
        } catch (EmptyTargetException e) {
            return List.of();
        }
    }

    /** Endpoint to get overview of TAs and their average hours worked a week for a single course.
     *
     * @param request The request of the client
     * @param courseCode The course code corresponding to the requested overview
     * @return A List of strings, each string contain a username and the avg hours worked
     */
    @PreAuthorize(lectureAuthority)
    @GetMapping("/overview/allAverage/{courseCode}")
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public ResponseEntity<?> courseOverview(HttpServletRequest request,
                                            @PathVariable String courseCode) {
        try {
            List<String> averages = workloadService.courseOverview(courseCode);
            return ResponseEntity
                .ok()
                .body(averages);
        } catch (Exception e) {
            return ResponseEntity
                .badRequest()
                .body(new ErrorString(e.getMessage()));
        }
    }


    /**
     * Endpoint to get average worked horus a week for a specific TA.
     *
     * @param request The request object
     * @param courseCode The courseCode of the course
     * @param username The username of the specific TA
     * @return String with format to view user and hours worked
     */
    @PreAuthorize(lectureAuthority)
    @GetMapping("/overview/average/{courseCode}/{username}")
    public ResponseEntity<?> averageTa(HttpServletRequest request,
                                       @PathVariable String courseCode,
                                       @PathVariable String username) {

        try {
            float hours = workloadService.averageTa(courseCode, username);
            return ResponseEntity
                .ok()
                .body(hours);
        } catch (Exception e) {
            return ResponseEntity
                .badRequest()
                .body(new ErrorString(e.getMessage()));
        }
    }


    /**
     * Endpoint to view all TAs who worked for a course.
     *
     * @param request The request object
     * @param courseCode The courseCode of the course to view TAs of
     * @return a list of usernames for all TAs who worked for the course
     */
    @PreAuthorize(lectureAuthority)
    @GetMapping("/overview/viewTAs/{courseCode}")
    public ResponseEntity<?> viewTas(HttpServletRequest request,
                                     @PathVariable String courseCode) {

        try {
            List<String> nameList = workloadService.viewTas(courseCode);
            return ResponseEntity
                .ok()
                .body(nameList);
        } catch (Exception e) {
            return ResponseEntity
                .badRequest()
                .body(new ErrorString(e.getMessage()));
        }
    }

    /**
     * Endpoint to update a Workload entity.
     *
     * @param request the request object
     * @param workloadInfo The (updated) WorkloadInfo entity
     * @return The updated Workload entity as confirmation
     */
    @PreAuthorize(lectureAuthority)
    @PutMapping("/updateWorkload")
    public ResponseEntity<?> updateWorkload(HttpServletRequest request,
                                            @RequestBody WorkloadInfo workloadInfo) {
        try {
            WorkloadInfo updatedWorkload = workloadService.updateWorkload(workloadInfo);
            return ResponseEntity
                .ok()
                .body(updatedWorkload);
        } catch (Exception e) {
            return ResponseEntity
                .badRequest()
                .body(new ErrorString(e.getMessage()));
        }
    }

    /**
     * Deletes a Workload by ID.
     *
     * @param request the request object
     * @param workloadId The Workload ID as a String
     * @return The deleted Workload entity
     */
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @DeleteMapping("/deleteWorkload/{workloadId}")
    public ResponseEntity<?> deleteWorkload(HttpServletRequest request,
                                            @PathVariable String workloadId) {
        try {
            Workload workload = workloadService.deleteWorkload(workloadId);
            return ResponseEntity
                .ok()
                .body(workload);
        } catch (Exception e) {
            return ResponseEntity
                .badRequest()
                .body(new ErrorString(e.getMessage()));
        }
    }


}
