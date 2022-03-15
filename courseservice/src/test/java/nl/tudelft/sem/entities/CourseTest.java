package nl.tudelft.sem.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Objects;
import org.junit.jupiter.api.Test;

public class CourseTest {
    @Test
    public void hashCodeTest() {
        Course course = new Course();
        course.setCourseCode("Here is some text.");
        int h = Objects.hash(course.getCourseCode(), course.getCourseName(), course.getQuarter(),
                course.getNumberOfStudents(), course.isOpen(), course.getAverageTaHour(),
                course.getDuration(), course.getNumberOfTas(), course.getStudentTaRatio(),
                course.getStartDate());
        assertEquals(course.hashCode(), h);
    }

    @Test
    public void equalsTest() {
        Course course1 = new Course();
        Course course2 = new Course();
        assertEquals(course1, course2);
    }

    @Test
    public void equalsSameTest() {
        Course course = new Course();
        assertEquals(course, course);
    }

    @Test
    public void equalsNullTest() {
        Course course = new Course();
        assertNotEquals(course, null);
    }

    @Test
    public void equalsOtherClassTest() {
        Course course = new Course();
        Grade grade = new Grade();
        assertNotEquals(course, grade);
    }

    @Test
    public void notEquals1Test() {
        Course course1 = new Course();
        course1.setCourseName("Text 1");
        Course course2 = new Course();
        course2.setCourseName("Text 2");
        assertNotEquals(course1, course2);
    }

    @Test
    public void notEquals2Test() {
        Course course1 = new Course();
        course1.setQuarter(2);
        Course course2 = new Course();
        course2.setQuarter(1);
        assertNotEquals(course1, course2);
    }

    @Test
    public void notEquals3Test() {
        Course course1 = new Course();
        course1.setNumberOfStudents(400);
        Course course2 = new Course();
        course2.setNumberOfStudents(300);
        assertNotEquals(course1, course2);
    }

    @Test
    public void notEquals4Test() {
        Course course1 = new Course();
        course1.setCourseCode("Text 1");
        Course course2 = new Course();
        course2.setCourseCode("Text 2");
        assertNotEquals(course1, course2);
    }

    @Test
    public void notEquals5Test() {
        Course course1 = new Course();
        course1.setAverageTaHour(20);
        Course course2 = new Course();
        course2.setAverageTaHour(19);
        assertNotEquals(course1, course2);
    }

    @Test
    public void notEquals6Test() {
        Course course1 = new Course();
        course1.setCourseCode("Text 1");
        Course course2 = new Course();
        course2.setCourseCode("Text 2");
        assertNotEquals(course1, course2);
    }

    @Test
    public void notEquals7Test() {
        Course course1 = new Course();
        course1.setDuration(10);
        Course course2 = new Course();
        course2.setDuration(9);
        assertNotEquals(course1, course2);
    }

    @Test
    public void notEquals8Test() {
        Course course1 = new Course();
        course1.setOpen(true);
        Course course2 = new Course();
        course2.setOpen(false);
        assertNotEquals(course1, course2);
    }

    @Test
    public void notEquals9Test() {
        Course course1 = new Course();
        course1.setNumberOfTas(20);
        Course course2 = new Course();
        course2.setNumberOfTas(19);
        assertNotEquals(course1, course2);
    }

    @Test
    public void notEquals10Test() {
        Course course1 = new Course();
        course1.setStudentTaRatio(20);
        Course course2 = new Course();
        course2.setStudentTaRatio(19);
        assertNotEquals(course1, course2);
    }

    @Test
    public void toStringTest() {
        assertEquals("Course(courseCode=null, courseName=null, "
                        + "quarter=0, numberOfStudents=0, isOpen=false, "
                        + "averageTaHour=0.0, duration=0, numberOfTas=0, "
                        + "studentTaRatio=0, startDate=null)",
                new Course().toString());
    }
}
