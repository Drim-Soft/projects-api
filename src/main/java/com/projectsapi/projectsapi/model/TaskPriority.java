package com.projectsapi.projectsapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "TaskPriority")
public class TaskPriority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer IDTaskPriority;

    private String name;

    // Getters y setters
    public Integer getIDTaskPriority() { return IDTaskPriority; }
    public void setIDTaskPriority(Integer IDTaskPriority) { this.IDTaskPriority = IDTaskPriority; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
