package com.projectsapi.projectsapi.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "Task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer IDTask;

    @ManyToOne
    @JoinColumn(name = "IDPhase", referencedColumnName = "IDPhase")
    private Phase phase;

    @ManyToOne
    @JoinColumn(name = "IDTaskStatus", referencedColumnName = "IDTaskStatus")
    private TaskStatus taskStatus;

    @ManyToOne
    @JoinColumn(name = "IDTaskPriority", referencedColumnName = "IDTaskPriority")
    private TaskPriority taskPriority;

    private Integer IDUser;
    private String name;
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private Integer timeInvested;
    private Integer percentageProgress;
    private Integer budget;
    private Integer cost;
    private String fileURL;
    private Integer score;
    private String feedback;

    @Transient
    @JsonProperty("IDPhaseRef")
    private Integer IDPhaseRef;

    @Transient
    @JsonProperty("IDTaskStatusRef")
    private Integer IDTaskStatusRef;

    @Transient
    @JsonProperty("IDTaskPriorityRef")
    private Integer IDTaskPriorityRef;

    // Getters y setters
    public Integer getIDTask() { return IDTask; }
    public void setIDTask(Integer IDTask) { this.IDTask = IDTask; }

    public Phase getPhase() { return phase; }
    public void setPhase(Phase phase) { this.phase = phase; }

    public TaskStatus getTaskStatus() { return taskStatus; }
    public void setTaskStatus(TaskStatus taskStatus) { this.taskStatus = taskStatus; }

    public TaskPriority getTaskPriority() { return taskPriority; }
    public void setTaskPriority(TaskPriority taskPriority) { this.taskPriority = taskPriority; }

    public Integer getIDUser() { return IDUser; }
    public void setIDUser(Integer IDUser) { this.IDUser = IDUser; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Integer getTimeInvested() { return timeInvested; }
    public void setTimeInvested(Integer timeInvested) { this.timeInvested = timeInvested; }

    public Integer getPercentageProgress() { return percentageProgress; }
    public void setPercentageProgress(Integer percentageProgress) { this.percentageProgress = percentageProgress; }

    public Integer getBudget() { return budget; }
    public void setBudget(Integer budget) { this.budget = budget; }

    public Integer getCost() { return cost; }
    public void setCost(Integer cost) { this.cost = cost; }

    public String getFileURL() { return fileURL; }
    public void setFileURL(String fileURL) { this.fileURL = fileURL; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public Integer getIDPhaseRef() { return IDPhaseRef; }
    public void setIDPhaseRef(Integer IDPhaseRef) { this.IDPhaseRef = IDPhaseRef; }

    public Integer getIDTaskStatusRef() { return IDTaskStatusRef; }
    public void setIDTaskStatusRef(Integer IDTaskStatusRef) { this.IDTaskStatusRef = IDTaskStatusRef; }

    public Integer getIDTaskPriorityRef() { return IDTaskPriorityRef; }
    public void setIDTaskPriorityRef(Integer IDTaskPriorityRef) { this.IDTaskPriorityRef = IDTaskPriorityRef; }
}