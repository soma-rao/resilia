package com.resilia.lre.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LreProjectDTO {
    @JsonProperty("Name")
    private String name;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("Domain")
    private String domain;

    public LreProjectDTO() {
    }

    public LreProjectDTO(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public LreProjectDTO(String name, String description, String domain) {
        this.name = name;
        this.description = description;
        this.domain = domain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String toString() {
        return name;
    }
}