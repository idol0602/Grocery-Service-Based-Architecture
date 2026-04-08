package nguyennhatquan.springbootreview.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConfigurationProperties(prefix = "momo")
@Data
public class MomoConfig {
    private String partnerCode;
    private String accessKey;
    private String secretKey;
    private String endpoint;
    private String redirectURL;
    private String ipnUrl;
    private String requestType;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
