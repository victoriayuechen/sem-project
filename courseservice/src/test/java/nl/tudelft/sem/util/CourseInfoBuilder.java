package nl.tudelft.sem.util;

import java.time.ZonedDateTime;
import nl.tudelft.sem.CourseInfo;

public class CourseInfoBuilder {
    private transient CourseInfo courseInfo;

    /**
     * courseInfoBuilder constructor.
     */
    public CourseInfoBuilder() {
        courseInfo = new CourseInfo();
        courseInfo.setCourseName("The Art of War - Sun Tzu");
        courseInfo.setDuration(10);
        courseInfo.setOpen(false);
        courseInfo.setQuarter(2);
        courseInfo.setAverageTaHour(23.5);
        courseInfo.setNumberOfTas(25);
        courseInfo.setNumberOfStudents(250);
        courseInfo.setStartDate(ZonedDateTime.now());
    }

    public CourseInfo build(String courseCode) {
        courseInfo.setCourseCode(courseCode);
        return courseInfo;
    }

    public CourseInfoBuilder withCourseName(String courseName) {
        courseInfo.setCourseName(courseName);
        return this;
    }

    public CourseInfoBuilder withQuarter(int quarter) {
        courseInfo.setQuarter(quarter);
        return this;
    }

    public CourseInfoBuilder withNumberOfStudents(int numberOfStudents) {
        courseInfo.setNumberOfStudents(numberOfStudents);
        return this;
    }

    public CourseInfoBuilder withIsOpen(boolean isOpen) {
        courseInfo.setOpen(isOpen);
        return this;
    }

    public CourseInfoBuilder withAverageTaHour(double averageTaHour) {
        courseInfo.setAverageTaHour(averageTaHour);
        return this;
    }

    public CourseInfoBuilder withDuration(int duration) {
        courseInfo.setDuration(duration);
        return this;
    }

    public CourseInfoBuilder withNumberOfTas(int numberOfTas) {
        courseInfo.setNumberOfTas(numberOfTas);
        return this;
    }

    public CourseInfoBuilder withStartDate(ZonedDateTime startDate) {
        courseInfo.setStartDate(startDate);
        return this;
    }
}
