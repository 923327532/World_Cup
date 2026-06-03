package notification_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "brevo")
@Data
public class BrevoConfig {
    private Api api = new Api();
    private Sender sender = new Sender();

    @Data
    public static class Api {
        private String key;
        private String url;
    }

    @Data
    public static class Sender {
        private String email;
        private String name;
    }
}
