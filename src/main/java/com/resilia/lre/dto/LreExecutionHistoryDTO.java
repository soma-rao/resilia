package com.resilia.lre.dto;

import java.time.LocalDateTime;

public class LreExecutionHistoryDTO {
    private Long id;
    private Long savedConfigId;
    private String savedProfileName;
    private Long serverConfigId;
    private String serverConfigName;
    private String domainName;
    private String projectName;
    private Long testId;
    private String testName;
    private Long runId;
    private String status;
    private Integer vusers;
    private Integer durationInMinutes;
    private Long timeslotId;
    private String postRunAction;
    private String executionMessage;
    private LocalDateTime initiatedAt;
    private LocalDateTime completedAt;

    public LreExecutionHistoryDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSavedConfigId() {
        return savedConfigId;
    }

    public void setSavedConfigId(Long savedConfigId) {
        this.savedConfigId = savedConfigId;
    }

    public String getSavedProfileName() {
        return savedProfileName;
    }

    public void setSavedProfileName(String savedProfileName) {
        this.savedProfileName = savedProfileName;
    }

    public Long getServerConfigId() {
        return serverConfigId;
    }

    public void setServerConfigId(Long serverConfigId) {
        this.serverConfigId = serverConfigId;
    }

    public String getServerConfigName() {
        return serverConfigName;
    }

    public void setServerConfigName(String serverConfigName) {
        this.serverConfigName = serverConfigName;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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

    public Long getRunId() {
        return runId;
    }

    public void setRunId(Long runId) {
        this.runId = runId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getExecutionMessage() {
        return executionMessage;
    }

    public void setExecutionMessage(String executionMessage) {
        this.executionMessage = executionMessage;
    }

    public LocalDateTime getInitiatedAt() {
        return initiatedAt;
    }

    public void setInitiatedAt(LocalDateTime initiatedAt) {
        this.initiatedAt = initiatedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}