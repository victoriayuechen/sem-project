package nl.tudelft.sem.util;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import nl.tudelft.sem.communication.TaCommunicator;
import nl.tudelft.sem.entities.Application;
import org.springframework.beans.factory.annotation.Autowired;

public class ExperienceRecommendation implements Recommendation {
    @Autowired
    private transient TaCommunicator taCommunicator;
    private transient List<Application> applications;
    private transient String token;

    /**
     * Constructor of the experience recommendation.
     *
     * @param applications      The list of applications.
     * @param taCommunicator    The TA communicator.
     * @param token             The security token of the HTTP-request.
     */
    public ExperienceRecommendation(List<Application> applications,
                                    TaCommunicator taCommunicator, String token) {
        this.applications = applications;
        this.taCommunicator = taCommunicator;
        this.token = token;
    }

    private int experienceCount(String username, String token)  {
        try {
            int size = taCommunicator.obtainExperiences(username, token).size();
            return size;
        } catch (IOException | InterruptedException e) {
            return 0;
        }
    }

    @Override
    public List<Application> recommend(String token) {
        applications.sort(Comparator.comparingInt(i -> -experienceCount(i.getUsername(), token)));
        return applications;
    }
}

