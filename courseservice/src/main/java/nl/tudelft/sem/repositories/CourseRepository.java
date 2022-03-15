package nl.tudelft.sem.repositories;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    @Override
    Optional<Course> findById(String s);

    List<Course> findAllByQuarter(int quarter);

    @Query(value = "SELECT c.isOpen FROM Course AS c WHERE c.courseCode = ?1")
    Optional<Boolean> getRecruitment(String courseCode);
}
