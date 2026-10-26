package com.resilia.dto;

import java.util.ArrayList;
import java.util.List;

public class TestSetDTO {
    private Long id;
    private String name;
    private String domain;
    private String project;
    private String description;
    private String folderPath;
    private List<Long> testIds = new ArrayList<>();

    public TestSetDTO() {
    }

    public TestSetDTO(Long id, String name, String domain, String project, String description) {
        this.id = id;
        this.name = name;
        this.domain = domain;
        this.project = project;
        this.description = description;
    }

    public TestSetDTO(Long id, String name, String domain, String project, String description, String folderPath, List<Long> testIds) {
        this.id = id;
        this.name = name;
        this.domain = domain;
        this.project = project;
        this.description = description;
        this.folderPath = folderPath;
        this.testIds = testIds != null ? testIds : new ArrayList<>();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public List<Long> getTestIds() {
        return testIds;
    }

    public void setTestIds(List<Long> testIds) {
        this.testIds = testIds != null ? testIds : new ArrayList<>();
    }
}
