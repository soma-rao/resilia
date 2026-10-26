package com.resilia.dto;

public class LoadGeneratorDTO {
    private Long id;
    private String name;
    private String domain;
    private String project;
    private String hostName;
    private String ipAddress;
    private String status;
    private String location;
    private Integer port = 443;
    private Integer capacity = 1000;

    public LoadGeneratorDTO() {
    }

    public LoadGeneratorDTO(Long id, String name, String domain, String project, String hostName, String ipAddress, String status, String location) {
        this.id = id;
        this.name = name;
        this.domain = domain;
        this.project = project;
        this.hostName = hostName;
        this.ipAddress = ipAddress;
        this.status = status;
        this.location = location;
        this.port = 443;
        this.capacity = 1000;
    }

    public LoadGeneratorDTO(Long id, String name, String domain, String project, String hostName, String ipAddress, String status, String location, Integer port, Integer capacity) {
        this.id = id;
        this.name = name;
        this.domain = domain;
        this.project = project;
        this.hostName = hostName;
        this.ipAddress = ipAddress;
        this.status = status;
        this.location = location;
        this.port = port;
        this.capacity = capacity;
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

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHost() {
        return hostName != null ? hostName : ipAddress;
    }

    public void setHost(String host) {
        this.hostName = host;
        if (this.ipAddress == null) {
            this.ipAddress = host;
        }
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}
