package com.resilia.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "run_results")
public class RunResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long runId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column
    private String fileUrl;

    @Column
    private Long fileSizeBytes;

    public RunResultEntity() {
    }

    public RunResultEntity(Long id, Long runId, String name, String type, String fileUrl, Long fileSizeBytes) {
        this.id = id;
        this.runId = runId;
        this.name = name;
        this.type = type;
        this.fileUrl = fileUrl;
        this.fileSizeBytes = fileSizeBytes;
    }

    public RunResultEntity(Long runId, String name, String type, String fileUrl, Long fileSizeBytes) {
        this.runId = runId;
        this.name = name;
        this.type = type;
        this.fileUrl = fileUrl;
        this.fileSizeBytes = fileSizeBytes;
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
}
