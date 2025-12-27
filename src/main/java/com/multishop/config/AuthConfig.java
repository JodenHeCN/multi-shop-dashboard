// src/main/java/com/multishop/config/AuthConfig.java
package com.multishop.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "platform.auth")
public class AuthConfig {

    private String douyinAppKey;
    private String douyinAppSecret;
    private String pddClientId;
    private String pddClientSecret;

    // Getters and Setters
    public String getDouyinAppKey() {
        return douyinAppKey;
    }

    public void setDouyinAppKey(String douyinAppKey) {
        this.douyinAppKey = douyinAppKey;
    }

    public String getDouyinAppSecret() {
        return douyinAppSecret;
    }

    public void setDouyinAppSecret(String douyinAppSecret) {
        this.douyinAppSecret = douyinAppSecret;
    }

    public String getPddClientId() {
        return pddClientId;
    }

    public void setPddClientId(String pddClientId) {
        this.pddClientId = pddClientId;
    }

    public String getPddClientSecret() {
        return pddClientSecret;
    }

    public void setPddClientSecret(String pddClientSecret) {
        this.pddClientSecret = pddClientSecret;
    }
}