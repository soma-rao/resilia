package com.resilia.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "timeslots")
public class TimeslotEntity {

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
    private LocalDateTime startTime;

    @Column
    private Integer durationMinutes;

    @Column
    private Integer vusersCount;

    @Column(nullable = false)
    private String status;

    public TimeslotEntity() {
    }

    public TimeslotEntity(Long id, String name, String domain, String project, LocalDateTime startTime, Integer durationMinutes, Integer vusersCount, String status) {
        this.id = id;
        this.name = name;
        this.domain = domain;
        this.project = project;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
        this.vusersCount = vusersCount;
        this.status = status;
    }

    public TimeslotEntity(String name, String domain, String project, LocalDateTime startTime, Integer durationMinutes, Integer vusersCount, String status) {
        this.name = name;
        this.domain = domain;
        this.project = project;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
        this.vusersCount = vusersCount;
        this.status = status;
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getVusersCount() {
        return vusersCount;
    }

    public void setVusersCount(Integer vusersCount) {
        this.vusersCount = vusersCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
