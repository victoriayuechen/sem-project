package nl.tudelft.sem.repositories;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.entities.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByCourseCode(String courseCode);

    Optional<Application> findApplicationsByApplicationId(Long applicationId);

    List<Application> findApplicationsByCourseCode(String courseCode);

    List<Application> findApplicationsByUsername(String username);

    Optional<Application> findApplicationsByUsernameAndCourseCode(String username,
                                                                  String courseCode);

    List<Application> findApplicationsByUsernameAndQuarter(String username, int quarter);
}
