package nl.tudelft.sem.entities;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import nl.tudelft.sem.Status;

@Entity
@Table(name = "contract")
@Data
public class Contract {
    @Id
    @Column(name = "contract_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long contractId;

    @Column(name = "user_name")
    private String username;
    @Column(name = "course_code")
    private String courseCode;
    @Column(name = "hours_required")
    private int hoursRequired;
    @Column(name = "textual_contract")
    private String textualContract;
    @Column(name = "status")
    private Status status;
    @Column(name = "ta_description")
    private String taDescription;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Contract contract = (Contract) o;
        return hoursRequired == contract.hoursRequired
                && Objects.equals(username, contract.username)
                && Objects.equals(courseCode, contract.courseCode)
                && Objects.equals(textualContract, contract.textualContract)
                && status == contract.status
                && Objects.equals(taDescription, contract.taDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, courseCode, hoursRequired, textualContract, status,
            taDescription);
    }
}
