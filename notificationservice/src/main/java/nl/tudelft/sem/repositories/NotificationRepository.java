package nl.tudelft.sem.repositories;

import java.util.Collection;
import nl.tudelft.sem.entities.Notification;
import nl.tudelft.sem.entities.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

    @Query(value = "SELECT n FROM Notification n WHERE n.username = ?1 AND n.status = ?2")
    Collection<Notification> findNotificationTextByUsername(String username,
                                                            NotificationStatus status);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Notification n SET n.status = ?1 WHERE n.id = ?2")
    int updateStatusById(NotificationStatus notificationStatus, long id);
}
