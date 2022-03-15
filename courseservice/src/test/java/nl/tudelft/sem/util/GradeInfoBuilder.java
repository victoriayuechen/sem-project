package nl.tudelft.sem.util;

import nl.tudelft.sem.GradeInfo;

public class GradeInfoBuilder {
    private transient GradeInfo gradeInfo;

    /**
     * Constructor for GradeInfoBuiler.
     */
    public GradeInfoBuilder() {
        gradeInfo = new GradeInfo();
        gradeInfo.setCourseCode("CSE2021");
        gradeInfo.setValue(7.2f);
        gradeInfo.setUsername("tdevalck");
    }

    public GradeInfo build() {
        return gradeInfo;
    }

    public GradeInfoBuilder withCourseCode(String courseCode) {
        gradeInfo.setCourseCode(courseCode);
        return this;
    }

    public GradeInfoBuilder withValue(double value) {
        gradeInfo.setValue(value);
        return this;
    }

    public GradeInfoBuilder withUsername(String username) {
        gradeInfo.setUsername(username);
        return this;
    }
}
