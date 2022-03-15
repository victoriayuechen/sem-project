package nl.tudelft.sem.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nl.tudelft.sem.Criteria;
import nl.tudelft.sem.Status;
import nl.tudelft.sem.communication.TaCommunicator;
import nl.tudelft.sem.entities.Application;
import nl.tudelft.sem.exceptions.EmptyTargetElementException;
import nl.tudelft.sem.repositories.ApplicationRepository;
import nl.tudelft.sem.util.ExperienceRecommendation;
import nl.tudelft.sem.util.GradeRecommendation;
import nl.tudelft.sem.util.RatingRecommendation;
import nl.tudelft.sem.util.Recommendation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ApplicationDataService {
    @Autowired
    private transient ApplicationRepository applicationRepository;

    @Autowired
    private transient TaCommunicator taCommunicator;

    public ApplicationDataService(ApplicationRepository applicationRepository,
                                  TaCommunicator taCommunicator) {
        this.applicationRepository = applicationRepository;
        this.taCommunicator = taCommunicator;
    }

    /**
     * Obtains the application data associated with an id.
     *
     * @param applicationId The application id of the application in question.
     * @return The data for of the user from this application.
     */
    public HashMap<String, Object> obtainApplicationData(Long applicationId, String token)
            throws IOException, InterruptedException, EmptyTargetElementException {
        Optional<Application> optApplication = applicationRepository
                .findApplicationsByApplicationId(applicationId);
        if (optApplication.isEmpty()) {
            throw new EmptyTargetElementException("No corresponding application could be found.");
        }
        Application application = optApplication.get();

        //Gets the experiences and combines it with the grade to send in the response.
        List<String> userExperiences = taCommunicator
                .obtainExperiences(application.getUsername(), token);
        HashMap<String, Object> hmap = new HashMap<String, Object>();
        hmap.put("grade", application.getGrade());
        hmap.put("experience", userExperiences);
        return hmap;
    }

    /**
     * Recommends potential candidates.
     *
     * @param courseCode The course code for which to run the recommendation algorithm.
     * @param criteria   A list of criteria to recommend on.
     * @param token      Security token of the HTTP-request.
     * @return The list of recommendations.
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public List<Application> recommendApplicants(String courseCode, List<Criteria> criteria,
                                                 String token)
            throws IOException, InterruptedException, EmptyTargetElementException {
        // Obtain list of applications
        List<Application> applications = obtainOpenApplications(courseCode);
        // Use default recommendation algorithm.
        for (Criteria criterion : criteria) {
            Recommendation recommendation = new GradeRecommendation(applications);
            if (criterion == Criteria.GRADE) {
                recommendation = new GradeRecommendation(applications);
            } else if (criterion == Criteria.EXPERIENCE) {
                recommendation =
                        new ExperienceRecommendation(applications, taCommunicator, token);
            } else if (criterion == Criteria.RATING) {
                recommendation = new RatingRecommendation(applications, taCommunicator, token);
            }
            applications = recommendation.recommend(token);
        }
        return applications;
    }

    private List<Application> obtainOpenApplications(String courseCode) {

        return applicationRepository
                .findApplicationsByCourseCode(courseCode)
                .stream()
                .filter(a -> a.getStatus() == Status.PENDING)
                .collect(Collectors.toList());
    }
}
