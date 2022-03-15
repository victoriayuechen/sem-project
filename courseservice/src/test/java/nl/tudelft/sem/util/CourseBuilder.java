package nl.tudelft.sem.util;

import nl.tudelft.sem.CourseInfo;
import nl.tudelft.sem.entities.Course;

public class CourseBuilder {
    private transient Course course;

    /**
     * Constructor for CourseBuilder.
     */
    public CourseBuilder() {
        course = new Course();
        course.setCourseCode("CSE2115");
        course.setCourseName("Software Engineering Methods");
        course.setQuarter(2);
        course.setNumberOfStudents(420);
        course.setOpen(true);
        course.setAverageTaHour(0.0);
        course.setDuration(10);
        course.setStudentTaRatio(20);
    }

    /**
     * Constructor using courseInfo entity.
     *
     * @param courseInfo courseInfo entity to base Course of
     */
    public CourseBuilder(CourseInfo courseInfo) {
        course = new Course();
        course.setCourseCode(courseInfo.getCourseCode());
        course.setCourseName(courseInfo.getCourseName());
        course.setQuarter(courseInfo.getQuarter());
        course.setNumberOfStudents(courseInfo.getNumberOfStudents());
        course.setOpen(courseInfo.isOpen());
        course.setAverageTaHour(courseInfo.getAverageTaHour());
        course.setDuration(courseInfo.getDuration());
        course.setNumberOfTas(courseInfo.getNumberOfTas());
        course.setStartDate(courseInfo.getStartDate());
    }

    public Course build(String code) {
        return course;
    }

    public CourseBuilder withCourseName(String name) {
        course.setCourseName(name);
        return this;
    }

    public CourseBuilder withQuarter(int q) {
        course.setQuarter(q);
        return this;
    }

    public CourseBuilder withOpen(boolean open) {
        this.course.setOpen(open);
        return this;
    }

    public CourseBuilder withDuration(int duration) {
        this.course.setDuration(duration);
        return this;
    }
}
