package nl.tudelft.sem.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Objects;
import org.junit.jupiter.api.Test;

public class GradeTest {
    @Test
    public void hashCodeTest() {
        Grade grade = new Grade();
        grade.setCourseCode("Here is some text.");
        int h = Objects.hash(grade.getId(), grade.getUserName(),
                grade.getCourseCode(), grade.getValue());
        assertEquals(grade.hashCode(), h);
    }

    @Test
    public void equalsTest() {
        Grade grade1 = new Grade();
        Grade grade2 = new Grade();
        assertEquals(grade1, grade2);
    }

    @Test
    public void equalsSameTest() {
        Grade grade = new Grade();
        assertEquals(grade, grade);
    }

    @Test
    public void equalsNullTest() {
        Grade grade = new Grade();
        assertNotEquals(grade, null);
    }

    @Test
    public void equalsOtherClassTest() {
        Grade grade = new Grade();
        Course course = new Course();
        assertNotEquals(grade, course);
    }

    @Test
    public void notEquals1Test() {
        Grade grade1 = new Grade();
        grade1.setCourseCode("Text 1");
        Grade grade2 = new Grade();
        grade2.setCourseCode("Text 2");
        assertNotEquals(grade1, grade2);
    }

    @Test
    public void notEquals2Test() {
        Grade grade1 = new Grade();
        grade1.setValue(2.0);
        Grade grade2 = new Grade();
        grade2.setValue(10.0);
        assertNotEquals(grade1, grade2);
    }

    @Test
    public void notEquals3Test() {
        Grade grade1 = new Grade();
        grade1.setUserName("Text 1");
        Grade grade2 = new Grade();
        grade2.setUserName("Text 2");
        assertNotEquals(grade1, grade2);
    }

    @Test
    public void notEquals4Test() {
        Grade grade1 = new Grade();
        grade1.setId(1);
        Grade grade2 = new Grade();
        grade2.setId(2);
        assertNotEquals(grade1, grade2);
    }

    @Test
    public void toStringTest() {
        assertEquals("Grade(id=0, userName=null, "
                        + "courseCode=null, value=0.0)",
                new Grade().toString());
    }
}
