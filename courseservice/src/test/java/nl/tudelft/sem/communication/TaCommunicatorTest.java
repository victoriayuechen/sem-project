package nl.tudelft.sem.communication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import nl.tudelft.sem.communicators.TaCommunicator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class TaCommunicatorTest {
    private final transient TaCommunicator taCommunicator = new TaCommunicator();

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
        taCommunicator.setClient(mockClient);
    }

    @Test
    public void obtainWorkloadHoursTest() throws IOException, InterruptedException {
        List<Integer> list = List.of(3, 4);
        when(mockClient.send(any(HttpRequest.class),
                any(HttpResponse.BodyHandlers.ofString().getClass())))
                .thenReturn(mockResponse);
        when(mockResponse.statusCode())
                .thenReturn(HttpStatus.OK.value());
        when(mockResponse.body())
                .thenReturn(new Gson().toJson(list));
        assertEquals(list, taCommunicator.obtainWorkLoadHours("CSE2115", TOKEN));
    }

    @Test
    public void obtainWorkloadHoursExceptionTest() throws IOException, InterruptedException {
        when(mockClient.send(any(HttpRequest.class),
                any(HttpResponse.BodyHandlers.ofString().getClass())))
                .thenReturn(mockResponse);
        when(mockResponse.statusCode())
                .thenReturn(HttpStatus.BAD_REQUEST.value());
        when(mockResponse.body())
                .thenReturn("Exception");
        assertThrows(IOException.class, () ->
                taCommunicator.obtainWorkLoadHours("CSE2115", TOKEN));
    }

    @Test
    public void taCountTest() throws IOException, InterruptedException {
        when(mockClient.send(any(HttpRequest.class),
                any(HttpResponse.BodyHandlers.ofString().getClass())))
                .thenReturn(mockResponse);
        when(mockResponse.statusCode())
                .thenReturn(HttpStatus.OK.value());
        when(mockResponse.body())
                .thenReturn("4");
        assertEquals(4, taCommunicator.taCount("CSE2115", TOKEN));
    }

    @Test
    public void toCountExceptionTest() throws IOException, InterruptedException {
        when(mockClient.send(any(HttpRequest.class),
                any(HttpResponse.BodyHandlers.ofString().getClass())))
                .thenReturn(mockResponse);
        when(mockResponse.statusCode())
                .thenReturn(HttpStatus.BAD_REQUEST.value());
        when(mockResponse.body())
                .thenReturn("Exception");
        assertThrows(IOException.class, () ->
                taCommunicator.taCount("CSE2115", TOKEN));
    }
}
