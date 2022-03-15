package nl.tudelft.sem.util;

import nl.tudelft.sem.GradeInfo;
import nl.tudelft.sem.entities.Grade;

public class GradeBuilder {
    private transient Grade grade;

    /**
     * Constructor for GradeBuilder.
     */
    public GradeBuilder() {
        grade = new Grade();
        grade.setUserName("tdevalck");
        grade.setCourseCode("CSE2021");
        grade.setValue(7.6f);
    }

    /**
     * GradeBuilder constructor using gradeInfo entity.
     *
     * @param gradeInfo GradeInfo to base Grade of
     */
    public GradeBuilder(GradeInfo gradeInfo) {
        grade = new Grade();
        grade.setUserName(gradeInfo.getUsername());
        grade.setCourseCode(gradeInfo.getCourseCode());
        grade.setValue(gradeInfo.getValue());
    }

    public Grade build(long id) {
        grade.setId(id);
        return grade;
    }

    public GradeBuilder withUsername(String username) {
        grade.setUserName(username);
        return this;
    }

    public GradeBuilder withCourseCode(String courseCode) {
        grade.setCourseCode(courseCode);
        return this;
    }

    public GradeBuilder withValue(double value) {
        grade.setValue(value);
        return this;
    }
}
