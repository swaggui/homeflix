package com.homeflix.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI homeFlixOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HomeFlix API")
                        .description("API REST para gerenciamento de catálogo pessoal de vídeos. Uma Netflix caseira para organizar seus vídeos baixados.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("HomeFlix")
                                .url("https://github.com/homeflix")));
    }
}
