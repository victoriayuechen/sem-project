package nl.tudelft.sem.util;

import nl.tudelft.sem.ContractInfo;
import nl.tudelft.sem.Status;

public class ContractInfoBuilder {
    private transient ContractInfo contractInfo;

    /**
     * Contructor to easily make contractInfo entities for testing.
     */
    public ContractInfoBuilder() {
        this.contractInfo = new ContractInfo();
        contractInfo.setUsername("tdevalck");
        contractInfo.setCourseCode("CSE2020");
        contractInfo.setTextualContract("Sign here or else");
        contractInfo.setHoursRequired(10);
        contractInfo.setStatus(Status.PENDING);
        contractInfo.setTaDescription("I had fun");
    }

    public ContractInfo build() {
        return this.contractInfo;
    }

    public ContractInfoBuilder withName(String name) {
        contractInfo.setUsername(name);
        return this;
    }

    public ContractInfoBuilder withCourse(String code) {
        contractInfo.setCourseCode(code);
        return this;
    }

    public ContractInfoBuilder withTextual(String contract) {
        contractInfo.setTextualContract(contract);
        return this;
    }

    public ContractInfoBuilder withHours(int hours) {
        contractInfo.setHoursRequired(hours);
        return this;
    }

    public ContractInfoBuilder withStatus(Status status) {
        contractInfo.setStatus(status);
        return this;
    }

    public ContractInfoBuilder withExperience(String experience) {
        contractInfo.setTaDescription(experience);
        return this;
    }

}
