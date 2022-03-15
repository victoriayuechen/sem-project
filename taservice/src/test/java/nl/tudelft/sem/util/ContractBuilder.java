package nl.tudelft.sem.util;

import nl.tudelft.sem.ContractInfo;
import nl.tudelft.sem.Status;
import nl.tudelft.sem.entities.Contract;

public class ContractBuilder {
    private transient Contract contract;

    /**
     * Contructor to easily make Contract entities for testing.
     */
    public ContractBuilder() {
        this.contract = new Contract();
        contract.setContractId(0L);
        contract.setCourseCode("CSE2115");
        contract.setUsername("rmihalachiuta");
        contract.setStatus(Status.PENDING);
        contract.setHoursRequired(42);
        contract.setTextualContract("Contract for TA");
        contract.setTaDescription("I had a lot of fun working as a TA");
    }

    /**
     * Contructor to easily make Contract entities using a contractInfo entity.
     *
     * @param contractInfo contractInfo entity
     *
     */
    public ContractBuilder(ContractInfo contractInfo) {
        this.contract = new Contract();
        contract.setUsername(contractInfo.getUsername());
        contract.setCourseCode(contractInfo.getCourseCode());
        contract.setHoursRequired(contractInfo.getHoursRequired());
        contract.setTextualContract(contractInfo.getTextualContract());
        contract.setStatus(contractInfo.getStatus());
        contract.setTaDescription(contractInfo.getTaDescription());
    }

    // Standard contract
    public Contract build(long id) {
        contract.setContractId(id);
        return this.contract;
    }

    public Contract retrieveWorkload() {
        return this.contract;
    }

    public ContractBuilder withStatus(Status status) {
        contract.setStatus(status);
        return this;
    }

    public ContractBuilder withName(String username) {
        contract.setUsername(username);
        return this;
    }

    public ContractBuilder withCourse(String coursecode) {
        contract.setCourseCode(coursecode);
        return this;
    }

    public ContractBuilder withHours(int hours) {
        contract.setHoursRequired(hours);
        return this;
    }

    public ContractBuilder withText(String text) {
        contract.setTextualContract(text);
        return this;
    }

    public ContractBuilder withExperience(String experience) {
        contract.setTaDescription(experience);
        return this;
    }
}
