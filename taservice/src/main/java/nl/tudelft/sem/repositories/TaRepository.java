package nl.tudelft.sem.repositories;


import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.entities.Ta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaRepository extends JpaRepository<Ta, String> {
    Optional<Ta> findByUsername(String userName);

}
