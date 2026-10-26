package com.resilia.lre.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LreAuthRequestDTO {
    @JsonProperty("Username")
    private String username;

    @JsonProperty("Password")
    private String password;

    @JsonProperty("ClientID")
    private String clientId;

    @JsonProperty("SecretKey")
    private String secretKey;

    public LreAuthRequestDTO() {
    }

    public LreAuthRequestDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public LreAuthRequestDTO(String clientId, String secretKey, boolean isOauth) {
        this.clientId = clientId;
        this.secretKey = secretKey;
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

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}