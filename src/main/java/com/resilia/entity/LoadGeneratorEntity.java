package com.resilia.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "load_generators")
public class LoadGeneratorEntity {

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
    private String hostName;

    @Column
    private String ipAddress;

    @Column(nullable = false)
    private String status;

    @Column
    private String location;

    @Column
    private Integer port = 443;

    @Column
    private Integer capacity = 1000;

    public LoadGeneratorEntity() {
    }

    public LoadGeneratorEntity(Long id, String name, String domain, String project, String hostName, String ipAddress, String status, String location) {
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

    public LoadGeneratorEntity(Long id, String name, String domain, String project, String hostName, String ipAddress, String status, String location, Integer port, Integer capacity) {
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

    public LoadGeneratorEntity(String name, String domain, String project, String hostName, String ipAddress, String status, String location) {
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

    public LoadGeneratorEntity(String name, String domain, String project, String hostName, String ipAddress, String status, String location, Integer port, Integer capacity) {
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
