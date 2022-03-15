package nl.tudelft.sem.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import nl.tudelft.sem.ErrorString;
import nl.tudelft.sem.NotificationMessage;
import nl.tudelft.sem.entities.Notification;
import nl.tudelft.sem.entities.NotificationStatus;
import nl.tudelft.sem.repositories.NotificationRepository;
import nl.tudelft.sem.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(NotificationController.class)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class NotificationControllerTest {
    @MockBean
    private transient NotificationService notificationService;
    @Autowired
    private transient MockMvc mockMvc;

    private transient NotificationMessage notificationMessage;
    private transient Notification notification;

    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJz"
            + "dWIiOiJhbm5pYmFsZSIsImV4cCI6MTY0MDMw"
            + "NjkyMywiaWF0IjoxNjQwMjcwOTIzfQ.Ab3qsQdzo"
            + "U8viZwWtnFf9NqIG9GDsSssTxrjyXj_8Dg";

    /**
     * Sets up all the objects for testing before each test.
     */
    @BeforeEach
    public void setup() {
        notificationMessage = new NotificationMessage();
        notificationMessage.setText("Text1.");
        notificationMessage.setUsername("ljpdeswart");

        notification = new Notification();
        notification.setNotificationId(0);
        notification.setStatus(NotificationStatus.PENDING);
        notification.setText("Text1.");
        notification.setUsername("ljpdeswart");
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void addNotificationTest() throws Exception {
        mockMvc.perform(post("/notifications/create_notification")
                .header("Authorization", TOKEN)
                        .content(new ObjectMapper().writeValueAsString(notificationMessage))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Notification successfully created."
                ));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void addNotificationExceptionTest() throws Exception {
        doThrow(new IllegalArgumentException()).when(notificationService)
                .addNotification(notificationMessage);
        mockMvc.perform(post("/notifications/create_notification")
                .header("Authorization", TOKEN)
                .content(new ObjectMapper().writeValueAsString(notificationMessage))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(
                        new ErrorString("Something went wrong when fetching"
                                + " your notifications."))));
    }

    @WithMockUser(roles = "STUDENT")
    @Test
    public void getNotificationTest() throws Exception {
        when(notificationService.getNotifications(notification.getUsername()))
                .thenReturn(notification.getText());
        mockMvc.perform(get("/notifications/get_notifications/" + notification.getUsername())
            .header("Authorization", TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().string(notification.getText()));
    }
}