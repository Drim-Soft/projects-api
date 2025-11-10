package com.projectsapi.projectsapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "TaskStatus")
public class TaskStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer IDTaskStatus;

    private String name;

    // Getters y setters
    public Integer getIDTaskStatus() { return IDTaskStatus; }
    public void setIDTaskStatus(Integer IDTaskStatus) { this.IDTaskStatus = IDTaskStatus; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
