package nl.tudelft.sem.entities;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "notification")
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "notification_id")
    private long notificationId;
    @Column(name = "text")
    private String text;
    @Enumerated
    @Column(name = "status")
    private NotificationStatus status;
    @Column(name = "username")
    private String username;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Notification that = (Notification) o;
        return notificationId == that.notificationId && Objects.equals(text, that.text)
            && status == that.status && Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notificationId, text, status, username);
    }
}
