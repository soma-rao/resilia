package com.resilia.lre.dto;

import java.time.LocalDateTime;

public class LreServerConfigDTO {
    private Long id;
    private String name;
    private String host;
    private int port = 443;
    private boolean useHttps = true;
    private String username;
    private String password;
    private String clientId;
    private String clientSecret;
    private String description;
    private boolean active = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public LreServerConfigDTO() {
    }

    public LreServerConfigDTO(Long id, String name, String host, int port, boolean useHttps, String username, String password, String clientId, String clientSecret, String description, boolean active) {
        this.id = id;
        this.name = name;
        this.host = host;
        this.port = port;
        this.useHttps = useHttps;
        this.username = username;
        this.password = password;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.description = description;
        this.active = active;
    }

    public String getBaseUrl() {
        String protocol = useHttps ? "https" : "http";
        return protocol + "://" + host + (port > 0 ? (":" + port) : "");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isUseHttps() {
        return useHttps;
    }

    public void setUseHttps(boolean useHttps) {
        this.useHttps = useHttps;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return name + " (" + getBaseUrl() + ")";
    }
}