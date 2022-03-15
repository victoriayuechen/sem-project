package nl.tudelft.sem.service;

import java.io.IOException;
import java.util.Optional;
import nl.tudelft.sem.AverageWorkload;
import nl.tudelft.sem.SelectInfo;
import nl.tudelft.sem.Status;
import nl.tudelft.sem.communication.CentralCommunicator;
import nl.tudelft.sem.entities.Contract;
import nl.tudelft.sem.entities.Ta;
import nl.tudelft.sem.entities.Workload;
import nl.tudelft.sem.exceptions.AddRoleFailureException;
import nl.tudelft.sem.exceptions.EmptyTargetException;
import nl.tudelft.sem.repositories.ContractRepository;
import nl.tudelft.sem.repositories.TaRepository;
import nl.tudelft.sem.repositories.WorkloadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaService {
    @Autowired
    private transient TaRepository taRepository;
    @Autowired
    private transient ContractRepository contractRepository;
    @Autowired
    private transient WorkloadRepository workloadRepository;
    @Autowired
    private transient CentralCommunicator centralCommunicator;

    /**
     * Constructor for the TA Service.
     *
     * @param taRepository               Ta repository for accessing the TA database.
     * @param contractRepository         Contract repository for accessing the Contract database.
     * @param workloadRepository         Workload repository for accessing the Workload database.
     * @param centralCommunicator        The central communicator for other microservices.
     */
    public TaService(TaRepository taRepository,
                     ContractRepository contractRepository,
                     WorkloadRepository workloadRepository,
                     CentralCommunicator centralCommunicator) {
        this.contractRepository = contractRepository;
        this.taRepository = taRepository;
        this.workloadRepository = workloadRepository;
        this.centralCommunicator = centralCommunicator;
    }

    /**
     * Adds a new TA to the database and upgrades the role of the corresponding user.
     *
     * @param username The username of the target user.
     * @param token    The authorisation token.
     * @return The Ta object if the role upgrade is successful.
     * @throws IOException             Error when communicating with other services.
     * @throws InterruptedException    Error when communicating with other services.
     * @throws AddRoleFailureException Error when upgrading the role of the user.
     */
    public boolean saveTaToDatabase(String username, String token)
        throws IOException, InterruptedException, AddRoleFailureException {
        if (taRepository.findByUsername(username).isPresent()) {
            return true;
        }

        if (!centralCommunicator.addTaRole(username, token)) {
            throw new AddRoleFailureException("Could not add the TA role for new assistant.");
        }

        Ta assistant = new Ta();
        assistant.setUsername(username);
        taRepository.save(assistant);

        return true;
    }

    /**
     * This allows the TA to declare the hours they spent on average for one week.
     *
     * @param workload The workload the TA has
     * @return Returns the workload declared if successful.
     */
    public Workload declareHoursWorked(AverageWorkload workload) throws EmptyTargetException {
        Optional<Contract> contract = contractRepository
            .findByUsernameAndCourseCode(workload.getUsername(), workload.getCourseCode());

        if (contract.isEmpty()) {
            throw new EmptyTargetException("Cannot declare workload without being a TA.");
        }

        Workload actualWorkload = new Workload();
        actualWorkload.setHours(workload.getAverageHours());
        actualWorkload.setCourseCode(workload.getCourseCode());
        actualWorkload.setUsername(workload.getUsername());
        actualWorkload.setStatus(Status.PENDING);

        workloadRepository.save(actualWorkload);
        return actualWorkload;
    }

    /**
     * Checks the number of hours of a TA.
     *
     * @param workloadId the ID of the workload
     * @param token      the JWT token
     * @return the updated workload
     */
    public Workload checkHours(String workloadId, String token)
        throws EmptyTargetException, IOException, InterruptedException {
        long id = Long.parseLong(workloadId);

        Optional<Workload> workload = workloadRepository.findById(id);

        if (workload.isEmpty()) {
            throw new EmptyTargetException("No hours declared.");
        }

        // Find contract associated with workload.
        Optional<Contract> contract = contractRepository
            .findByUsernameAndCourseCode(
                workload.get().getUsername(),
                workload.get().getCourseCode()
            );

        if (contract.isEmpty()) {
            throw new EmptyTargetException("No contract found.");
        }

        // Check if the number of hours in workload
        // does not exceed number of required hours in contract.
        if (workload.get().getHours() <= contract.get().getHoursRequired()) {
            workload.get().setStatus(Status.APPROVED);
            workloadRepository.save(workload.get());

            SelectInfo selectInfo = new SelectInfo();
            selectInfo.setStatus(Status.APPROVED);
            selectInfo.setCourseCode(workload.get().getCourseCode());
            selectInfo.setUsername(workload.get().getUsername());
            centralCommunicator.sendNotification(selectInfo, token);

            return workload.get();
        }

        // If it does, reject the workload.
        workload.get().setStatus(Status.REJECTED);
        workloadRepository.save(workload.get());

        SelectInfo selectInfo = new SelectInfo();
        selectInfo.setStatus(Status.REJECTED);
        selectInfo.setCourseCode(workload.get().getCourseCode());
        selectInfo.setUsername(workload.get().getUsername());
        centralCommunicator.sendNotification(selectInfo, token);

        return workload.get();
    }

    public int getAverageHoursCourse(String courseCode, String token)
        throws IOException, InterruptedException {
        return centralCommunicator.getAverageHoursCourse(courseCode, token);
    }
}
