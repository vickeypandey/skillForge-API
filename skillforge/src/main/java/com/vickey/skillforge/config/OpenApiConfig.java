package com.vickey.skillforge.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI skillForgeOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("SkillForge API")
                        .version("1.0")
                        .description("Backend APIs for SkillForge - a peer-to-peer skill exchange platform"));
    }
}