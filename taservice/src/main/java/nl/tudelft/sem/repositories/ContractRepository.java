package nl.tudelft.sem.repositories;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.entities.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findByUsername(String userName);

    @Override
    Optional<Contract> findById(Long contractId);

    Optional<Contract> findByUsernameAndCourseCode(String username, String courseCode);

    List<Contract> findAllByCourseCode(String courseCode);
}
