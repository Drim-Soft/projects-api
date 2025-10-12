package com.projectsapi.projectsapi.model;

import jakarta.persistence.*;
import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonFormat;


import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "Project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer IDProject;

    @ManyToOne
    @JoinColumn(name = "IDMethodology", referencedColumnName = "IDMethodology")
    private Methodology methodology;

    @ManyToOne
    @JoinColumn(name = "IDProjectStatus", referencedColumnName = "IDProjectStatus")
    private ProjectStatus projectStatus;

    private String name;
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Timestamp startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Timestamp endDate;

    private Integer percentageProgress;
    private Integer budget;
    private Integer cost;
    private Integer percentageBudgetExecution;

    @Transient
    @JsonProperty("IDMethodologyRef")
    private Integer IDMethodologyRef;

    @Transient
    @JsonProperty("IDProjectStatusRef")
    private Integer IDProjectStatusRef;

    // ====== Getters y Setters ======
    public Integer getIDProject() { return IDProject; }
    public void setIDProject(Integer IDProject) { this.IDProject = IDProject; }

    public Methodology getMethodology() { return methodology; }
    public void setMethodology(Methodology methodology) { this.methodology = methodology; }

    public ProjectStatus getProjectStatus() { return projectStatus; }
    public void setProjectStatus(ProjectStatus projectStatus) { this.projectStatus = projectStatus; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Timestamp getStartDate() { return startDate; }
    public void setStartDate(Timestamp startDate) { this.startDate = startDate; }

    public Timestamp getEndDate() { return endDate; }
    public void setEndDate(Timestamp endDate) { this.endDate = endDate; }

    public Integer getPercentageProgress() { return percentageProgress; }
    public void setPercentageProgress(Integer percentageProgress) { this.percentageProgress = percentageProgress; }

    public Integer getBudget() { return budget; }
    public void setBudget(Integer budget) { this.budget = budget; }

    public Integer getCost() { return cost; }
    public void setCost(Integer cost) { this.cost = cost; }

    public Integer getPercentageBudgetExecution() { return percentageBudgetExecution; }
    public void setPercentageBudgetExecution(Integer percentageBudgetExecution) { this.percentageBudgetExecution = percentageBudgetExecution; }

    public Integer getIDMethodologyRef() { return IDMethodologyRef; }
    public void setIDMethodologyRef(Integer IDMethodologyRef) { this.IDMethodologyRef = IDMethodologyRef; }

    public Integer getIDProjectStatusRef() { return IDProjectStatusRef; }
    public void setIDProjectStatusRef(Integer IDProjectStatusRef) { this.IDProjectStatusRef = IDProjectStatusRef; }
}
