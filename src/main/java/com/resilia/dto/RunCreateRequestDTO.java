package com.resilia.dto;

public class RunCreateRequestDTO {
    private Long testId;
    private Long testInstanceId;
    private Long timeslotId;
    private String postRunAction;
    private Integer durationInMinutes;
    private Integer vusers;

    public RunCreateRequestDTO() {
    }

    public RunCreateRequestDTO(Long testId, Long testInstanceId, Long timeslotId, String postRunAction) {
        this.testId = testId;
        this.testInstanceId = testInstanceId;
        this.timeslotId = timeslotId;
        this.postRunAction = postRunAction;
    }

    public RunCreateRequestDTO(Long testId, Long testInstanceId, Long timeslotId, String postRunAction, Integer durationInMinutes, Integer vusers) {
        this.testId = testId;
        this.testInstanceId = testInstanceId;
        this.timeslotId = timeslotId;
        this.postRunAction = postRunAction;
        this.durationInMinutes = durationInMinutes;
        this.vusers = vusers;
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

    public String getPostRunAction() {
        return postRunAction;
    }

    public void setPostRunAction(String postRunAction) {
        this.postRunAction = postRunAction;
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
}
