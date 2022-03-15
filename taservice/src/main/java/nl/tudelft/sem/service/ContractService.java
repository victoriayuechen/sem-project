package nl.tudelft.sem.service;

import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.ContractInfo;
import nl.tudelft.sem.Status;
import nl.tudelft.sem.entities.Contract;
import nl.tudelft.sem.exceptions.DuplicateObjectException;
import nl.tudelft.sem.exceptions.EmptyTargetException;
import nl.tudelft.sem.exceptions.InvalidStatusException;
import nl.tudelft.sem.repositories.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContractService {
    @Autowired
    private transient ContractRepository contractRepository;

    public ContractService(ContractRepository contractRepository) {
        this.contractRepository = contractRepository;
    }

    /**
     * Returns the number of TAs hired for a specific course.
     * Note that this is only called by Course Service.
     *
     * @param courseCode The course code for which this is requested.
     * @return The number of TAs, also known as number of contracts signed.
     */
    public int numberOfTasCourse(String courseCode) {
        return contractRepository.findAllByCourseCode(courseCode).size();
    }


    /**
     * Create a contract for an accepted candidate TA.
     *
     * @param contractInfo The contractInfo
     * @return The created contract for the TA.
     */
    public ContractInfo createContract(ContractInfo contractInfo) throws DuplicateObjectException {
        if (contractInfo == null) {
            throw new NullPointerException("Invalid ContractInfo");
        }

        if (contractRepository
            .findByUsernameAndCourseCode(contractInfo.getUsername(),
                contractInfo.getCourseCode())
            .isPresent()) {
            throw new DuplicateObjectException("Contract already Exists");
        }

        Contract contract = new Contract();
        contract.setUsername(contractInfo.getUsername());
        contract.setCourseCode(contractInfo.getCourseCode());
        contract.setHoursRequired(contractInfo.getHoursRequired());
        contract.setTextualContract(contractInfo.getTextualContract());
        contract.setStatus(Status.PENDING);
        contract.setTaDescription(contractInfo.getTaDescription());

        //Save the contract in the database
        contractRepository.save(contract);

        return contractInfo;
    }

    /**
     * Obtains the contract associated with a TA.
     *
     * @param contractId Contract Id of the contract to be obtained.
     * @return The requested contract
     */
    public Contract getContract(String contractId) throws EmptyTargetException {

        long id = Long.parseLong(contractId);

        if (contractRepository.findById(id).isEmpty()) {
            throw new EmptyTargetException("No Contract found with contractId");
        }

        Contract contract = contractRepository.findById(id).get();

        return contract;
    }

    /**
     * Signs the contract of a candidate TA.
     *
     * @param contractId Contract ID of the contract to be signed.
     * @return The updated contract with status APPROVED (= signed)
     */
    public Contract signContract(String contractId) throws Exception {
        long id = Long.parseLong(contractId);
        if (contractRepository.findById(id).isEmpty()) {
            throw new EmptyTargetException("No Contract found with given contractId");
        }

        Contract contract = contractRepository.findById(id).get();

        if (!contract.getStatus().equals(Status.PENDING)) {
            throw new InvalidStatusException("Invalid Status");
        }
        contract.setStatus(Status.APPROVED);
        contractRepository.save(contract);

        return contract;
    }

    /**
     * Rejects a contract (if current status is pending).
     *
     * @param contractId The id of the contract to be rejected
     * @return the updated contract
     */
    public Contract rejectContract(String contractId) throws Exception {
        long id = Long.parseLong(contractId);
        if (contractRepository.findById(id).isEmpty()) {
            throw new EmptyTargetException("No Contract found with given contractId");
        }

        Contract contract = contractRepository.findById(id).get();

        if (!contract.getStatus().equals(Status.PENDING)) {
            throw new InvalidStatusException("Invalid Status");
        }
        contract.setStatus(Status.REJECTED);
        contractRepository.save(contract);

        return contract;
    }

    /**
     * Revokes a contract (overwrites any status).
     *
     * @param contractId the id of the contract to be rejected
     * @return The updated contract
     */
    public Contract revokeContract(String contractId) throws Exception {
        long id = Long.parseLong(contractId);
        if (contractRepository.findById(id).isEmpty()) {
            throw new EmptyTargetException("No Contract found with given contractId");
        }

        Contract contract = contractRepository.findById(id).get();

        if (contract.getStatus().equals(Status.REVOKED)) {
            throw new InvalidStatusException("Invalid Status");
        }
        contract.setStatus(Status.REVOKED);
        contractRepository.save(contract);

        return contract;
    }

    /**
     * Endpoint to get the experience of a TA during a course.
     *
     * @param username   the username of the to get the experience from
     * @param courseCode the course of the ta to get the experience from
     * @return The experience the TA had while being a TA
     */
    public String getTaDescription(String username, String courseCode) throws EmptyTargetException {
        if (contractRepository.findByUsernameAndCourseCode(username, courseCode).isEmpty()) {
            throw new EmptyTargetException("No Contract found with the contractId");
        }

        Contract contract = contractRepository
            .findByUsernameAndCourseCode(username, courseCode).get();

        if (contract.getTaDescription() == null) {
            throw new NullPointerException("No description has been written");
        }

        return contract.getTaDescription();
    }


    /**
     * Endpoint for a TA to write about their experience as a TA.
     *
     * @param username    the username of the TA
     * @param courseCode  the courseCode of the course
     * @param description the experience the TA wrote
     * @return The (updated) contract entity
     */
    public Contract writeTaDescription(String username, String courseCode, String description)
        throws Exception {
        if (description == null || description.equals("null")) {
            throw new NullPointerException("Invalid experience");
        }

        if (contractRepository.findByUsernameAndCourseCode(username, courseCode).isEmpty()) {
            throw new EmptyTargetException("No Contract found with the contractId");
        }

        Contract contract = contractRepository
            .findByUsernameAndCourseCode(username, courseCode).get();

        contract.setTaDescription(description);
        contractRepository.save(contract);

        return contract;
    }

    /**
     * Endpoint to get all TA Descriptions of a course.
     *
     * @param courseCode The coursecode of the course
     * @return a list of descriptions of the TA's
     */
    public List<String> getCourseDescriptions(String courseCode) throws EmptyTargetException {
        List<Contract> contracts = contractRepository.findAllByCourseCode(courseCode);

        if (contracts.size() == 0) {
            throw new EmptyTargetException("No TA's found for this course");
        }

        List<String> descriptions = new ArrayList<>();

        for (Contract i : contracts) {
            if (i.getTaDescription() != null && !i.getTaDescription().equals("")) {
                descriptions.add(i.getTaDescription());
            }
        }

        if (descriptions.size() == 0) {
            throw new EmptyTargetException("No Descriptions found for this course");
        }

        return descriptions;
    }


    /**
     * Updates a Contract entity in the database.
     *
     * @param contractInfo The updated ContractInfo entity
     * @return The updated Contract entity, as confirmation
     */
    public ContractInfo updateContract(ContractInfo contractInfo) throws EmptyTargetException {
        if (contractInfo == null) {
            throw new NullPointerException("Invalid contract");
        }

        if (contractRepository
            .findByUsernameAndCourseCode(
                contractInfo.getUsername(),
                contractInfo.getCourseCode())
            .isEmpty()) {
            throw new EmptyTargetException("No Contract found");
        }

        Contract contract = contractRepository
            .findByUsernameAndCourseCode(
                contractInfo.getUsername(),
                contractInfo.getCourseCode())
            .get();

        contract.setHoursRequired(contractInfo.getHoursRequired());
        contract.setTextualContract(contractInfo.getTextualContract());
        contract.setStatus(contractInfo.getStatus());
        contract.setTaDescription(contractInfo.getTaDescription());

        contractRepository.save(contract);

        return contractInfo;
    }

    /**
     * Deletes a contract by ID.
     *
     * @param contractId The ID of the Contract as a String
     * @return The deleted Contract
     */
    public Contract deleteContract(String contractId) throws EmptyTargetException {
        if (contractId == null) {
            throw new NullPointerException("Invalid ID");
        }

        long id = Long.parseLong(contractId);

        if (contractRepository.findById(id).isEmpty()) {
            throw new EmptyTargetException("No Contract Found");
        }

        Contract contract = contractRepository.findById(id).get();

        contractRepository.deleteById(id);

        return contract;
    }

    /**
     * Gets all experiences from a TA.
     *
     * @param username username of the TA
     * @return List of courses the student has TA'd for
     * @throws EmptyTargetException Exception if no previous experiences found
     */
    public List<String> getExperiences(String username)
        throws EmptyTargetException {
        List<Contract> contracts = contractRepository.findByUsername(username);

        if (contracts.isEmpty()) {
            throw new EmptyTargetException("No reviews with this username.");
        }

        List<String> experiences = new ArrayList<>();
        for (Contract i : contracts) {
            experiences.add(i.getCourseCode());
        }
        return experiences;
    }

}
