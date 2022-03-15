package nl.tudelft.sem.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Objects;
import nl.tudelft.sem.NotificationMessage;
import org.junit.jupiter.api.Test;

public class NotificationTest {
    @Test
    public void hashCodeTest() {
        Notification notification = new Notification();
        notification.setText("Here is some text.");
        int h = Objects.hash(notification.getNotificationId(),
                notification.getText(), notification.getUsername(),
                notification.getStatus());
        assertEquals(notification.hashCode(), h);
    }

    @Test
    public void equalsTest() {
        Notification notification1 = new Notification();
        Notification notification2 = new Notification();
        assertEquals(notification1, notification2);
    }

    @Test
    public void equalsSameTest() {
        Notification notification = new Notification();
        assertEquals(notification, notification);
    }

    @Test
    public void equalsNullTest() {
        Notification notification = new Notification();
        assertNotEquals(notification, null);
    }

    @Test
    public void equalsOtherClassTest() {
        Notification notification = new Notification();
        NotificationMessage message = new NotificationMessage();
        assertNotEquals(notification, message);
    }

    @Test
    public void notEquals1Test() {
        Notification notification1 = new Notification();
        notification1.setText("Text 1");
        Notification notification2 = new Notification();
        notification2.setText("Text 2");
        assertNotEquals(notification1, notification2);
    }

    @Test
    public void notEquals2Test() {
        Notification notification1 = new Notification();
        notification1.setUsername("Text 1");
        Notification notification2 = new Notification();
        notification2.setUsername("Text 2");
        assertNotEquals(notification1, notification2);
    }

    @Test
    public void notEquals3Test() {
        Notification notification1 = new Notification();
        notification1.setStatus(NotificationStatus.PENDING);
        Notification notification2 = new Notification();
        notification2.setStatus(NotificationStatus.COMPLETED);
        assertNotEquals(notification1, notification2);
    }

    @Test
    public void notEquals4Test() {
        Notification notification1 = new Notification();
        notification1.setNotificationId(1);
        Notification notification2 = new Notification();
        notification2.setNotificationId(2);
        assertNotEquals(notification1, notification2);
    }

    @Test
    public void toStringTest() {
        assertEquals("Notification(notificationId=0,"
                + " text=null, status=null, username=null)",
                new Notification().toString());
    }
}
