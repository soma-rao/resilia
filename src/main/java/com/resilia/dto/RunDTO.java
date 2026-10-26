package com.resilia.dto;

import java.time.LocalDateTime;

public class RunDTO {
    private Long id;
    private Long testId;
    private String testName;
    private String domain;
    private String project;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationSeconds;
    private Integer vusersCount;
    private Long timeslotId;
    private String postRunAction;

    public RunDTO() {
    }

    public RunDTO(Long id, Long testId, String testName, String domain, String project, String status, LocalDateTime startTime, LocalDateTime endTime, Integer durationSeconds, Integer vusersCount, Long timeslotId) {
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

    public RunDTO(Long id, Long testId, String testName, String domain, String project, String status, LocalDateTime startTime, LocalDateTime endTime, Integer durationSeconds, Integer vusersCount, Long timeslotId, String postRunAction) {
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

    public Integer getVusers() {
        return vusersCount;
    }

    public void setVusers(Integer vusers) {
        this.vusersCount = vusers;
    }

    public Integer getDurationInMinutes() {
        return durationSeconds != null ? durationSeconds / 60 : null;
    }

    public void setDurationInMinutes(Integer durationInMinutes) {
        this.durationSeconds = durationInMinutes != null ? durationInMinutes * 60 : null;
    }
}
