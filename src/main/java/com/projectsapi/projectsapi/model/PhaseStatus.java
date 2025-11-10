package com.projectsapi.projectsapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "PhaseStatus")
public class PhaseStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer IDPhaseStatus;

    private String name;

    // Getters y setters
    public Integer getIDPhaseStatus() { return IDPhaseStatus; }
    public void setIDPhaseStatus(Integer IDPhaseStatus) { this.IDPhaseStatus = IDPhaseStatus; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
