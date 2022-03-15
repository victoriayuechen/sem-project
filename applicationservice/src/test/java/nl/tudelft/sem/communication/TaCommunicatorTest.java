package nl.tudelft.sem.communication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import nl.tudelft.sem.SelectInfo;
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
    public void obtainExperiencesTest() throws IOException, InterruptedException {
        List<String> list = List.of("Experience1", "Experience2");
        when(mockClient.send(any(HttpRequest.class),
                any(HttpResponse.BodyHandlers.ofString().getClass())))
                .thenReturn(mockResponse);
        when(mockResponse.statusCode())
                .thenReturn(HttpStatus.OK.value());
        when(mockResponse.body())
                .thenReturn(new Gson().toJson(list));
        assertEquals(list, taCommunicator.obtainExperiences("ljpdeswart", TOKEN));
    }

    @Test
    public void obtainExperiencesExceptionTest() throws IOException, InterruptedException {
        when(mockClient.send(any(HttpRequest.class),
                any(HttpResponse.BodyHandlers.ofString().getClass())))
                .thenReturn(mockResponse);
        when(mockResponse.statusCode())
                .thenReturn(HttpStatus.BAD_REQUEST.value());
        when(mockResponse.body())
                .thenReturn("Exception");
        assertThrows(IOException.class, () ->
                taCommunicator.obtainExperiences("ljpdeswart", TOKEN));
    }

    @Test
    public void obtainRatingsTest() throws IOException, InterruptedException {
        List<Integer> list = List.of(3, 4);
        when(mockClient.send(any(HttpRequest.class),
                any(HttpResponse.BodyHandlers.ofString().getClass())))
                .thenReturn(mockResponse);
        when(mockResponse.statusCode())
                .thenReturn(HttpStatus.OK.value());
        when(mockResponse.body())
                .thenReturn(new Gson().toJson(list));
        assertEquals(list, taCommunicator.obtainRatings("ljpdeswart", TOKEN));
    }

    @Test
    public void obtainRatingsExceptionTest() throws IOException, InterruptedException {
        when(mockClient.send(any(HttpRequest.class),
                any(HttpResponse.BodyHandlers.ofString().getClass())))
                .thenReturn(mockResponse);
        when(mockResponse.statusCode())
                .thenReturn(HttpStatus.BAD_REQUEST.value());
        when(mockResponse.body())
                .thenReturn("Exception");
        assertThrows(IOException.class, () ->
                taCommunicator.obtainRatings("ljpdeswart", TOKEN));
    }

    @Test
    public void addTaToCourseTestTrue() throws IOException, InterruptedException {
        when(mockClient.send(any(HttpRequest.class),
                any(HttpResponse.BodyHandlers.ofString().getClass())))
                .thenReturn(mockResponse);
        when(mockResponse.statusCode())
                .thenReturn(HttpStatus.OK.value());
        when(mockResponse.body())
                .thenReturn("true");
        SelectInfo selectInfo = new SelectInfo();
        assertTrue(taCommunicator.addTaToCourse(selectInfo, TOKEN));
    }

    @Test
    public void addTaToCourseTestFalse() throws IOException, InterruptedException {
        when(mockClient.send(any(HttpRequest.class),
                any(HttpResponse.BodyHandlers.ofString().getClass())))
                .thenReturn(mockResponse);
        when(mockResponse.statusCode())
                .thenReturn(HttpStatus.OK.value());
        when(mockResponse.body())
                .thenReturn("false");
        SelectInfo selectInfo = new SelectInfo();
        assertFalse(taCommunicator.addTaToCourse(selectInfo, TOKEN));
    }

    @Test
    public void addTaToCourseExceptionTest() throws IOException, InterruptedException {
        when(mockClient.send(any(HttpRequest.class),
                any(HttpResponse.BodyHandlers.ofString().getClass())))
                .thenReturn(mockResponse);
        when(mockResponse.statusCode())
                .thenReturn(HttpStatus.BAD_REQUEST.value());
        when(mockResponse.body())
                .thenReturn("Exception");

        assertThrows(IOException.class, () -> {
            SelectInfo selectInfo = new SelectInfo();
            taCommunicator.addTaToCourse(selectInfo, TOKEN);
        });
    }
}
