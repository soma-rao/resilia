package com.resilia.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "test_runs")
public class RunEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long testId;

    @Column
    private String testName;

    @Column(nullable = false)
    private String domain;

    @Column(nullable = false)
    private String project;

    @Column(nullable = false)
    private String status;

    @Column
    private LocalDateTime startTime;

    @Column
    private LocalDateTime endTime;

    @Column
    private Integer durationSeconds;

    @Column
    private Integer vusersCount;

    @Column
    private Long timeslotId;

    @Column
    private String postRunAction;

    public RunEntity() {
    }

    public RunEntity(Long id, Long testId, String testName, String domain, String project, String status, LocalDateTime startTime, LocalDateTime endTime, Integer durationSeconds, Integer vusersCount, Long timeslotId) {
        this.id = id;
        this.testId = testId;
        this.testName = testName;
        this.domain = domain;
        this.project = project;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationSeconds = durationSeconds;
        this.vusersCount = vusersCount;
        this.timeslotId = timeslotId;
    }

    public RunEntity(Long id, Long testId, String testName, String domain, String project, String status, LocalDateTime startTime, LocalDateTime endTime, Integer durationSeconds, Integer vusersCount, Long timeslotId, String postRunAction) {
        this.id = id;
        this.testId = testId;
        this.testName = testName;
        this.domain = domain;
        this.project = project;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationSeconds = durationSeconds;
        this.vusersCount = vusersCount;
        this.timeslotId = timeslotId;
        this.postRunAction = postRunAction;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public Integer getVusersCount() {
        return vusersCount;
    }

    public void setVusersCount(Integer vusersCount) {
        this.vusersCount = vusersCount;
    }

    public Long getTimeslotId() {
        return timeslotId;
    }

    public void setTimeslotId(Long timeslotId) {
        this.timeslotId = timeslotId;
    }

    public String getPostRunAction() {
        return postRunAction;
    }

    public void setPostRunAction(String postRunAction) {
        this.postRunAction = postRunAction;
    }
}
