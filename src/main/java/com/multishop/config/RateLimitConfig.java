// src/main/java/com/multishop/config/RateLimitConfig.java
package com.multishop.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "platform.rate-limit")
public class RateLimitConfig {

    private int douyinRequestsPerSecond = 10;  // 抖音每秒请求数限制
    private int pddRequestsPerSecond = 5;      // 拼多多每秒请求数限制

    // Getters and Setters
    public int getDouyinRequestsPerSecond() {
        return douyinRequestsPerSecond;
    }

    public void setDouyinRequestsPerSecond(int douyinRequestsPerSecond) {
        this.douyinRequestsPerSecond = douyinRequestsPerSecond;
    }

    public int getPddRequestsPerSecond() {
        return pddRequestsPerSecond;
    }

    public void setPddRequestsPerSecond(int pddRequestsPerSecond) {
        this.pddRequestsPerSecond = pddRequestsPerSecond;
    }
}