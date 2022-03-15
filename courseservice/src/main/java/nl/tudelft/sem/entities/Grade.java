package nl.tudelft.sem.entities;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "grades")
@Data
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "course_code")
    private String courseCode;
    @Column
    private double value;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Grade grade = (Grade) o;
        return id == grade.id && Double.compare(grade.value, value) == 0
            && Objects.equals(userName, grade.userName)
            && Objects.equals(courseCode, grade.courseCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userName, courseCode, value);
    }
}
