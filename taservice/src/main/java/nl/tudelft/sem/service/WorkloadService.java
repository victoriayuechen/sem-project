package nl.tudelft.sem.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import nl.tudelft.sem.SelectInfo;
import nl.tudelft.sem.Status;
import nl.tudelft.sem.WorkloadInfo;
import nl.tudelft.sem.communication.CentralCommunicator;
import nl.tudelft.sem.entities.Workload;
import nl.tudelft.sem.exceptions.EmptyTargetException;
import nl.tudelft.sem.repositories.WorkloadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkloadService {
    @Autowired
    private transient WorkloadRepository workloadRepository;
    @Autowired
    private transient CentralCommunicator centralCommunicator;

    public WorkloadService(WorkloadRepository workloadRepository,
                           CentralCommunicator centralCommunicator) {
        this.workloadRepository = workloadRepository;
        this.centralCommunicator = centralCommunicator;
    }

    /**
     * Returns the number of hours worked per course by a TA in total.
     * Note that this is only called by Course Service.
     *
     * @param courseCode The course code for which this is requested.
     * @return The List of integers.
     */
    public List<Integer> getWorkloadHoursPerCourse(String courseCode) throws EmptyTargetException {
        List<Workload> workloads = workloadRepository
            .findAllByCourseCode(courseCode);

        if (workloads.isEmpty()) {
            throw new EmptyTargetException("No workloads available for this given course.");
        }

        return workloads
            .stream()
            .map(Workload::getHours)
            .collect(Collectors.toList());
    }

    /**
     * Endpoint to get overview of TAs and their average hours worked a week for a single course.
     *
     * @param courseCode The course code corresponding to the requested overview
     * @return A List of strings, each string contain a username and the avg hours worked
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public List<String> courseOverview(String courseCode) throws EmptyTargetException {

        List<Workload> workloads = workloadRepository.findAllByCourseCode(courseCode);

        if (workloads.isEmpty()) {
            throw new EmptyTargetException("There are no workloads for the course: " + courseCode);
        }

        List<String> names = new ArrayList<>();
        List<Float> hours = new ArrayList<>();

        //Average gets calculated by assuming set amount of 10 Weeks
        for (Workload i : workloads) {
            if (!names.contains(i.getUsername())) {
                names.add(i.getUsername());
                hours.add(i.getHours() / 10.0f);
            } else {
                hours.set(names.indexOf(i.getUsername()),
                    hours.get(names.indexOf(i.getUsername())) + (i.getHours() / 10.0f));
            }
        }

        List<String> average = new ArrayList<>();

        for (int i = 0; i < names.size(); i++) {
            average.add("Name: " + names.get(i) + "; Hours/Week: " + hours.get(i));
        }


        return average;
    }

    /**
     * Endpoint to get average worked horus a week for a specific TA.
     *
     * @param courseCode The courseCode of the course
     * @param username   The username of the specific TA
     * @return String with format to view user and hours worked
     */
    public float averageTa(String courseCode, String username) {
        List<Workload> workloads = workloadRepository.findAllByCourseCode(courseCode);

        float hours = 0;
        for (Workload i : workloads) {
            if (i.getUsername().equals(username)) {
                hours += i.getHours() / 10.0f;
            }
        }

        return hours;
    }

    /**
     * Endpoint to view all TAs who worked for a course.
     *
     * @param courseCode The courseCode of the course to view TAs of
     * @return a list of usernames for all TAs who worked for the course
     */
    public List<String> viewTas(String courseCode) throws EmptyTargetException {
        List<Workload> workloads = workloadRepository.findAllByCourseCode(courseCode);

        if (workloads.isEmpty()) {
            throw new EmptyTargetException("No workloads found for the course: " + courseCode);
        }

        Set<String> names = new HashSet<>();

        for (Workload i : workloads) {
            names.add(i.getUsername());
        }

        List<String> nameList = new ArrayList<>(names);

        return nameList;
    }

    /**
     * Endpoint to update a Workload entity.
     *
     * @param workloadInfo The (updated) WorkloadInfo entity
     * @return The updated Workload entity as confirmation
     */
    public WorkloadInfo updateWorkload(WorkloadInfo workloadInfo) throws EmptyTargetException {
        if (workloadInfo == null) {
            throw new NullPointerException("Invalid contract");
        }

        if (workloadRepository
            .findWorkloadByUsernameAndCourseCode(
                workloadInfo.getUsername(),
                workloadInfo.getCourseCode())
            .isEmpty()) {
            throw new EmptyTargetException("No Contract found");
        }

        Workload workload = workloadRepository
            .findWorkloadByUsernameAndCourseCode(
                workloadInfo.getUsername(),
                workloadInfo.getCourseCode())
            .get();

        workload.setHours(workloadInfo.getHours());
        workload.setStatus(workloadInfo.getStatus());

        workloadRepository.save(workload);

        return workloadInfo;
    }

    /**
     * Deletes a Workload by ID.
     *
     * @param workloadId The Workload ID as a String
     * @return The deleted Workload entity
     */
    public Workload deleteWorkload(String workloadId) throws EmptyTargetException {
        if (workloadId == null) {
            throw new NullPointerException("Invalid ID");
        }

        long id = Long.parseLong(workloadId);

        if (workloadRepository.findById(id).isEmpty()) {
            throw new EmptyTargetException("No Contract Found");
        }

        Workload workload = workloadRepository.findById(id).get();

        workloadRepository.deleteById(id);

        return workload;
    }

    /**
     * Offers flexibility in rejection.
     *
     * @param workloadId the id of the workload.
     * @param token      the JWT token.
     * @return the updated workload object.
     */
    public Workload rejectHours(String workloadId, String token)
        throws EmptyTargetException, IOException, InterruptedException {

        long id = Long.parseLong(workloadId);

        Optional<Workload> workload = workloadRepository.findById(id);

        if (workload.isEmpty()) {
            throw new EmptyTargetException("Hours were not declared.");
        }

        workload.get().setStatus(Status.REJECTED);
        workloadRepository.save(workload.get());

        SelectInfo selectInfo = new SelectInfo();
        selectInfo.setStatus(Status.REJECTED);
        selectInfo.setCourseCode(workload.get().getCourseCode());
        selectInfo.setUsername(workload.get().getUsername());
        centralCommunicator.sendNotification(selectInfo, token);

        return workload.get();
    }

}
