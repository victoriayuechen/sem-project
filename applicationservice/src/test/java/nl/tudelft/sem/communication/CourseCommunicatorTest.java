package nl.tudelft.sem.communication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import nl.tudelft.sem.ApplyInfo;
import nl.tudelft.sem.SelectInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

public class CourseCommunicatorTest {
    private final transient CourseCommunicator courseCommunicator = new CourseCommunicator();

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
        courseCommunicator.setClient(mockClient);
    }

    @Test
    public void obtainQuarterTest() throws IOException, InterruptedException {
        when(mockClient.send(any(HttpRequest.class),
                any(HttpResponse.BodyHandlers.ofString().getClass())))
                .thenReturn(mockResponse);
        when(mockResponse.statusCode())
                .thenReturn(HttpStatus.OK.value());
        when(mockResponse.body())
                .thenReturn("5");
        ApplyInfo applyInfo = new ApplyInfo();
        assertEquals(5, courseCommunicator.obtainCourseQuarter(applyInfo.getCourseCode(), TOKEN));
    }

    @Test
    public void obtainQuarterExceptionTest() throws IOException, InterruptedException {
        when(mockClient.send(any(HttpRequest.class),
                any(HttpResponse.BodyHandlers.ofString().getClass())))
                .thenReturn(mockResponse);
        when(mockResponse.statusCode())
                .thenReturn(HttpStatus.BAD_REQUEST.value());
        when(mockResponse.body())
                .thenReturn("Exception");

        assertThrows(IOException.class, () -> {
            ApplyInfo applyInfo = new ApplyInfo();
            courseCommunicator.obtainCourseQuarter(applyInfo.getCourseCode(), TOKEN);
        });
    }

    @Test
    public void courseOpenForRecruitmentTrueTest() throws IOException, InterruptedException {
        when(mockClient.send(any(HttpRequest.class),
                any(HttpResponse.BodyHandlers.ofString().getClass())))
                .thenReturn(mockResponse);
        when(mockResponse.statusCode())
                .thenReturn(HttpStatus.OK.value());
        when(mockResponse.body())
                .thenReturn("true");
        SelectInfo selectInfo = new SelectInfo();
        assertTrue(courseCommunicator.courseOpenForRecruitment(selectInfo, TOKEN));
    }

    @Test
    public void courseOpenForRecruitmentFalseTest() throws IOException, InterruptedException {
        when(mockClient.send(any(HttpRequest.class),
                any(HttpResponse.BodyHandlers.ofString().getClass())))
                .thenReturn(mockResponse);
        when(mockResponse.statusCode())
                .thenReturn(HttpStatus.OK.value());
        when(mockResponse.body())
                .thenReturn("false");
        SelectInfo selectInfo = new SelectInfo();
        assertFalse(courseCommunicator.courseOpenForRecruitment(selectInfo, TOKEN));
    }

    @Test
    public void courseOpenForRecruitmentExceptionTest() throws IOException, InterruptedException {
        when(mockClient.send(any(HttpRequest.class),
                any(HttpResponse.BodyHandlers.ofString().getClass())))
                .thenReturn(mockResponse);
        when(mockResponse.statusCode())
                .thenReturn(HttpStatus.BAD_REQUEST.value());
        when(mockResponse.body())
                .thenReturn("Exception");

        assertThrows(IOException.class, () -> {
            SelectInfo selectInfo = new SelectInfo();
            courseCommunicator.courseOpenForRecruitment(selectInfo, TOKEN);
        });
    }

    @Test
    public void getGradeForCourseTest() throws IOException, InterruptedException {
        when(mockClient.send(any(HttpRequest.class),
                any(HttpResponse.BodyHandlers.ofString().getClass())))
                .thenReturn(mockResponse);
        when(mockResponse.statusCode())
                .thenReturn(HttpStatus.OK.value());
        when(mockResponse.body())
                .thenReturn("5.0");
        ApplyInfo applyInfo = new ApplyInfo();
        assertEquals(5.0, courseCommunicator
                .getGradeForCourse(applyInfo.getCourseCode(), applyInfo.getUsername(), TOKEN));
    }

    @Test
    public void getGradeForCourseExceptionTest() throws IOException, InterruptedException {
        when(mockClient.send(any(HttpRequest.class),
                any(HttpResponse.BodyHandlers.ofString().getClass())))
                .thenReturn(mockResponse);
        when(mockResponse.statusCode())
                .thenReturn(HttpStatus.BAD_REQUEST.value());
        when(mockResponse.body())
                .thenReturn("Exception");
        assertThrows(IOException.class, () -> {
            ApplyInfo applyInfo = new ApplyInfo();
            courseCommunicator.getGradeForCourse(applyInfo.getCourseCode(),
                    applyInfo.getUsername(), TOKEN);
        });
    }
}
