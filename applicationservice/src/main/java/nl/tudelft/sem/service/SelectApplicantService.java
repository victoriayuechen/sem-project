package nl.tudelft.sem.service;

import java.io.IOException;
import java.util.Optional;
import nl.tudelft.sem.SelectInfo;
import nl.tudelft.sem.Status;
import nl.tudelft.sem.communication.CourseCommunicator;
import nl.tudelft.sem.communication.NotificationCommunicator;
import nl.tudelft.sem.communication.TaCommunicator;
import nl.tudelft.sem.entities.Application;
import nl.tudelft.sem.exceptions.EmptyTargetElementException;
import nl.tudelft.sem.exceptions.InvalidApplicationException;
import nl.tudelft.sem.repositories.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SelectApplicantService {
    @Autowired
    private transient ApplicationRepository applicationRepository;
    @Autowired
    private transient TaCommunicator taCommunicator;
    @Autowired
    private transient NotificationCommunicator notificationCommunicator;
    @Autowired
    private transient CourseCommunicator courseCommunicator;

    /**
     * Constructor of the SelectApplicantService.
     *
     * @param applicationRepository     The application repository.
     * @param taCommunicator            The TA communicator.
     * @param notificationCommunicator  The notification communicator.
     * @param courseCommunicator        The course communicator.
     */
    public SelectApplicantService(ApplicationRepository applicationRepository,
                              TaCommunicator taCommunicator,
                              NotificationCommunicator notificationCommunicator,
                              CourseCommunicator courseCommunicator) {
        this.applicationRepository = applicationRepository;
        this.taCommunicator = taCommunicator;
        this.notificationCommunicator = notificationCommunicator;
        this.courseCommunicator = courseCommunicator;
    }

    /**
     * Selects an applicant.
     *
     * @param selectInfo    The info of the applicant.
     * @param token         The security token of the HTTP-request.
     * @return The application object with the updated status.
     * @throws IOException  In case the communicator fails.
     * @throws InterruptedException In case the communicator get interrupted.
     * @throws InvalidApplicationException  In case the course is no longer recruiting.
     * @throws EmptyTargetElementException  In case the applicant doesn't exist.
     */
    public Application selectApplicant(SelectInfo selectInfo, String token)
            throws IOException, InterruptedException, InvalidApplicationException,
            EmptyTargetElementException {
        boolean openForRecruitment = courseCommunicator.courseOpenForRecruitment(selectInfo, token);

        if (!openForRecruitment) {
            throw new InvalidApplicationException("Course is no longer recruiting applicants.");
        }

        // Find the application in the database
        Optional<Application> application = applicationRepository
                .findApplicationsByUsernameAndCourseCode(selectInfo.getUsername(),
                        selectInfo.getCourseCode());

        if (application.isEmpty()) {
            throw new EmptyTargetElementException("No corresponding application could be found.");
        }

        if (!taCommunicator.addTaToCourse(selectInfo, token)) {
            throw new InvalidApplicationException("TA could not be saved to database.");
        }

        // Update the application in the database
        application.get().setStatus(Status.APPROVED);
        applicationRepository.save(application.get());

        // Send notification to user
        notificationCommunicator.sendNotification(selectInfo, token);

        return application.get();
    }
}
