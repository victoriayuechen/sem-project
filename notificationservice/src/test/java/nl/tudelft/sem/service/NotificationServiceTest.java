package nl.tudelft.sem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import nl.tudelft.sem.NotificationMessage;
import nl.tudelft.sem.entities.Notification;
import nl.tudelft.sem.entities.NotificationStatus;
import nl.tudelft.sem.repositories.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(NotificationService.class)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class NotificationServiceTest {
    @MockBean
    private transient NotificationRepository notificationRepository;

    private transient NotificationService notificationService;

    private transient NotificationMessage notificationMessage;
    private transient Notification notification;

    /**
     * Sets up all the objects for testing before each test.
     */
    @BeforeEach
    public void setup() {
        notificationMessage = new NotificationMessage();
        notificationMessage.setText("Text1.");
        notificationMessage.setUsername("ljpdeswart");

        notificationService = new NotificationService(notificationRepository);

        notification = new Notification();
        notification.setNotificationId(0);
        notification.setStatus(NotificationStatus.PENDING);
        notification.setText("Text1.");
        notification.setUsername("ljpdeswart");
    }

    @Test
    public void addNotificationTest() {
        notificationService.addNotification(notificationMessage);
        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    public void getNotificationTest() {
        when(notificationRepository
                .findNotificationTextByUsername(notification.getUsername(),
                        NotificationStatus.PENDING))
                .thenReturn(List.of(notification));
        when(notificationRepository.updateStatusById(NotificationStatus.COMPLETED,
                notification.getNotificationId())).thenReturn(1);

        assertEquals(notification.getText(),
                notificationService.getNotifications(notification.getUsername()));
    }

    @Test
    public void getMultipleNotificationsTest() {
        when(notificationRepository
                .findNotificationTextByUsername(notification.getUsername(),
                        NotificationStatus.PENDING))
                .thenReturn(List.of(notification, notification));
        when(notificationRepository.updateStatusById(NotificationStatus.COMPLETED,
                notification.getNotificationId())).thenReturn(1);

        assertEquals(notification.getText() + "\n" + notification.getText(),
                notificationService.getNotifications(notification.getUsername()));
    }
}