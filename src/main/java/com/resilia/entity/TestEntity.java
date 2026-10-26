package com.resilia.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tests")
public class TestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String domain;

    @Column(nullable = false)
    private String project;

    @Column
    private String testType;

    @Column
    private String testFolder;

    @Column
    private String description;

    @Column
    private Integer durationMinutes;

    @Column
    private Integer vusers;

    public TestEntity() {
    }

    public TestEntity(Long id, String name, String domain, String project, String testType, String testFolder, String description, Integer durationMinutes, Integer vusers) {
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

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getVusers() {
        return vusers;
    }

    public void setVusers(Integer vusers) {
        this.vusers = vusers;
    }
}
