package com.resilia.dto;

import java.time.LocalDateTime;

public class TimeslotDTO {
    private Long id;
    private String name;
    private String domain;
    private String project;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationMinutes;
    private Integer vusersCount;
    private String status;

    public TimeslotDTO() {
    }

    public TimeslotDTO(Long id, String name, String domain, String project, LocalDateTime startTime, Integer durationMinutes, Integer vusersCount, String status) {
        this.id = id;
        this.name = name;
        this.domain = domain;
        this.project = project;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
        this.vusersCount = vusersCount;
        this.status = status;
        if (startTime != null && durationMinutes != null) {
            this.endTime = startTime.plusMinutes(durationMinutes);
        }
    }

    public TimeslotDTO(Long id, String name, String domain, String project, LocalDateTime startTime, LocalDateTime endTime, Integer durationMinutes, Integer vusersCount, String status) {
        this.id = id;
        this.name = name;
        this.domain = domain;
        this.project = project;
        this.startTime = startTime;
        this.endTime = endTime;
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
        if (startTime != null && durationMinutes != null && endTime == null) {
            this.endTime = startTime.plusMinutes(durationMinutes);
        }
    }

    public LocalDateTime getEndTime() {
        if (endTime == null && startTime != null && durationMinutes != null) {
            return startTime.plusMinutes(durationMinutes);
        }
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        if (startTime != null && endTime != null) {
            this.durationMinutes = (int) java.time.Duration.between(startTime, endTime).toMinutes();
        }
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
        if (startTime != null && durationMinutes != null) {
            this.endTime = startTime.plusMinutes(durationMinutes);
        }
    }

    public Integer getVusersCount() {
        return vusersCount;
    }

    public void setVusersCount(Integer vusersCount) {
        this.vusersCount = vusersCount;
    }

    public Integer getVusersAllocated() {
        return vusersCount;
    }

    public void setVusersAllocated(Integer vusersAllocated) {
        this.vusersCount = vusersAllocated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
