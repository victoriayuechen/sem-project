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
@Table(name = "application")
@Data
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "application_id")
    private long applicationId;

    @Column(name = "course_code")
    private String courseCode;
    @Column(name = "user_name")
    private String username;
    @Column(name = "grade")
    private double grade;
    @Column(name = "status")
    private Status status;
    @Column(name = "quarter")
    private int quarter;

    /**
     * Creates an application object.
     *
     * @param courseCode Course code of the course.
     * @param username   Username of the user.
     * @param quarter    Quarter of the course.
     * @param grade      Grade the user got for the course.
     */
    public Application(String courseCode, String username, int quarter, double grade) {
        this.courseCode = courseCode;
        this.username = username;
        this.grade = grade;
        this.status = Status.PENDING;
        this.quarter = quarter;
    }

    public Application() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Application that = (Application) o;
        return Double.compare(that.grade, grade) == 0 && quarter == that.quarter
                && Objects.equals(courseCode, that.courseCode)
                && Objects.equals(username, that.username) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(applicationId, courseCode, username, grade, status, quarter);
    }
}
