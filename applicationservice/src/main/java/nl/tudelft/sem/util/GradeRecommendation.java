package nl.tudelft.sem.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import nl.tudelft.sem.entities.Application;

public class GradeRecommendation implements Recommendation {
    private transient List<Application> applications;

    public GradeRecommendation(List<Application> applications) {
        this.applications = applications;
    }

    @Override
    public List<Application> recommend(String token) {
        Comparator<Application> comparator = Collections.reverseOrder(Comparator
                .comparing(Application::getGrade));
        applications.sort(comparator);
        return applications;
    }
}
