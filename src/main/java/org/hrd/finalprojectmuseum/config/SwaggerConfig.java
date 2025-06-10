package org.hrd.finalprojectmuseum.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Sela Monty API",
                version = "1.0.0",
                description = "API documentation for the Sela Monty application",
                contact = @Contact(
                        name = "GitHub",
                        url = "https://github.com/13-Generation-Basic-Course-Projects"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local Server"),
                @Server(url = "http://34.143.146.124:9125", description = "Host Server")
        }
)
public class SwaggerConfig {
}
