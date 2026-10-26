package com.resilia.lre.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LreRunResponseDTO {
    @JsonProperty("RunID")
    private Long runId;

    @JsonProperty("ID")
    private Long id;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("Message")
    private String message;

    public LreRunResponseDTO() {
    }

    public LreRunResponseDTO(Long runId, String status) {
        this.runId = runId;
        this.status = status;
    }

    public Long getRunId() {
        return runId != null ? runId : id;
    }

    public void setRunId(Long runId) {
        this.runId = runId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}