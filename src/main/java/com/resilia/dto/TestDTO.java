package com.resilia.dto;

public class TestDTO {
    private Long id;
    private String name;
    private String domain;
    private String project;
    private String testType;
    private String testFolder;
    private String description;
    private Integer durationMinutes;
    private Integer vusers;

    public TestDTO() {
    }

    public TestDTO(Long id, String name, String domain, String project, String testType, String testFolder, String description, Integer durationMinutes, Integer vusers) {
        this.id = id;
        this.name = name;
        this.domain = domain;
        this.project = project;
        this.testType = testType;
        this.testFolder = testFolder;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.vusers = vusers;
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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getType() {
        return testType;
    }

    public void setType(String type) {
        this.testType = type;
    }

    public String getTestFolder() {
        return testFolder;
    }

    public void setTestFolder(String testFolder) {
        this.testFolder = testFolder;
    }

    public String getFolderPath() {
        return testFolder;
    }

    public void setFolderPath(String folderPath) {
        this.testFolder = folderPath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getDurationInMinutes() {
        return durationMinutes;
    }

    public void setDurationInMinutes(Integer durationInMinutes) {
        this.durationMinutes = durationInMinutes;
    }

    public Integer getVusers() {
        return vusers;
    }

    public void setVusers(Integer vusers) {
        this.vusers = vusers;
    }
}
