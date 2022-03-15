package nl.tudelft.sem.util;

import nl.tudelft.sem.Status;
import nl.tudelft.sem.WorkloadInfo;
import nl.tudelft.sem.entities.Workload;

public class WorkloadBuilder {
    private transient Workload workload;

    /**
     * Creates a workload entity with preset variables.
     */
    public WorkloadBuilder() {
        workload = new Workload();
        workload.setCourseCode("CSE2115");
        workload.setUsername("rmihalachiuta");
        workload.setStatus(Status.PENDING);
        workload.setHours(40);
    }

    /**
     * Constructor for WorkloadBuilder using workloadInfo.
     *
     * @param workloadInfo workloadInfo to base workload of
     */
    public WorkloadBuilder(WorkloadInfo workloadInfo) {
        workload = new Workload();
        workload.setCourseCode(workloadInfo.getCourseCode());
        workload.setHours(workloadInfo.getHours());
        workload.setStatus(workloadInfo.getStatus());
        workload.setUsername(workloadInfo.getUsername());
    }

    // Standard workload
    public Workload build(long id) {
        workload.setWorkloadId(id);
        return this.workload;
    }

    public Workload retrieveWorkload() {
        return this.workload;
    }

    public WorkloadBuilder withStatus(Status status) {
        workload.setStatus(status);
        return this;
    }

    public WorkloadBuilder withName(String username) {
        workload.setUsername(username);
        return this;
    }

    public WorkloadBuilder withCourse(String coursecode) {
        workload.setCourseCode(coursecode);
        return this;
    }

    public WorkloadBuilder withHours(int hours) {
        workload.setHours(hours);
        return this;
    }
}
