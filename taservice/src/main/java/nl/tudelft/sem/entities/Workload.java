package nl.tudelft.sem.entities;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import nl.tudelft.sem.Status;

@Entity
@Table(name = "workload")
@Data
public class Workload {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "workload_id")
    private long workloadId;

    @Column(name = "user_name")
    private String username;
    @Column(name = "course_code")
    private String courseCode;
    @Column(name = "workload_hours")
    private int hours;
    @Column(name = "status")
    private Status status;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Workload workload = (Workload) o;
        return workloadId == workload.workloadId && hours == workload.hours
                && Objects.equals(username, workload.username)
                && Objects.equals(courseCode, workload.courseCode)
                && status == workload.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(workloadId, username, courseCode, hours, status);
    }
}
