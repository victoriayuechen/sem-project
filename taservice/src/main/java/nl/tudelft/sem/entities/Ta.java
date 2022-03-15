package nl.tudelft.sem.entities;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "ta")
@Data
public class Ta {
    @Id
    @Column(name = "user_name")
    private String username;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Ta ta = (Ta) o;
        return Objects.equals(username, ta.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
