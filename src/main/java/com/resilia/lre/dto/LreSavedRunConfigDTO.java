package com.resilia.lre.dto;

import java.time.LocalDateTime;

public class LreSavedRunConfigDTO {
    private Long id;
    private String profileName;
    private Long serverConfigId;
    private String serverConfigName;
    private String serverBaseUrl;
    private String domainName;
    private String projectName;
    private Long testId;
    private String testName;
    private Long testInstanceId;
    private Long timeslotId;
    private Integer durationInMinutes = 30;
    private Integer vusers = 100;
    private String postRunAction = "Collate And Analyze";
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime lastExecutedAt;
    private String lastExecutionStatus;

    public LreSavedRunConfigDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
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

    public String getServerBaseUrl() {
        return serverBaseUrl;
    }

    public void setServerBaseUrl(String serverBaseUrl) {
        this.serverBaseUrl = serverBaseUrl;
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

    public Long getTestInstanceId() {
        return testInstanceId;
    }

    public void setTestInstanceId(Long testInstanceId) {
        this.testInstanceId = testInstanceId;
    }

    public Long getTimeslotId() {
        return timeslotId;
    }

    public void setTimeslotId(Long timeslotId) {
        this.timeslotId = timeslotId;
    }

    public Integer getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(Integer durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public Integer getVusers() {
        return vusers;
    }

    public void setVusers(Integer vusers) {
        this.vusers = vusers;
    }

    public String getPostRunAction() {
        return postRunAction;
    }

    public void setPostRunAction(String postRunAction) {
        this.postRunAction = postRunAction;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastExecutedAt() {
        return lastExecutedAt;
    }

    public void setLastExecutedAt(LocalDateTime lastExecutedAt) {
        this.lastExecutedAt = lastExecutedAt;
    }

    public String getLastExecutionStatus() {
        return lastExecutionStatus;
    }

    public void setLastExecutionStatus(String lastExecutionStatus) {
        this.lastExecutionStatus = lastExecutionStatus;
    }

    @Override
    public String toString() {
        return profileName + " [" + domainName + "/" + projectName + " - Test #" + testId + "]";
    }
}