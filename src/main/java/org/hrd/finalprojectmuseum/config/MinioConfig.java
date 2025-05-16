package org.hrd.finalprojectmuseum.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    private final String url = "http://localhost:9000";
    private final String accessKey = "admin";
    private final String secretKey = "admin123";
    @Bean
    public MinioClient minioClient(){
        MinioClient minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
        return minioClient;
    }
}
