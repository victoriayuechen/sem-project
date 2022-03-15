package nl.tudelft.sem.repositories;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByUsername(String username);

    List<Review> findAllByCourseCode(String courseCode);

    Optional<Review> findAllByUsernameAndCourseCode(String username, String courseCode);
}
