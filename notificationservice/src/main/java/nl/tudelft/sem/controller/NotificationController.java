package nl.tudelft.sem.controller;

import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.ErrorString;
import nl.tudelft.sem.NotificationMessage;
import nl.tudelft.sem.entities.Notification;
import nl.tudelft.sem.entities.NotificationStatus;
import nl.tudelft.sem.repositories.NotificationRepository;
import nl.tudelft.sem.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    @Autowired
    private transient NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Returns all the pending notifications of a user, returns their text
     * and updates the notification status.
     *
     * @param request   The request object.
     * @param username  The username of the student.
     * @return String of all the pending notifications
     */
    @PreAuthorize("hasAnyAuthority('ROLE_LECTURER', 'ROLE_TA','ROLE_STUDENT', 'ROLE_ADMIN')")
    @GetMapping("/get_notifications/{username}")
    public ResponseEntity<?> getNotifications(HttpServletRequest request,
                                             @PathVariable String username) {
        String notifications = notificationService.getNotifications(username);
        return ResponseEntity.ok().body(notifications);
    }

    /**
     * Adds a notification to the notification database.
     *
     * @param request               The request object.
     * @param notificationMessage   The notificationMessage object containing
     *                              all the notifications parameters.
     * @return String message saying the creating of the notification was successful.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PostMapping("/create_notification")
    public ResponseEntity<?> addNotification(HttpServletRequest request,
                                             @RequestBody NotificationMessage notificationMessage) {
        try {
            notificationService.addNotification(notificationMessage);
            return ResponseEntity.ok().body("Notification successfully created.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ErrorString("Something went wrong when fetching your notifications."));
        }
    }
}
