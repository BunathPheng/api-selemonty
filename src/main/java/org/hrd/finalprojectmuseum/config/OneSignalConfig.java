package org.hrd.finalprojectmuseum.config;

import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "onesignal")
public class OneSignalConfig {

    private String appId;
    private String restApiKey;
    private String apiUrl;

}
