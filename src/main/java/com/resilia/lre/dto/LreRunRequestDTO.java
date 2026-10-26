package com.resilia.lre.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LreRunRequestDTO {
    @JsonProperty("TestID")
    private Long testId;

    @JsonProperty("TestInstanceID")
    private Long testInstanceId;

    @JsonProperty("TimeslotID")
    private Long timeslotId;

    @JsonProperty("VusersNum")
    private Integer vusersNum;

    @JsonProperty("Duration")
    private Integer durationInMinutes;

    @JsonProperty("PostRunAction")
    private String postRunAction;

    public LreRunRequestDTO() {
    }

    public LreRunRequestDTO(Long testId, Long testInstanceId, Long timeslotId, String postRunAction) {
        this.testId = testId;
        this.testInstanceId = testInstanceId;
        this.timeslotId = timeslotId;
        this.postRunAction = postRunAction;
    }

    public LreRunRequestDTO(Long testId, Long testInstanceId, Long timeslotId, Integer vusersNum, Integer durationInMinutes, String postRunAction) {
        this.testId = testId;
        this.testInstanceId = testInstanceId;
        this.timeslotId = timeslotId;
        this.vusersNum = vusersNum;
        this.durationInMinutes = durationInMinutes;
        this.postRunAction = postRunAction;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
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

    public Integer getVusersNum() {
        return vusersNum;
    }

    public void setVusersNum(Integer vusersNum) {
        this.vusersNum = vusersNum;
    }

    public Integer getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(Integer durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public String getPostRunAction() {
        return postRunAction;
    }

    public void setPostRunAction(String postRunAction) {
        this.postRunAction = postRunAction;
    }
}