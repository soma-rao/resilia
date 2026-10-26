package com.resilia.lre.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LreTestDTO {
    @JsonProperty("ID")
    private Long id;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("TestType")
    private String testType;

    @JsonProperty("TestFolder")
    private String testFolder;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("Vusers")
    private Integer vusers;

    @JsonProperty("Duration")
    private Integer durationInMinutes;

    public LreTestDTO() {
    }

    public LreTestDTO(Long id, String name, String testType, String testFolder, String description, Integer vusers, Integer durationInMinutes) {
        this.id = id;
        this.name = name;
        this.testType = testType;
        this.testFolder = testFolder;
        this.description = description;
        this.vusers = vusers;
        this.durationInMinutes = durationInMinutes;
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

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getTestFolder() {
        return testFolder;
    }

    public void setTestFolder(String testFolder) {
        this.testFolder = testFolder;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getVusers() {
        return vusers;
    }

    public void setVusers(Integer vusers) {
        this.vusers = vusers;
    }

    public Integer getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(Integer durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + name + (testType != null ? " (" + testType + ")" : "");
    }
}