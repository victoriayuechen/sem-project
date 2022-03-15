package nl.tudelft.sem.service;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nl.tudelft.sem.AverageWorkload;
import nl.tudelft.sem.CourseInfo;
import nl.tudelft.sem.GradeInfo;
import nl.tudelft.sem.communicators.TaCommunicator;
import nl.tudelft.sem.entities.Course;
import nl.tudelft.sem.entities.Grade;
import nl.tudelft.sem.exceptions.EmptyTargetException;
import nl.tudelft.sem.exceptions.InvalidCourseException;
import nl.tudelft.sem.repositories.CourseRepository;
import nl.tudelft.sem.repositories.GradeRepository;
import nl.tudelft.sem.util.CourseChecks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
public class CourseService {
    @Autowired
    private transient CourseRepository courseRepository;
    @Autowired
    private transient TaCommunicator taCommunicator;

    /**
     * Constructor for course service.
     *
     * @param courseRepository the course repository.
     */
    public CourseService(CourseRepository courseRepository,
                         TaCommunicator taCommunicator) {
        this.courseRepository = courseRepository;
        this.taCommunicator = taCommunicator;
    }

    /**
     * Closes the course if there are enough TAs.
     *
     * @param courseCode the code of the course.
     * @return the code of the closed course.
     */
    public String closeCourseEnoughTas(String courseCode)
        throws IOException, InterruptedException, InvalidCourseException {
        Optional<Course> course = courseRepository.findById(courseCode);

        if (course.isEmpty()) {
            throw new InvalidCourseException("The course does not exist.");
        }

        if (CourseChecks.courseHasEnoughTas(course.get())) {
            course.get().setOpen(false);
            courseRepository.save(course.get());
            return course.get().getCourseCode();
        } else {
            throw new InvalidCourseException("The course does not have enough TAs.");
        }
    }

    /**
     * Closes a course if the deadline for application has passed.
     *
     * @param courseCode the code of the course.
     * @return the code of the closed course.
     */
    public String closeCourseDeadlinePassed(String courseCode)
        throws IOException, InterruptedException, InvalidCourseException {
        Optional<Course> course = courseRepository.findById(courseCode);

        if (course.isEmpty()) {
            throw new InvalidCourseException("The course does not exist.");
        }

        if (CourseChecks.hasDeadlinePassed(course.get())) {
            course.get().setOpen(false);
            courseRepository.save(course.get());
            return course.get().getCourseCode();
        } else {
            throw new InvalidCourseException("The deadline to apply "
                + "for this course has not passed.");
        }
    }

    /**
     * This method adds a course.
     *
     * @param course The course to be added.
     * @return The added course.
     */
    public Course addCourse(Course course) throws IOException,
            InterruptedException, InvalidCourseException {
        Optional<Course> courseTest = courseRepository.findById(course.getCourseCode());
        if (courseTest.isPresent()) {
            throw new InvalidCourseException("The course:"
                            + course.getCourseCode() + "already exists.");
        } else {
            course.setStudentTaRatio(20);
            return courseRepository.save(course);
        }
    }

    /**
     * Closes a course.
     *
     * @param courseCode    Course code of a course.
     * @return Returns the course with the updated status.
     * @throws IOException  In case the communicator fails.
     * @throws InterruptedException In case the communicator gets interrupted.
     * @throws IllegalArgumentException In case the course doesn't exist.
     */
    public Course closeCourse(String courseCode)
            throws IOException, InterruptedException, IllegalArgumentException {
        Optional<Course> course = courseRepository.findById(courseCode);

        if (course.isEmpty()) {
            throw new IllegalArgumentException("No such course found in the database");
        }

        course.get().setOpen(false);
        courseRepository.save(course.get());
        return course.get();
    }

    /**
     * Returns the quarter of the corresponding course.
     *
     * @param courseCode The course code of the target course.
     * @return The quarter during which the course is held.
     * @throws EmptyTargetException Exception thrown when there is no such course.
     */
    public int getCourseQuarter(String courseCode) throws EmptyTargetException {
        Optional<Course> course = courseRepository.findById(courseCode);

        if (course.isEmpty()) {
            throw new EmptyTargetException("No such course can be found.");
        }

        return course.get().getQuarter();
    }

    /**
     * Returns whether or not the course is still open for recruitment.
     *
     * @param courseCode The associated course code.
     * @return True if the course is still recruiting new TAs, false otherwise.
     */
    public boolean openForRecruitment(String courseCode) {
        Optional<Course> course = courseRepository.findById(courseCode);

        return course.isPresent() && course.get().isOpen();
    }

    /**
     * Obtains the average workload of a TA for a particular course.
     *
     * @param courseCode The course code of the target course.
     * @param token      The Access token needed for service to service communication.
     * @return The average workload object containing required information.
     * @throws IOException            Exception thrown when communication with other services fail.
     * @throws InterruptedException   Exception thrown when communication with other services fail.
     * @throws InvalidCourseException Exception thrown when not all
     *                                relevant information is contained in course.
     * @throws EmptyTargetException   Exception thrown when the target course cannot be found.
     */
    public AverageWorkload obtainAverageWorkloadPerCourse(String courseCode, String token)
        throws IOException, InterruptedException, InvalidCourseException, EmptyTargetException {
        Optional<Course> course = courseRepository.findById(courseCode);

        if (course.isEmpty()) {
            throw new EmptyTargetException("No course with the given course code.");
        }

        List<Integer> hours = taCommunicator.obtainWorkLoadHours(courseCode, token);
        if (hours.isEmpty()) {
            throw new InvalidCourseException("No workload hours have "
                + "been declared for this course.");
        }

        int taCount = taCommunicator.taCount(courseCode, token);
        if (taCount <= 0) {
            throw new InvalidCourseException("There are no TAs for this course.");
        }

        double averageHrsOverAllWeeks = (double) hours.stream()
            .reduce(Integer::sum).get() / taCount;
        int average = (int) averageHrsOverAllWeeks / course.get().getDuration();

        AverageWorkload workload = new AverageWorkload();
        workload.setCourseCode(courseCode);
        workload.setAverageHours(average);

        return workload;
    }

    /**
     * Updates a course according to new info.
     *
     * @param courseInfo Course code of the course.
     * @return The course info with the updated information.
     * @throws EmptyTargetException In case the course doesn't exist.
     */
    public CourseInfo updateCourse(CourseInfo courseInfo) throws EmptyTargetException {
        if (courseInfo == null) {
            throw new NullPointerException("Invalid contract");
        }

        if (courseRepository.findById(courseInfo.getCourseCode()).isEmpty()) {
            throw new EmptyTargetException("No Contract found");
        }

        Course course = courseRepository.findById(courseInfo.getCourseCode()).get();

        course.setCourseCode(courseInfo.getCourseCode());
        course.setCourseName(courseInfo.getCourseName());
        course.setQuarter(courseInfo.getQuarter());
        course.setNumberOfStudents(courseInfo.getNumberOfStudents());
        course.setOpen(courseInfo.isOpen());
        course.setAverageTaHour(courseInfo.getAverageTaHour());
        course.setDuration(courseInfo.getDuration());
        course.setNumberOfTas(courseInfo.getNumberOfTas());
        course.setStartDate(courseInfo.getStartDate());

        courseRepository.save(course);

        return courseInfo;
    }

    /**
     * Deletes a course.
     *
     * @param courseCode Course code of a course.
     * @return The course with the updated information.
     * @throws EmptyTargetException In case the course doesn't exist.
     */
    public Course deleteCourse(String courseCode) throws EmptyTargetException {
        if (courseCode == null) {
            throw new NullPointerException("Invalid ID");
        }

        if (courseRepository.findById(courseCode).isEmpty()) {
            throw new EmptyTargetException("No Contract Found");
        }

        Course course = courseRepository.findById(courseCode).get();

        courseRepository.deleteById(courseCode);

        return course;
    }

    /**
     * Changes the student to TA ratio of a given course.
     *
     * @param courseCode The target course code.
     * @param ratio      The new student to TA ratio.
     */
    public void changeRatio(String courseCode, int ratio)
        throws IOException, InterruptedException, EmptyTargetException, InvalidCourseException {
        Optional<Course> course = courseRepository.findById(courseCode);
        if (course.isEmpty()) {
            throw new EmptyTargetException("No course with the given course code.");
        }
        course.get().setStudentTaRatio(ratio);
        courseRepository.save(course.get());
    }
}
