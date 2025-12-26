// src/main/java/com/multishop/model/Store.java
package com.multishop.model;

import jakarta.persistence.*;

@Entity
@Table(name = "stores")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform")
    private Platform platform;

    @Column(name = "platform_store_id")
    private String platformStoreId;

    @Column(name = "name")
    private String name;

    @Column(name = "credentials_ref")
    private String credentialsRef;

    @Column(name = "enabled")
    private Boolean enabled;

    // Constructors
    public Store() {}

    public Store(Platform platform, String platformStoreId, String name, String credentialsRef, Boolean enabled) {
        this.platform = platform;
        this.platformStoreId = platformStoreId;
        this.name = name;
        this.credentialsRef = credentialsRef;
        this.enabled = enabled;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Platform getPlatform() { return platform; }
    public void setPlatform(Platform getPlatform) { this.platform = getPlatform; }

    public String getPlatformStoreId() { return platformStoreId; }
    public void setPlatformStoreId(String platformStoreId) { this.platformStoreId = platformStoreId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCredentialsRef() { return credentialsRef; }
    public void setCredentialsRef(String credentialsRef) { this.credentialsRef = credentialsRef; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public enum Platform {
        DOUYIN, PDD
    }
}