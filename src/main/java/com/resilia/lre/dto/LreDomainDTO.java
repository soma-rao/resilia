package com.resilia.lre.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LreDomainDTO {
    @JsonProperty("Name")
    private String name;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("Projects")
    private List<LreProjectDTO> projects = new ArrayList<>();

    public LreDomainDTO() {
    }

    public LreDomainDTO(String name, String description) {
        this.name = name;
        this.description = description;
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

    public List<LreProjectDTO> getProjects() {
        return projects;
    }

    public void setProjects(List<LreProjectDTO> projects) {
        this.projects = projects;
    }

    @Override
    public String toString() {
        return name;
    }
}