package nl.tudelft.sem.communication;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.List;
import nl.tudelft.sem.Gateway;
import nl.tudelft.sem.SelectInfo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class TaCommunicator {
    private transient HttpClient client = HttpClient.newBuilder().build();
    private transient Gson gson = new Gson();

    /**
     * Obtains the experiences for the ta.
     *
     * @param userName The TA's username.
     * @return The experiences of the ta as a list of strings.
     */
    public List<String> obtainExperiences(String userName, String token)
        throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(Gateway.CONTRACT_URL + "/getExperiences/"
                        + userName))
                .header("Authorization", token)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != HttpStatus.OK.value()) {
            throw new IOException();
        }

        Type collectionType = new TypeToken<Collection<String>>(){}.getType();
        return gson.fromJson(response.body(), collectionType);
    }

    /**
     * Obtains the ratings for the ta.
     *
     * @param userName The TA's username.
     * @return The ratings of the ta as a list of integers.
     */
    public List<Integer> obtainRatings(String userName, String token)
            throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(Gateway.REVIEW_URL + "/getRatings/user/"
                        + userName))
                .header("Authorization", token)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != HttpStatus.OK.value()) {
            throw new IOException();
        }

        Type collectionType = new TypeToken<Collection<Integer>>(){}.getType();
        return gson.fromJson(response.body(), collectionType);
    }

    /**
     * Saves a new TA to the TA microservice database.
     *
     * @param selectInfo The selection information related to this application.
     * @return True if the TA was saved to database successfully, false otherwise.
     */
    public Boolean addTaToCourse(SelectInfo selectInfo, String token)
            throws IOException, InterruptedException {
        String bodyRequest = gson.toJson(selectInfo);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(Gateway.TA_URL + "/save-ta"))
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .POST(HttpRequest.BodyPublishers.ofString(bodyRequest))
                .build();

        HttpResponse<String> response = client
            .send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != HttpStatus.OK.value()) {
            throw new IOException();
        }

        return Boolean.parseBoolean(response.body());
    }

    public void setClient(HttpClient client) {
        this.client = client;
    }
}
