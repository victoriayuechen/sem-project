package nl.tudelft.sem.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.AverageWorkload;
import nl.tudelft.sem.ErrorString;
import nl.tudelft.sem.SelectInfo;
import nl.tudelft.sem.entities.Workload;
import nl.tudelft.sem.exceptions.AddRoleFailureException;
import nl.tudelft.sem.exceptions.EmptyTargetException;
import nl.tudelft.sem.service.TaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/ta")
public class TaController {
    @Autowired
    private transient TaService taService;
    private static final String AUTHORIZATION = "Authorization";
    private final transient String lectureAuthority =
            "hasAnyAuthority('ROLE_LECTURER', 'ROLE_ADMIN')";

    /**
     * Constructor for controller.
     */
    public TaController(TaService taService) {
        this.taService = taService;
    }

    /**
     * Saves a given TA to the ta microservice database. The contract
     * will also be saved, but without the information provided by the lecturer.
     *
     * @param request    The request of the client.
     * @param selectInfo The associated selection information.
     * @return True if saved successfully to both databases, false otherwise.
     */
    @PreAuthorize(lectureAuthority)
    @PostMapping("/save-ta")
    public boolean saveTaToDatabase(HttpServletRequest request,
                                    @RequestBody SelectInfo selectInfo) {
        String token = request.getHeader(AUTHORIZATION);
        try {
            boolean result = taService.saveTaToDatabase(selectInfo.getUsername(), token);
            return result;
        } catch (InterruptedException | IOException | AddRoleFailureException e) {
            return false;
        }
    }

    /**
     * Validates the workload declared by TA, given the workload ID.
     *
     * @param request   The request of the client.
     * @param workloadId The workload ID of the workload that needs verification.
     * @return The response of the service, return workload object is successful.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_LECTURER', 'ROLE_TA', 'ROLE_ADMIN')")
    @PostMapping("/workload/validate/{workloadId}")
    public ResponseEntity<?> validateWorkload(HttpServletRequest request,
                                              @PathVariable String workloadId)
            throws IOException, InterruptedException {

        String token = request.getHeader(AUTHORIZATION);

        try {
            Workload workload = taService.checkHours(workloadId, token);
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
     * Gets the average hours a TA has worked since the start of the course.
     *
     * @param request       The request body.
     * @param courseCode    The course code of the course.
     * @return the average amount of hours worked.
     */
    @PreAuthorize(lectureAuthority)
    @GetMapping("/workload/getAverage/{courseCode}")
    public ResponseEntity<?> getAverageWork(HttpServletRequest request,
                                            @PathVariable String courseCode) {
        String token = request.getHeader(AUTHORIZATION);
        try {
            int average = taService.getAverageHoursCourse(courseCode, token);
            return ResponseEntity.ok(average);
        } catch (IOException | InterruptedException e) {
            return ResponseEntity.badRequest().body(
                    new ErrorString("Unable to get average."));
        }
    }

    /**
     * This allows the TA to declare the hours they spent on average for one week.
     *
     * @param workload The workload the TA has
     * @return Returns the workload declared if successful.
     */
    @PostMapping("/declareHours")
    public ResponseEntity<?> declareHoursWorked(@RequestBody AverageWorkload workload) {
        try {
            Workload actualWorkload = taService.declareHoursWorked(workload);
            return ResponseEntity
                .ok()
                .body(actualWorkload);
        } catch (EmptyTargetException e) {
            return ResponseEntity
                .badRequest()
                .body(new ErrorString(e.getMessage()));
        }
    }
}
