package notification_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final notification_service.config.BrevoConfig brevoConfig;

    public void sendEmail(String to, String subject, String body) {
        sendViaSmtp(to, subject, body);
        sendViaApi(to, subject, body);
    }

    private void sendViaSmtp(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom(brevoConfig.getSender().getEmail());
            mailSender.send(message);
            log.info("Email sent via SMTP to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email via SMTP to {}: {}", to, e.getMessage());
        }
    }

    private void sendViaApi(String to, String subject, String body) {
        try {
            String jsonPayload = String.format(
                """
                {
                    "sender": {"email": "%s", "name": "%s"},
                    "to": [{"email": "%s"}],
                    "subject": "%s",
                    "htmlContent": "<html><body><p>%s</p></body></html>"
                }
                """,
                brevoConfig.getSender().getEmail(),
                brevoConfig.getSender().getName(),
                to,
                escapeJson(subject),
                escapeJson(body)
            );

            URL url = URI.create(brevoConfig.getApi().getUrl()).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("api-key", brevoConfig.getApi().getKey());
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                log.info("Email sent via Brevo API to {}", to);
            } else {
                log.warn("Brevo API returned response code {} for {}", responseCode, to);
            }
        } catch (Exception e) {
            log.error("Failed to send email via Brevo API to {}: {}", to, e.getMessage());
        }
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
}
