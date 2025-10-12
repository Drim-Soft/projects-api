package com.projectsapi.projectsapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ProjectStatus")
public class ProjectStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer IDProjectStatus;

    @Column(nullable = false)
    private String name;

    public Integer getIDProjectStatus() {
        return IDProjectStatus;
    }

    public void setIDProjectStatus(Integer IDProjectStatus) {
        this.IDProjectStatus = IDProjectStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
