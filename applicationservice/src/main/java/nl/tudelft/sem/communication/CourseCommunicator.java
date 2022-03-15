package nl.tudelft.sem.communication;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import nl.tudelft.sem.Gateway;
import nl.tudelft.sem.SelectInfo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class CourseCommunicator {
    private transient HttpClient client = HttpClient.newBuilder().build();

    /**
     * Obtains the quarter for the course.
     *
     * @param courseCode The course code of the course.
     * @param token The security token of the HTTP-request.
     * @return The quarter during which this course is held.
     */
    public Integer obtainCourseQuarter(String courseCode, String token)
        throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(Gateway.COURSE_URL + "/getCourseQuarter/"
                + courseCode))
            .header("Authorization", token)
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != HttpStatus.OK.value()) {
            throw new IOException();
        }

        return Integer.parseInt(response.body());
    }

    /**
     * Checks whether the course is still open for TA recruitment. Allows the
     * lecturer to know if selecting another TA is necessary.
     *
     * @param selectInfo The selection information.
     * @return True if the course is still open, false otherwise.
     */
    public boolean courseOpenForRecruitment(SelectInfo selectInfo, String token)
        throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(Gateway.COURSE_URL + "/courseOpen/"
                + selectInfo.getCourseCode()))
            .header("Authorization", token)
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != HttpStatus.OK.value()) {
            throw new IOException();
        }

        return Boolean.parseBoolean(response.body());
    }

    /**
     * Retrieves a grade, given the user and course.
     *
     * @param courseCode The course code of the course.
     * @param username  The username of the user.
     * @param token     The security token of the HTTP-request.
     * @return The value of the grade, double.
     */
    public Double getGradeForCourse(String courseCode, String username, String token)
            throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(Gateway.COURSE_URL + "/courseGrade/"
                + courseCode
                + "/" + username))
            .header("Authorization", token)
            .build();

        HttpResponse<String> response = client
            .send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != HttpStatus.OK.value()) {
            throw new IOException();
        }

        return Double.parseDouble(response.body());
    }

    public void setClient(HttpClient client) {
        this.client = client;
    }
}
