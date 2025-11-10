package com.projectsapi.projectsapi.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "Phase")
public class Phase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer IDPhase;

    @ManyToOne
    @JoinColumn(name = "IDProject", referencedColumnName = "IDProject")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "IDPhaseStatus", referencedColumnName = "IDPhaseStatus")
    private PhaseStatus phaseStatus;

    private String name;
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private Integer percentageProgress;
    private Integer budget;
    private Integer cost;

    @Transient
    @JsonProperty("IDProjectRef")
    private Integer IDProjectRef;

    @Transient
    @JsonProperty("IDPhaseStatusRef")
    private Integer IDPhaseStatusRef;

    // Getters y setters
    public Integer getIDPhase() { return IDPhase; }
    public void setIDPhase(Integer IDPhase) { this.IDPhase = IDPhase; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public PhaseStatus getPhaseStatus() { return phaseStatus; }
    public void setPhaseStatus(PhaseStatus phaseStatus) { this.phaseStatus = phaseStatus; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Integer getPercentageProgress() { return percentageProgress; }
    public void setPercentageProgress(Integer percentageProgress) { this.percentageProgress = percentageProgress; }

    public Integer getBudget() { return budget; }
    public void setBudget(Integer budget) { this.budget = budget; }

    public Integer getCost() { return cost; }
    public void setCost(Integer cost) { this.cost = cost; }

    public Integer getIDProjectRef() { return IDProjectRef; }
    public void setIDProjectRef(Integer IDProjectRef) { this.IDProjectRef = IDProjectRef; }

    public Integer getIDPhaseStatusRef() { return IDPhaseStatusRef; }
    public void setIDPhaseStatusRef(Integer IDPhaseStatusRef) { this.IDPhaseStatusRef = IDPhaseStatusRef; }
}
