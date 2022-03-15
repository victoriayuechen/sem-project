package nl.tudelft.sem.util;

import nl.tudelft.sem.Status;
import nl.tudelft.sem.WorkloadInfo;

public class WorkloadInfoBuilder {
    private transient WorkloadInfo workloadInfo;

    /**
     * Constructor for WorkloadInfoBuilder.
     */
    public WorkloadInfoBuilder() {
        workloadInfo = new WorkloadInfo();
        workloadInfo.setCourseCode("CSE2021");
        workloadInfo.setHours(32);
        workloadInfo.setStatus(Status.PENDING);
        workloadInfo.setUsername("tdevalck");
    }

    public WorkloadInfo build() {
        return workloadInfo;
    }

    public WorkloadInfoBuilder withCourseCode(String courseCode) {
        workloadInfo.setCourseCode(courseCode);
        return this;
    }

    public WorkloadInfoBuilder withHours(int hours) {
        workloadInfo.setHours(hours);
        return this;
    }

    public WorkloadInfoBuilder withStatus(Status status) {
        workloadInfo.setStatus(status);
        return this;
    }

    public WorkloadInfoBuilder withUsername(String username) {
        workloadInfo.setUsername(username);
        return this;
    }
}
