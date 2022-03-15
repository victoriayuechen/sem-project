package nl.tudelft.sem.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.ContractInfo;
import nl.tudelft.sem.ErrorString;
import nl.tudelft.sem.entities.Contract;
import nl.tudelft.sem.exceptions.EmptyTargetException;
import nl.tudelft.sem.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(path = "/contract")
public class ContractController {
    @Autowired
    private transient ContractService contractService;
    private final transient String lectureAuthority =
            "hasAnyAuthority('ROLE_LECTURER', 'ROLE_ADMIN')";
    private final transient String taAuthority = "hasAnyAuthority('ROLE_TA', 'ROLE_ADMIN')";

    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    /**
     * Obtains the experiences associated with a TA.
     *
     * @param request  The request object.
     * @param userName The username of the TA in question.
     * @return The experiences of the TA as a list of course codes.
     */
    @PreAuthorize(lectureAuthority)
    @GetMapping("/getExperiences/{userName}")
    public ResponseEntity<?> obtainExperiences(HttpServletRequest request,
                                               @PathVariable String userName) {
        try {
            return ResponseEntity.ok().body(contractService.getExperiences(userName));
        } catch (EmptyTargetException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorString(e.getMessage()));
        }
    }

    /**
     * Create a contract for an accepted candidate TA.
     *
     * @param request      The request object
     * @param contractInfo The contractInfo
     * @return The created contract for the TA.
     */
    @PreAuthorize(lectureAuthority)
    @PostMapping("/createContract")
    public ResponseEntity<?> createContract(HttpServletRequest request,
                                            @RequestBody ContractInfo contractInfo) {

        try {
            ContractInfo created = contractService.createContract(contractInfo);
            return ResponseEntity
                    .ok()
                    .body(created);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorString(e.getMessage()));
        }
    }

    /**
     * Obtains the contract associated with a TA.
     *
     * @param request    The request object.
     * @param contractId Contract Id of the contract to be obtained.
     * @return The requested contract
     */
    @PreAuthorize(lectureAuthority)
    @GetMapping("/getContract/{contractId}")
    public ResponseEntity<?> getContract(HttpServletRequest request,
                                         @PathVariable String contractId) {

        try {
            Contract contract = contractService.getContract(contractId);
            return ResponseEntity
                    .ok()
                    .body(contract);
        } catch (EmptyTargetException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorString(e.getMessage()));
        }
    }

    /**
     * Signs the contract of a candidate TA.
     *
     * @param request    The request object.
     * @param contractId Contract ID of the contract to be signed.
     * @return The updated contract with status APPROVED (= signed)
     */
    @PreAuthorize(lectureAuthority)
    @PostMapping("/signContract/{contractId}")
    public ResponseEntity<?> signContract(HttpServletRequest request,
                                          @PathVariable String contractId) {
        try {
            Contract contract = contractService.signContract(contractId);
            return ResponseEntity
                    .ok()
                    .body(contract);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorString(e.getMessage()));
        }
    }

    /**
     * Returns the number of TAs hired for a specific course.
     * Note that this is only called by Course Service.
     *
     * @param courseCode The course code for which this is requested.
     * @return The number of TAs, also known as number of contracts signed.
     */
    @GetMapping("/countTa/{courseCode}")
    public Integer findNumberOfTa(@PathVariable String courseCode) {
        return contractService.numberOfTasCourse(courseCode);
    }

    /**
     * Rejects a contract (if current status is pending).
     *
     * @param request The request object
     * @param contractId The id of the contract to be rejected
     * @return the updated contract
     */
    @PreAuthorize(lectureAuthority)
    @PostMapping("/rejectContract/{contractId}")
    public ResponseEntity<?> rejectContract(HttpServletRequest request,
                                            @PathVariable String contractId) {
        try {
            Contract contract = contractService.rejectContract(contractId);
            return ResponseEntity
                    .ok()
                    .body(contract);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorString(e.getMessage()));
        }
    }

    /**
     * Revokes a contract (overwrites any status).
     *
     * @param request The request object
     * @param contractId the id of the contract to be rejected
     * @return The updated contract
     */
    @PreAuthorize(lectureAuthority)
    @PostMapping("/revokeContract/{contractId}")
    public ResponseEntity<?> revokeContract(HttpServletRequest request,
                                            @PathVariable String contractId) {
        try {
            Contract contract = contractService.revokeContract(contractId);
            return ResponseEntity
                    .ok()
                    .body(contract);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorString(e.getMessage()));
        }
    }

    /**
     * Endpoint to get the experience of a TA during a course.
     *
     * @param request The request object
     * @param username the username of the to get the experience from
     * @param courseCode the course of the ta to get the experience from
     * @return The experience the TA had while being a TA
     */
    @PreAuthorize(lectureAuthority)
    @GetMapping("/getTaDescription/{username}/{courseCode}")
    public ResponseEntity<?> getTaDescription(HttpServletRequest request,
                                              @PathVariable String username,
                                              @PathVariable String courseCode) {

        try {
            String description = contractService.getTaDescription(username, courseCode);
            return ResponseEntity
                    .ok()
                    .body(description);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorString(e.getMessage()));
        }
    }

    /**
     * Endpoint for a TA to write about their experience as a TA.
     *
     * @param request the request object
     * @param username the username of the TA
     * @param courseCode the courseCode of the course
     * @param description the experience the TA wrote
     * @return The (updated) contract entity
     */
    @PreAuthorize(taAuthority)
    @PostMapping("/writeTaDescription/{username}/{courseCode}")
    public ResponseEntity<?> writeTaDescription(HttpServletRequest request,
                                                @PathVariable String username,
                                                @PathVariable String courseCode,
                                                @RequestBody String description) {

        try {
            Contract contract = contractService
                    .writeTaDescription(username, courseCode, description);
            return ResponseEntity
                    .ok()
                    .body(contract);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorString(e.getMessage()));
        }
    }

    /**
     * Endpoint to get all TA Descriptions of a course.
     *
     * @param request The request object
     * @param courseCode The coursecode of the course
     * @return a list of descriptions of the TA's
     */
    @PreAuthorize(lectureAuthority)
    @GetMapping("/getCourseDescriptions/{courseCode}")
    public ResponseEntity<?> getCourseDescriptions(HttpServletRequest request,
                                                   @PathVariable String courseCode) {

        try {
            List<String> descriptions = contractService.getCourseDescriptions(courseCode);
            return ResponseEntity
                    .ok()
                    .body(descriptions);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorString(e.getMessage()));
        }
    }

    /**
     * Updates a Contract entity in the database.
     *
     * @param request The request object
     * @param contractInfo The updated ContractInfo entity
     * @return The updated Contract entity, as confirmation
     */
    @PreAuthorize(lectureAuthority)
    @PutMapping("/updateContract")
    public ResponseEntity<?> updateContract(HttpServletRequest request,
                                            @RequestBody ContractInfo contractInfo) {
        try {
            ContractInfo updatedContract = contractService.updateContract(contractInfo);
            return ResponseEntity
                    .ok()
                    .body(updatedContract);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorString(e.getMessage()));
        }
    }


    /**
     * Deletes a contract by ID.
     *
     * @param request The request object
     * @param contractId The ID of the Contract as a String
     * @return The deleted Contract
     */
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @DeleteMapping("/deleteContract/{contractId}")
    public ResponseEntity<?> deleteContract(HttpServletRequest request,
                                            @PathVariable String contractId) {
        try {
            Contract contract = contractService.deleteContract(contractId);
            return ResponseEntity
                    .ok()
                    .body(contract);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorString(e.getMessage()));
        }
    }
}
