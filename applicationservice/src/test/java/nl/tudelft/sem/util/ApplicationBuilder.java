package nl.tudelft.sem.util;

import nl.tudelft.sem.Status;
import nl.tudelft.sem.entities.Application;

// Class for returning application objects
public class ApplicationBuilder {
    private transient Application application;

    /**
     * Builds an application.
     */
    public ApplicationBuilder() {
        this.application = new Application();
        application.setApplicationId(0L);
        application.setCourseCode("CSE2115");
        application.setUsername("Victoria");

        application.setGrade(10.0);
        application.setStatus(Status.PENDING);
        application.setQuarter(2);
    }

    // Standard application
    public Application build(long id) {
        application.setApplicationId(id);
        return this.application;
    }

    public Application retrieveApplication() {
        return this.application;
    }

    public ApplicationBuilder withGrade(double grade) {
        application.setGrade(grade);
        return this;
    }

    public ApplicationBuilder withQuarter(int q) {
        application.setQuarter(q);
        return this;
    }

    public ApplicationBuilder withStatus(Status status) {
        application.setStatus(status);
        return this;
    }

    public ApplicationBuilder withName(String username) {
        application.setUsername(username);
        return this;
    }

    public ApplicationBuilder withCourse(String coursecode) {
        application.setCourseCode(coursecode);
        return this;
    }


}
