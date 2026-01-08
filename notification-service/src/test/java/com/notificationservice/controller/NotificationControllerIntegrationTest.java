package com.notificationservice.controller;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.notificationservice.config.TestMailConfig;
import javax.mail.internet.MimeMessage;
import javax.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "spring.mail.host=localhost",
    "spring.mail.port=3025",
    "spring.mail.username=test@test.com",
    "spring.mail.password=test",
    "spring.mail.properties.mail.smtp.auth=false",
    "spring.mail.properties.mail.smtp.starttls.enable=false"
})
@AutoConfigureWebMvc
@Import(TestMailConfig.class)
class NotificationControllerIntegrationTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test@test.com", "test"))
            .withPerMethodLifecycle(false);

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        greenMail.reset();
    }

    @Test
    void testSendEmail_CreateOperation() throws Exception {
        // Given
        String jsonRequest = """
            {
                "operation": "CREATE",
                "email": "user@example.com"
            }
            """;

        // When & Then
        mockMvc.perform(post("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk());

        // Проверка отправки письма
        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertEquals(1, messages.length, "Должно быть отправлено одно письмо");
        assertEquals("Создание аккаунта", messages[0].getSubject());
    }

    @Test
    void testSendEmail_DeleteOperation() throws Exception {
        // Given
        String jsonRequest = """
            {
                "operation": "DELETE",
                "email": "user@example.com"
            }
            """;

        // When & Then
        mockMvc.perform(post("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk());

        // Проверка отправки письма
        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertEquals(1, messages.length, "Должно быть отправлено одно письмо");
        assertEquals("Удаление аккаунта", messages[0].getSubject());
    }

    @Test
    void testSendEmail_InvalidOperation() throws Exception {
        // Given
        String jsonRequest = """
            {
                "operation": "INVALID",
                "email": "user@example.com"
            }
            """;

        // When & Then
        mockMvc.perform(post("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest());

        // Проверка, что письмо не было отправлено
        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertEquals(0, messages.length, "Письмо не должно быть отправлено");
    }

    @Test
    void testSendEmail_MissingEmail() throws Exception {
        // Given
        String jsonRequest = """
            {
                "operation": "CREATE"
            }
            """;

        // When & Then
        mockMvc.perform(post("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSendEmail_EmptyEmail() throws Exception {
        // Given
        String jsonRequest = """
            {
                "operation": "CREATE",
                "email": ""
            }
            """;

        // When & Then
        mockMvc.perform(post("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }
}

