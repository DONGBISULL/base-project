package com.demo.api.security.anotation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Setter
@Getter
@NoArgsConstructor
@ConfigurationProperties(prefix = "security.cors")
public class CorsProperties {

    private List<String> allowedOrigins;

}