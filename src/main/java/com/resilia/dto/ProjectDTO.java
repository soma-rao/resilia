package com.resilia.dto;

public class ProjectDTO {
    private Long id;
    private String name;
    private String domainName;
    private String description;
    private boolean active = true;

    public ProjectDTO() {
    }

    public ProjectDTO(Long id, String name, String domainName, String description) {
        this.id = id;
        this.name = name;
        this.domainName = domainName;
        this.description = description;
        this.active = true;
    }

    public ProjectDTO(Long id, String name, String domainName, String description, boolean active) {
        this.id = id;
        this.name = name;
        this.domainName = domainName;
        this.description = description;
        this.active = active;
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

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
