package wkallil.card.creditCardManager.config;

import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Credit Card Manager API",
                version = "0.0.1",
                description = "Api for managing credit cards"
        )
)
public class OpenApiConfig {
}

