package com.resilia.lre.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lre_saved_run_configs")
public class LreSavedRunConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String profileName;

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
    private Long testInstanceId;

    @Column
    private Long timeslotId;

    @Column
    private Integer durationInMinutes = 30;

    @Column
    private Integer vusers = 100;

    @Column
    private String postRunAction = "Collate And Analyze";

    @Column
    private String description;

    @Column
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime lastExecutedAt;

    @Column
    private String lastExecutionStatus;

    public LreSavedRunConfigEntity() {
    }

    public LreSavedRunConfigEntity(String profileName, LreServerConfigEntity serverConfig, String domainName, String projectName, Long testId, String testName, Long testInstanceId, Long timeslotId, Integer durationInMinutes, Integer vusers, String postRunAction, String description) {
        this.profileName = profileName;
        this.serverConfig = serverConfig;
        this.domainName = domainName;
        this.projectName = projectName;
        this.testId = testId;
        this.testName = testName;
        this.testInstanceId = testInstanceId;
        this.timeslotId = timeslotId;
        this.durationInMinutes = durationInMinutes;
        this.vusers = vusers;
        this.postRunAction = postRunAction;
        this.description = description;
        this.createdAt = LocalDateTime.now();
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
}