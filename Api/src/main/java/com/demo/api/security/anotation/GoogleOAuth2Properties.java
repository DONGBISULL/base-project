package com.demo.api.security.anotation;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.google")
public class GoogleOAuth2Properties {
    private List<String> scope;

    public List<String> getScope() {
        return scope;
    }

    public void setScope(List<String> scope) {
        this.scope = scope;
    }
}