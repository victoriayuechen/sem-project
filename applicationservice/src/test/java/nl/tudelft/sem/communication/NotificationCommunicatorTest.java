package nl.tudelft.sem.communication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import nl.tudelft.sem.Gateway;
import nl.tudelft.sem.NotificationMessage;
import nl.tudelft.sem.SelectInfo;
import nl.tudelft.sem.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;



public class NotificationCommunicatorTest {
    private final transient NotificationCommunicator notificationCommunicator
            = new NotificationCommunicator();

    @Mock
    private transient HttpClient mockClient;
    @Mock
    private transient HttpResponse<String> mockResponse;

    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJz"
            + "dWIiOiJhbm5pYmFsZSIsImV4cCI6MTY0MDMw"
            + "NjkyMywiaWF0IjoxNjQwMjcwOTIzfQ.Ab3qsQdzo"
            + "U8viZwWtnFf9NqIG9GDsSssTxrjyXj_8Dg";

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        notificationCommunicator.setClient(mockClient);
    }

    @Test
    public void sendNotificationTest() throws IOException, InterruptedException {
        NotificationMessage notification = new NotificationMessage();
        notification.setText("Newest update for CSE2115 is APPROVED");
        notification.setUsername("Username username");
        HttpRequest.BodyPublisher notificationJson = HttpRequest.BodyPublishers.ofString(
                new Gson().toJson(notification));
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(Gateway.NOTF_URL + "/create_notification"))
                .header("Content-Type", "application/json")
                .header("Authorization", TOKEN)
                .POST(notificationJson)
                .build();
        when(mockClient.send(request,
                HttpResponse.BodyHandlers.ofString()))
                .thenReturn(mockResponse);
        when(mockResponse.statusCode())
                .thenReturn(HttpStatus.OK.value());
        SelectInfo selectInfo = new SelectInfo();
        selectInfo.setStatus(Status.APPROVED);
        selectInfo.setUsername("Username username");
        selectInfo.setCourseCode("CSE2115");
        notificationCommunicator.sendNotification(selectInfo, TOKEN);
        ArgumentCaptor<HttpRequest> argument = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockClient).send(argument.capture(),
                any(HttpResponse.BodyHandlers.ofString().getClass()));
        if (argument.getValue().bodyPublisher().isPresent()) {
            assertEquals(notificationJson.contentLength(),
                    argument.getValue().bodyPublisher().get().contentLength());
        }
    }

    @Test
    public void sendNotificationExceptionTest() throws IOException, InterruptedException {
        when(mockClient.send(any(HttpRequest.class),
                any(HttpResponse.BodyHandlers.ofString().getClass())))
                .thenReturn(mockResponse);
        when(mockResponse.statusCode())
                .thenReturn(HttpStatus.BAD_REQUEST.value());
        when(mockResponse.body())
                .thenReturn("Exception");

        assertThrows(IOException.class, () -> {
            SelectInfo selectInfo = new SelectInfo();
            notificationCommunicator.sendNotification(selectInfo, TOKEN);
        });
    }
}
