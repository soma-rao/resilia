package com.resilia.dto;

public class RunResultDTO {
    private Long id;
    private Long runId;
    private String name;
    private String type;
    private String fileUrl;
    private Long fileSizeBytes;
    private java.time.LocalDateTime createdTime = java.time.LocalDateTime.now();

    public RunResultDTO() {
    }

    public RunResultDTO(Long id, Long runId, String name, String type, String fileUrl, Long fileSizeBytes) {
        this.id = id;
        this.runId = runId;
        this.name = name;
        this.type = type;
        this.fileUrl = fileUrl;
        this.fileSizeBytes = fileSizeBytes;
        this.createdTime = java.time.LocalDateTime.now();
    }

    public RunResultDTO(Long id, Long runId, String name, String type, String fileUrl, Long fileSizeBytes, java.time.LocalDateTime createdTime) {
        this.id = id;
        this.runId = runId;
        this.name = name;
        this.type = type;
        this.fileUrl = fileUrl;
        this.fileSizeBytes = fileSizeBytes;
        this.createdTime = createdTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRunId() {
        return runId;
    }

    public void setRunId(Long runId) {
        this.runId = runId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public void setFileSizeBytes(Long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public java.time.LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(java.time.LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
}
