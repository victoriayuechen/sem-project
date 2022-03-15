package nl.tudelft.sem.communication;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import nl.tudelft.sem.AverageWorkload;
import nl.tudelft.sem.Gateway;
import nl.tudelft.sem.NotificationMessage;
import nl.tudelft.sem.SelectInfo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class CentralCommunicator {
    private transient HttpClient client = HttpClient.newBuilder().build();
    private transient Gson gson = new Gson();

    public void setClient(HttpClient client) {
        this.client = client;
    }

    /**
     * This adds the TA role to a newly selected role.
     *
     * @param username The username of the student who will now be TA.
     * @param token The authorization token associated with this request.
     * @return True if the role is added successfully, false otherwise.
     * @throws IOException Exception when something goes wrong
     * @throws InterruptedException Exception when something goes wrong
     */
    public boolean addTaRole(String username, String token)
        throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
            .newBuilder()
            .PUT(HttpRequest.BodyPublishers.ofString(""))
            .header("Authorization", token)
            .uri(URI.create(Gateway.AUTH_URL + "add-role-ta/" + username))
            .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.statusCode() == HttpStatus.OK.value();
    }

    /**
     * Creates a http request to get the starting date of a course.
     *
     * @param courseCode    Course code of the course.
     * @return the date.
     */
    public int getAverageHoursCourse(String courseCode, String token)
        throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(Gateway.COURSE_URL + "course/averageWorkload/"
                + courseCode))
            .header("Authorization", token)
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != HttpStatus.OK.value()) {
            System.out.println(response.body());
            throw new IOException();
        }
        return gson.fromJson(response.body(), AverageWorkload.class).getAverageHours();
    }

    /**
     * Sends a notification to user when the status of the application is updated.
     *
     * @param selectInfo The selection information result.
     */
    public void sendNotification(SelectInfo selectInfo, String token)
        throws IOException, InterruptedException {
        String text = "Newest update for " + selectInfo.getCourseCode()
            + " is " + selectInfo.getStatus();
        NotificationMessage notification = new NotificationMessage();
        notification.setText(text);
        notification.setUsername(selectInfo.getUsername());
        String bodyRequest = gson.toJson(notification);

        HttpRequest request = HttpRequest
            .newBuilder()
            .uri(URI.create(Gateway.NOTF_URL + "/create_notification"))
            .header("Content-Type", "application/json")
            .header("Authorization", token)
            .POST(HttpRequest.BodyPublishers.ofString(bodyRequest))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != HttpStatus.OK.value()) {
            System.out.println(response.body());
            throw new IOException();
        }

    }

}
