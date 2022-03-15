package nl.tudelft.sem.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.communication.TaCommunicator;
import nl.tudelft.sem.entities.Application;
import org.springframework.beans.factory.annotation.Autowired;

public class RatingRecommendation implements Recommendation {
    @Autowired
    private transient TaCommunicator taCommunicator;

    private transient List<Application> applications;
    private transient String token;

    /**
     * Constructor for the rating recommentdation.
     *
     * @param applications      The list of applications.
     * @param taCommunicator    The TA communicator.
     * @param token             The security token of the HTTP-request.
     */
    public RatingRecommendation(List<Application> applications,
                                TaCommunicator taCommunicator, String token) {
        this.applications = applications;
        this.taCommunicator = taCommunicator;
        this.token = token;
    }

    private double averageRating(String username, String token) {
        try {
            List<Integer> ratings = taCommunicator.obtainRatings(username, token);
            int count = ratings.size();
            Integer sum = ratings.stream().reduce(Integer::sum).get();
            return (double) sum / count;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    @Override
    public List<Application> recommend(String token) {
        applications.sort(Comparator.comparingDouble(i -> -averageRating(i.getUsername(), token)));
        return applications;
    }
}
