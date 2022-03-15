package nl.tudelft.sem.communication;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import nl.tudelft.sem.Gateway;
import nl.tudelft.sem.NotificationMessage;
import nl.tudelft.sem.SelectInfo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class NotificationCommunicator {
    private transient HttpClient client = HttpClient.newBuilder().build();
    private transient Gson gson = new Gson();

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
            throw new IOException();
        }
    }

    public void setClient(HttpClient client) {
        this.client = client;
    }
}
