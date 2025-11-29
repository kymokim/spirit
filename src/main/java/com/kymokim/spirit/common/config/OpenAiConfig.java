package com.kymokim.spirit.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.time.Duration;

@Getter
@Setter
@Configuration
@PropertySource("classpath:/secret/api-key.properties")
@ConfigurationProperties(prefix = "openai")
public class OpenAiConfig {
    @Value("${openai.api.key}")
    private String apiKey;
    @Value("${openai.prompt.id}")
    private String promptId;
    private String model = "gpt-4o-mini";
    private String baseUrl = "https://api.openai.com/v1";
    private Duration timeout = Duration.ofSeconds(15);
}
