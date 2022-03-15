package nl.tudelft.sem.service;

import java.util.Collection;
import nl.tudelft.sem.NotificationMessage;
import nl.tudelft.sem.entities.Notification;
import nl.tudelft.sem.entities.NotificationStatus;
import nl.tudelft.sem.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    @Autowired
    private final transient NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Returns all the pending notifications of a user, returns their text
     * and updates the notification status.
     *
     * @param username  The username of the student.
     * @return String of all the pending notifications
     */
    public String getNotifications(String username) {
        Collection<Notification> list = notificationRepository
                .findNotificationTextByUsername(username, NotificationStatus.PENDING);
        StringBuilder stringBuilder = new StringBuilder();
        list.forEach(notification -> {
            stringBuilder.append(notification.getText());
            stringBuilder.append("\n");
            notificationRepository.updateStatusById(NotificationStatus.COMPLETED,
                    notification.getNotificationId());
        });
        return stringBuilder.toString().trim();
    }

    /**
     * Adds a notification to the notification database.
     *
     * @param notificationMessage   The notificationMessage object containing
     *                              all the notifications parameters.
     */
    public void addNotification(NotificationMessage notificationMessage)
            throws IllegalArgumentException {
        Notification notification = new Notification();
        notification.setStatus(NotificationStatus.PENDING);
        notification.setUsername(notificationMessage.getUsername());
        notification.setText(notificationMessage.getText());
        notificationRepository.save(notification);
    }
}
