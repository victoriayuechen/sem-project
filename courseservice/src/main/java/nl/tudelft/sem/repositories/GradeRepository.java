package nl.tudelft.sem.repositories;

import java.util.Collection;
import java.util.Optional;
import nl.tudelft.sem.entities.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    Optional<Grade> findGradeByCourseCodeAndAndUserName(String courseCode, String userName);

    @Query(value = "SELECT g.courseCode FROM Grade AS g WHERE g.userName = ?1")
    Collection<String> selectApplicableCoursesByUsername(String userName);
}
