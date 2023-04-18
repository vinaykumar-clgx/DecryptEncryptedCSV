package com.example.decrypt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@ConfigurationProperties(prefix = "encryptionservice")
@Configuration
@Validated
public class EncryptionServiceConfigurationProperties {
    private String url;
    private String keyName;
}
