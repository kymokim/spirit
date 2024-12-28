package com.kymokim.spirit;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.services.s3.S3Client;

@TestConfiguration
public class S3MockConfig {
    @Bean
    public S3Client mockS3Client() {
        return Mockito.mock(S3Client.class);
    }
}
