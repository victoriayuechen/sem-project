package nl.tudelft.sem.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;
import nl.tudelft.sem.util.ApplicationBuilder;
import org.junit.jupiter.api.Test;

public class ApplicationTest {

    @Test
    public void hashCodeTest() {
        Application app = new ApplicationBuilder()
            .withName("Willy Wonka")
            .withQuarter(2).withGrade(10.0)
            .build(1L);
        int h = Objects.hash(app.getApplicationId(),
            app.getCourseCode(), app.getUsername(),
            app.getGrade(), app.getStatus(), app.getQuarter());
        assertEquals(app.hashCode(), h);
    }

    @Test
    public void isSameObjectTest() {
        Application app1 = new ApplicationBuilder().build(1L);
        assertTrue(app1.equals(app1));
    }

    @SuppressWarnings("PMD.EqualsNull")
    @Test
    public void isNullTest() {
        Application app1 = new ApplicationBuilder().build(1L);
        assertFalse(app1.equals(null));
    }

    @Test
    public void notSameClass() {
        Application app1 = new ApplicationBuilder().build(1L);
        assertFalse(app1.equals("app1"));
    }

    @Test
    public void isSameTest() {
        Application app1 = new ApplicationBuilder().build(1L);
        Application app2 = new ApplicationBuilder().build(1L);
        assertTrue(app1.equals(app2));
    }

    @Test
    public void isDifferentTest() {
        Application app1 = new ApplicationBuilder()
                .withGrade(5.8)
                .build(1L);
        Application app2 = new ApplicationBuilder()
                .withGrade(6.1)
                .build(2L);
        assertFalse(app1.equals(app2));
    }

    @Test
    public void toStringTest() {
        Application app1 = new ApplicationBuilder()
                .withGrade(5.8)
                .build(1L);
        assertFalse(app1.toString().equals(""));
    }


}
