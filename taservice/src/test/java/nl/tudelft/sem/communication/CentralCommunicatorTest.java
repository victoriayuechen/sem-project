package nl.tudelft.sem.communication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import nl.tudelft.sem.AverageWorkload;
import nl.tudelft.sem.SelectInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

public class CentralCommunicatorTest {
    private final transient CentralCommunicator centralCommunicator = new CentralCommunicator();

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
        centralCommunicator.setClient(mockClient);
    }

    @Test
    public void addTaRoleTest() throws IOException, InterruptedException {
        when(mockClient.send(any(HttpRequest.class),
            any(HttpResponse.BodyHandlers.ofString().getClass())))
            .thenReturn(mockResponse);
        when(mockResponse.statusCode())
            .thenReturn(HttpStatus.OK.value());
        assertTrue(centralCommunicator.addTaRole("ljpdeswart", TOKEN));
    }

    @Test
    public void getAverageHoursCourseTest() throws IOException, InterruptedException {
        when(mockClient.send(any(HttpRequest.class),
            any(HttpResponse.BodyHandlers.ofString().getClass())))
            .thenReturn(mockResponse);
        when(mockResponse.statusCode())
            .thenReturn(HttpStatus.OK.value());
        AverageWorkload averageWorkload = new AverageWorkload();
        averageWorkload.setAverageHours(20);
        when(mockResponse.body())
            .thenReturn(new ObjectMapper().writeValueAsString(averageWorkload));
        assertEquals(20, centralCommunicator.getAverageHoursCourse("CSE2115", TOKEN));
    }

    @Test
    public void getAverageHoursCourseExceptionTest() throws IOException, InterruptedException {
        when(mockClient.send(any(HttpRequest.class),
            any(HttpResponse.BodyHandlers.ofString().getClass())))
            .thenReturn(mockResponse);
        when(mockResponse.statusCode())
            .thenReturn(HttpStatus.BAD_REQUEST.value());
        AverageWorkload averageWorkload = new AverageWorkload();
        when(mockResponse.body())
            .thenReturn(new ObjectMapper().writeValueAsString(averageWorkload));
        assertThrows(IOException.class, () ->
            centralCommunicator.getAverageHoursCourse("CSE2115", TOKEN));
    }

    @Test
    public void sendNotificationTest() throws IOException, InterruptedException {
        when(mockClient.send(any(HttpRequest.class),
            any(HttpResponse.BodyHandlers.ofString().getClass())))
            .thenReturn(mockResponse);
        when(mockResponse.statusCode())
            .thenReturn(HttpStatus.OK.value());
        SelectInfo selectInfo = new SelectInfo();
        centralCommunicator.sendNotification(selectInfo, TOKEN);
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
            centralCommunicator.sendNotification(selectInfo, TOKEN);
        });
    }
}
