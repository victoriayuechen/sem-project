package nl.tudelft.sem.communicators;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import nl.tudelft.sem.Gateway;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class TaCommunicator {
    private transient HttpClient client = HttpClient.newBuilder().build();
    private transient Gson gson = new Gson();

    /**
     * Get the work load hours of a course from the TA-service.
     *
     * @param courseCode    The course code of the course.
     * @param token         The security token of the HTTP-request.
     * @return A list of all the work load hours of a course.
     * @throws IOException  In case the other service breaks.
     * @throws InterruptedException In case the HTTP-request gets interrupted.
     */
    public List<Integer> obtainWorkLoadHours(String courseCode, String token)
        throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
            .newBuilder()
            .GET()
            .uri(URI.create(Gateway.WORKLOAD_URL + "/workload-hours/" + courseCode))
                .header("Authorization", token)
            .build();

        HttpResponse<String> response = client.send(request,
            HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != HttpStatus.OK.value()) {
            throw new IOException("Unable to fetch work hours.");
        }
        Type collectionType = new TypeToken<List<Integer>>(){}.getType();
        return gson.fromJson(response.body(), collectionType);
    }

    /**
     * Get the amount of times a student has been a TA before from the TA-service.
     *
     * @param courseCode    The course code of the course.
     * @param token         The security token of the HTTP-request.
     * @return The amount of times a student has been a TA before.
     * @throws IOException  In case the TA-service breaks.
     * @throws InterruptedException In case the HTTP-request gets interrupted.
     */
    public Integer taCount(String courseCode, String token)
        throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
            .newBuilder()
            .GET()
            .uri(URI.create(Gateway.CONTRACT_URL + "/countTa/" + courseCode))
                .header("Authorization", token)
            .build();

        HttpResponse<String> response = client
            .send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != HttpStatus.OK.value()) {
            throw new IOException("Unable to fetch TA count.");
        }
        return Integer.parseInt(response.body());
    }

    public void setClient(HttpClient client) {
        this.client = client;
    }
}

