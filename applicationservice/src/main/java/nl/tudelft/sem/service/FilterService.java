package nl.tudelft.sem.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import nl.tudelft.sem.SelectInfo;
import nl.tudelft.sem.Status;
import nl.tudelft.sem.communication.NotificationCommunicator;
import nl.tudelft.sem.communication.TaCommunicator;
import nl.tudelft.sem.entities.Application;
import nl.tudelft.sem.exceptions.EmptyTargetElementException;
import nl.tudelft.sem.repositories.ApplicationRepository;
import nl.tudelft.sem.util.FilterParameters;
import nl.tudelft.sem.util.GradeRecommendation;
import nl.tudelft.sem.util.Recommendation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FilterService {
    @Autowired
    private transient ApplicationRepository applicationRepository;
    @Autowired
    private transient TaCommunicator taCommunicator;
    @Autowired
    private transient NotificationCommunicator notificationCommunicator;

    /**
     * Constructor for the filter service.
     *
     * @param applicationRepository     The application repository.
     * @param taCommunicator            The TA communicator.
     * @param notificationCommunicator  The notificaion communicator.
     */
    public FilterService(ApplicationRepository applicationRepository,
                              TaCommunicator taCommunicator,
                              NotificationCommunicator notificationCommunicator) {
        this.applicationRepository = applicationRepository;
        this.taCommunicator = taCommunicator;
        this.notificationCommunicator = notificationCommunicator;
    }

    /**
     * Rejects applications that do not meet a certain set of requirements.
     *
     * @param courseCode   Course code of the course.
     * @param filter       Combination of all filter parameters
     *                     (minGrade, minRating, minAvgRating, minReqTa).
     *      minGrade     Minimum grade to filter on.
     *      minRating    Minimum rating to filter on.
     *      minAvgRating Minimum average rating to filter on.
     *      minReqTa     Minimum amount of times a TA must have been a TA before on other
     *                     courses to filter on.
     */
    public void autoReject(String courseCode, FilterParameters filter, String token)
            throws EmptyTargetElementException, IOException,
            NumberFormatException, InterruptedException {
        if (filter == null) {
            throw new EmptyTargetElementException("Filter cannot be null.");
        }
        List<Application> applications = obtainOpenApplications(courseCode);
        List<Application> checkList = new ArrayList<>(applications);
        List<Application> finalApplications =
                filterOnParams(applications, filter, taCommunicator, token);
        if (finalApplications.size() == checkList.size()) {
            return;
        }
        for (Application application : checkList) {
            if (!finalApplications.contains(application)) {
                application.setStatus(Status.REJECTED);
                applicationRepository.save(application);
                SelectInfo selectInfo = new SelectInfo(application.getUsername(),
                        application.getCourseCode(), Status.REJECTED);
                notificationCommunicator.sendNotification(selectInfo, token);
            }
        }
    }

    /**
     * Shows the standard recommendation but filtered on certain parameters.
     *
     * @param courseCode   Course code of the course.
     * @param filter       Combination of all filter parameters (minGrade, minRating,
     *                     minAvgRating, minReqTa).
     *      minGrade     Minimum grade to filter on.
     *      minRating    Minimum rating to filter on.
     *      minAvgRating Minimum average rating to filter on.
     *      minReqTa     Minimum amount of times a TA must have been a TA before on other
     *                     courses to filter on.
     * @return filtered list of applications.
     */
    public Recommendation applyAlgorithm(String courseCode, FilterParameters filter, String token)
            throws EmptyTargetElementException, NumberFormatException {
        List<Application> applications = obtainOpenApplications(courseCode);
        if (applications.isEmpty()) {
            throw new EmptyTargetElementException("No open applications with given course code");
        }
        applications = filterOnParams(applications, filter, taCommunicator, token);
        return new GradeRecommendation(applications);
    }

    private List<Application> filterOnParams(List<Application> list,
                                             FilterParameters filter, TaCommunicator taCommunicator,
                                             String token) throws NumberFormatException {
        List<Application> applications = new ArrayList<>(list);
        applications = filterOnGrade(applications, filter.getMinGrade());
        applications = filterOnRating(applications, filter.getMinRating(), taCommunicator, token);
        applications = filterOnAvgRating(applications, filter.getMinAvgRating(),
                taCommunicator, token);
        applications = filterOnAmountTa(applications, filter.getMinReqTa(), taCommunicator, token);
        return applications;
    }

    private List<Application> filterOnGrade(List<Application> list,
                                            String minGrade) throws NumberFormatException {
        if (minGrade == null) {
            return list;
        }
        List<Application> applications = new ArrayList<>(list);
        Double.parseDouble(minGrade);
        return applications.stream().filter(application -> {
            double grade = Double.parseDouble(minGrade);
            return application.getGrade() >= grade;
        }).collect(Collectors.toList());
    }

    private List<Application> filterOnRating(List<Application> list, String minRating,
                                             TaCommunicator taCommunicator, String token)
            throws NumberFormatException {
        List<Application> applications = new ArrayList<>(list);
        if (minRating == null) {
            return applications;
        }
        return applications.stream().filter(application -> {
            int rating = Integer.parseInt(minRating);
            try {
                return taCommunicator.obtainRatings(application.getUsername(), token)
                        .stream().allMatch(integer -> integer >= rating);
            } catch (IOException | InterruptedException e) {
                return false;
            }
        }).collect(Collectors.toList());
    }

    private List<Application> filterOnAvgRating(List<Application> list,
                                                String minAvgRating, TaCommunicator taCommunicator,
                                                String token) throws NumberFormatException {
        List<Application> applications = new ArrayList<>(list);
        if (minAvgRating == null) {
            return applications;
        }
        return applications.stream().filter(application -> {
            double avgRating = Double.parseDouble(minAvgRating);
            try {
                List<Integer> ratings = taCommunicator
                        .obtainRatings(application.getUsername(), token);
                return (double) ratings.stream()
                        .reduce(Integer::sum).get() / (double) ratings.size() >= avgRating;
            } catch (IOException | InterruptedException e) {
                return false;
            } catch (NoSuchElementException e) {
                return true;
            }
        }).collect(Collectors.toList());
    }

    private List<Application> filterOnAmountTa(List<Application> list,
                                               String minReqTa, TaCommunicator taCommunicator,
                                               String token) throws NumberFormatException {
        List<Application> applications = new ArrayList<>(list);
        if (minReqTa == null) {
            return applications;
        }
        return applications.stream().filter(application -> {
            int reqTa = Integer.parseInt(minReqTa);
            try {
                return taCommunicator.obtainExperiences(application.getUsername(), token).size()
                        >= reqTa;
            } catch (IOException | InterruptedException e) {
                return false;
            }
        }).collect(Collectors.toList());
    }

    private List<Application> obtainOpenApplications(String courseCode) {

        return applicationRepository
                .findApplicationsByCourseCode(courseCode)
                .stream()
                .filter(a -> a.getStatus() == Status.PENDING)
                .collect(Collectors.toList());
    }
}
