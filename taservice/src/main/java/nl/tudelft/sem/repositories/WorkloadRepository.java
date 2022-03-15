package nl.tudelft.sem.repositories;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.entities.Workload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkloadRepository extends JpaRepository<Workload, Long> {
    @Override
    Optional<Workload> findById(Long workloadId);

    List<Workload> findAllByCourseCode(String courseCode);

    Optional<Workload> findWorkloadByUsernameAndCourseCode(String username,
                                                                  String courseCode);
}
