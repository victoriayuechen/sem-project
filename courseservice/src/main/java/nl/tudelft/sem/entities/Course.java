package nl.tudelft.sem.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "course")
@Data
public class Course {
    @Id
    @Column(name = "course_code")
    private String courseCode;
    @Column(name = "course_name")
    private String courseName;
    @Column(name = "quarter")
    private int quarter;
    @Column(name = "number_of_students")
    private int numberOfStudents;
    @Column(name = "is_open")
    private boolean isOpen;
    @Column(name = "average_ta_hour")
    private double averageTaHour;
    @Column(name = "duration")
    private int duration;
    @Column(name = "number_of_TAs")
    private int numberOfTas;
    @Column(name = "student_Ta_Ratio")
    private int studentTaRatio;
    @Column(name = "start_date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssSSSSSSSSSXXX[ VV]")
    private ZonedDateTime startDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Course course = (Course) o;
        return quarter == course.quarter && numberOfStudents == course.numberOfStudents
                && isOpen == course.isOpen
                && Double.compare(course.averageTaHour, averageTaHour) == 0
                && duration == course.duration && numberOfTas == course.numberOfTas
                && studentTaRatio == course.studentTaRatio
                && Objects.equals(courseCode, course.courseCode)
                && Objects.equals(courseName, course.courseName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseCode, courseName, quarter, numberOfStudents, isOpen,
                averageTaHour,
                duration, numberOfTas, studentTaRatio, startDate);
    }
}
