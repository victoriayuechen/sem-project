package nl.tudelft.sem.util;

import java.util.List;
import nl.tudelft.sem.entities.Application;

public interface Recommendation {
    List<Application> recommend(String token);
}
