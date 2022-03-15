package nl.tudelft.sem.service;

import java.util.Optional;
import nl.tudelft.sem.GradeInfo;
import nl.tudelft.sem.entities.Grade;
import nl.tudelft.sem.exceptions.EmptyTargetException;
import nl.tudelft.sem.repositories.GradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GradeService {
    @Autowired
    private transient GradeRepository gradeRepository;

    /**
     * Constructor for course service.
     *
     * @param gradeRepository the grade repository.
     */
    public GradeService(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
    }

    /**
     * Returns the grade for a student given a particular course.
     *
     * @param userName   The username of the student.
     * @param courseCode The course code of the target course.
     * @return The Grade the student received.
     * @throws EmptyTargetException Exception thrown in case the student does not have a grade.
     */
    public Grade getGradeForStudent(String userName, String courseCode)
            throws EmptyTargetException {
        Optional<Grade> grade = gradeRepository
                .findGradeByCourseCodeAndAndUserName(courseCode, userName);

        if (grade.isEmpty()) {
            throw new EmptyTargetException("No such grade found for student.");
        }

        return grade.get();
    }

    /**
     * Updates a grade object in the database.
     *
     * @param gradeInfo Object that contains all of a grade's data.
     * @return gradeInfo object containing all the updated info of the grade.
     * @throws EmptyTargetException In case the grade doesn't exist in the database.
     */
    public GradeInfo updateGrade(GradeInfo gradeInfo) throws EmptyTargetException {
        if (gradeInfo == null) {
            throw new NullPointerException("Invalid contract");
        }

        if (gradeRepository
                .findGradeByCourseCodeAndAndUserName(
                        gradeInfo.getCourseCode(),
                        gradeInfo.getUsername())
                .isEmpty()) {
            throw new EmptyTargetException("No Contract found");
        }

        Grade grade = gradeRepository
                .findGradeByCourseCodeAndAndUserName(
                        gradeInfo.getCourseCode(),
                        gradeInfo.getUsername())
                .get();

        grade.setUserName(gradeInfo.getUsername());
        grade.setCourseCode(gradeInfo.getCourseCode());
        grade.setValue(gradeInfo.getValue());

        gradeRepository.save(grade);

        return gradeInfo;
    }

    /**
     * Deletes a grade from the database.
     *
     * @param gradeId   The ID of the grade.
     * @return The deleted grade.
     * @throws EmptyTargetException In case the grade doesn't exist in the database.
     */
    public Grade deleteGrade(String gradeId) throws EmptyTargetException {
        if (gradeId == null) {
            throw new NullPointerException("Invalid ID");
        }

        long id = Long.parseLong(gradeId);

        if (gradeRepository.findById(id).isEmpty()) {
            throw new EmptyTargetException("No Contract Found");
        }

        Grade grade = gradeRepository.findById(id).get();

        gradeRepository.deleteById(id);

        return grade;
    }
}
