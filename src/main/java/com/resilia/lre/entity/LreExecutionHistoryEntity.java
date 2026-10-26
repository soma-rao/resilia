package com.resilia.lre.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lre_execution_history")
public class LreExecutionHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "saved_config_id")
    private LreSavedRunConfigEntity savedConfig;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "server_config_id", nullable = false)
    private LreServerConfigEntity serverConfig;

    @Column(nullable = false)
    private String domainName;

    @Column(nullable = false)
    private String projectName;

    @Column(nullable = false)
    private Long testId;

    @Column
    private String testName;

    @Column
    private Long runId;

    @Column(nullable = false)
    private String status = "INITIALIZING";

    @Column
    private Integer vusers;

    @Column
    private Integer durationInMinutes;

    @Column
    private Long timeslotId;

    @Column
    private String postRunAction;

    @Column(length = 2000)
    private String executionMessage;

    @Column
    private LocalDateTime initiatedAt = LocalDateTime.now();

    @Column
    private LocalDateTime completedAt;

    public LreExecutionHistoryEntity() {
    }

    public LreExecutionHistoryEntity(LreSavedRunConfigEntity savedConfig, LreServerConfigEntity serverConfig, String domainName, String projectName, Long testId, String testName, Integer vusers, Integer durationInMinutes, Long timeslotId, String postRunAction) {
        this.savedConfig = savedConfig;
        this.serverConfig = serverConfig;
        this.domainName = domainName;
        this.projectName = projectName;
        this.testId = testId;
        this.testName = testName;
        this.vusers = vusers;
        this.durationInMinutes = durationInMinutes;
        this.timeslotId = timeslotId;
        this.postRunAction = postRunAction;
        this.status = "INITIALIZING";
        this.initiatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LreSavedRunConfigEntity getSavedConfig() {
        return savedConfig;
    }

    public void setSavedConfig(LreSavedRunConfigEntity savedConfig) {
        this.savedConfig = savedConfig;
    }

    public LreServerConfigEntity getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(LreServerConfigEntity serverConfig) {
        this.serverConfig = serverConfig;
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