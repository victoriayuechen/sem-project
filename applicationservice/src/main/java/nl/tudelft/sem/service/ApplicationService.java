package nl.tudelft.sem.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import nl.tudelft.sem.Criteria;
import nl.tudelft.sem.SelectInfo;
import nl.tudelft.sem.Status;
import nl.tudelft.sem.communication.CourseCommunicator;
import nl.tudelft.sem.communication.NotificationCommunicator;
import nl.tudelft.sem.communication.TaCommunicator;
import nl.tudelft.sem.entities.Application;
import nl.tudelft.sem.exceptions.EmptyTargetElementException;
import nl.tudelft.sem.exceptions.InvalidApplicationException;
import nl.tudelft.sem.repositories.ApplicationRepository;
import nl.tudelft.sem.util.ExperienceRecommendation;
import nl.tudelft.sem.util.FilterParameters;
import nl.tudelft.sem.util.GradeRecommendation;
import nl.tudelft.sem.util.RatingRecommendation;
import nl.tudelft.sem.util.Recommendation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService {
    @Autowired
    private transient ApplicationRepository applicationRepository;
    @Autowired
    private transient NotificationCommunicator notificationCommunicator;
    @Autowired
    private transient CourseCommunicator courseCommunicator;

    private static final double PASS = 5.75;
    private static final int CAPACITY = 3;

    /**
     * Constructor for initializing all the dependencies.
     *
     * @param applicationRepository     Application repository.
     * @param notificationCommunicator  Notification communicator.
     * @param courseCommunicator        Course communicator.
     */
    public ApplicationService(ApplicationRepository applicationRepository,
                               NotificationCommunicator notificationCommunicator,
                               CourseCommunicator courseCommunicator) {
        this.applicationRepository = applicationRepository;
        this.notificationCommunicator = notificationCommunicator;
        this.courseCommunicator = courseCommunicator;
    }

    /**
     * Creates an application.
     *
     * @param token         Security token of the HTTP-request.
     * @param courseCode    Course code of the course.
     * @param username      Username of the user.
     * @return  The application that was created.
     * @throws IOException  In case the communicator fails.
     * @throws InterruptedException In case the HTTP was interrupted.
     * @throws InvalidApplicationException  In case the application already exists.
     */
    public Application createApplication(String token, String courseCode, String username)
        throws IOException, InterruptedException, InvalidApplicationException {
        allowedToCreate(courseCode, username, token);
        int quarter = courseCommunicator.obtainCourseQuarter(courseCode, token);
        double grade = courseCommunicator.getGradeForCourse(courseCode, username, token);
        // Valid application, and we save it
        Application application = new Application(courseCode, username, quarter, grade);
        applicationRepository.save(application);

        return application;
    }
    //  To see if the student has already applied to more
    //  than three courses this quarter and has passed the course
    //     or to see if the student applied to the same position already

    private void allowedToCreate(String courseCode, String username, String token)
            throws IOException, InterruptedException, InvalidApplicationException {
        quarterRestriction(courseCode, username, token);
        applicationExists(courseCode, username);
        gradeRestriction(courseCode, username, token);
    }

    private void quarterRestriction(String courseCode, String username, String token)
            throws IOException, InterruptedException, InvalidApplicationException {
        int quarter = courseCommunicator.obtainCourseQuarter(courseCode, token);
        if (applicationRepository.findApplicationsByUsernameAndQuarter(username,
                quarter).size() >= CAPACITY) {
            throw new InvalidApplicationException("Can not apply to more then "
                    + "3 courses per quarter.");
        }
    }

    private void gradeRestriction(String courseCode, String username, String token)
            throws IOException, InterruptedException, InvalidApplicationException {
        double grade = courseCommunicator.getGradeForCourse(courseCode, username, token);
        if (grade < PASS) {
            throw new InvalidApplicationException("Grade is not sufficient enough.");
        }
    }

    private void applicationExists(String courseCode, String username)
            throws InvalidApplicationException {
        Optional<Application> previousApplication = applicationRepository
                .findApplicationsByUsernameAndCourseCode(username, courseCode);
        if ((previousApplication.isPresent()
                && previousApplication.get().getStatus() != Status.REVOKED)) {
            throw new InvalidApplicationException("Application already exists.");
        }
    }

    /**
     * Obtains all the applications with a course code.
     *
     * @param courseCode    course code of the course.
     * @return List of applications which have the course code.
     * @throws EmptyTargetElementException In case there are no application with that course code.
     */
    public List<Application> obtainApplicationsByCourse(String courseCode)
        throws EmptyTargetElementException {
        List<Application> applications = applicationRepository
            .findApplicationsByCourseCode(courseCode)
            .stream()
            .filter(a -> a.getStatus() == Status.PENDING)
            .collect(Collectors.toList());

        if (applications.isEmpty()) {
            throw new EmptyTargetElementException("No applications were found for this course.");
        }

        return applications;
    }


    /**
     * Rejects an applicant.
     *
     * @param selectInfo    All the info of the applicant.
     * @param token         The sucurity token of the HTTP-request.
     * @return returns the application with the updated status.
     * @throws IOException  In case the communicator fails.
     * @throws InterruptedException In case the communicator was interrupted.
     * @throws EmptyTargetElementException In case the applicant doesn't exist.
     */
    public Application rejectApplicant(SelectInfo selectInfo, String token)
        throws IOException, InterruptedException, EmptyTargetElementException {
        // Find the application in the database
        Optional<Application> application = applicationRepository
            .findApplicationsByUsernameAndCourseCode(selectInfo.getUsername(),
                selectInfo.getCourseCode());

        if (application.isEmpty()) {
            throw new EmptyTargetElementException("No corresponding application could be found.");
        }

        // Application can be found, so we update the status.
        application.get().setStatus(Status.REJECTED);
        applicationRepository.save(application.get());

        // Send notification to user
        notificationCommunicator.sendNotification(selectInfo, token);

        return application.get();
    }


    /** Allows a student to withdraw their application.
     *
     * @param selectInfo the select info of the application.
     * @param token the JWT token.
     * @return the application which was withdrawn.
     */
    public Application withdrawApp(SelectInfo selectInfo, String token)
            throws IOException, InterruptedException, InvalidApplicationException {

        // Find the application in the database
        Optional<Application> application = applicationRepository
                .findApplicationsByUsernameAndCourseCode(selectInfo.getUsername(),
                        selectInfo.getCourseCode());

        if (application.isEmpty()) {
            throw new InvalidApplicationException("The application was not found.");
        }

        // Check if the application has already been accepted
        if (application.get().getStatus().equals(Status.APPROVED)) {
            throw new InvalidApplicationException("You cannot withdraw this application "
                                                    + "since it has already been accepted.");
        }

        // The application was not accepted, so just update the status
        application.get().setStatus(Status.REVOKED);
        applicationRepository.save(application.get());
        // Send notification to user
        notificationCommunicator.sendNotification(selectInfo, token);

        return application.get();
    }
}
