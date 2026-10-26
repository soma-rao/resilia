package com.resilia.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "projects")
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String domainName;

    @Column
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    public ProjectEntity() {
    }

    public ProjectEntity(Long id, String name, String domainName, String description) {
        this.id = id;
        this.name = name;
        this.domainName = domainName;
        this.description = description;
        this.active = true;
    }

    public ProjectEntity(Long id, String name, String domainName, String description, boolean active) {
        this.id = id;
        this.name = name;
        this.domainName = domainName;
        this.description = description;
        this.active = active;
    }

    public ProjectEntity(String name, String domainName, String description) {
        this.name = name;
        this.domainName = domainName;
        this.description = description;
        this.active = true;
    }

    public ProjectEntity(String name, String domainName, String description, boolean active) {
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
