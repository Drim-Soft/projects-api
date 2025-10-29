package com.projectsapi.projectsapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "UserTask")
@IdClass(UserTaskId.class)
public class UserTask {

    @Id
    @Column(name = "IDUser")
    private Integer IDUser;

    @Id
    @Column(name = "IDTask")
    private Integer IDTask;

    @ManyToOne
    @JoinColumn(name = "IDTask", referencedColumnName = "IDTask", insertable = false, updatable = false)
    private Task task;

    // Constructor por defecto
    public UserTask() {}

    // Constructor con parámetros
    public UserTask(Integer IDUser, Integer IDTask) {
        this.IDUser = IDUser;
        this.IDTask = IDTask;
    }

    // Getters y setters
    public Integer getIDUser() { 
        return IDUser; 
    }
    
    public void setIDUser(Integer IDUser) { 
        this.IDUser = IDUser; 
    }

    public Integer getIDTask() { 
        return IDTask; 
    }
    
    public void setIDTask(Integer IDTask) { 
        this.IDTask = IDTask; 
    }

    public Task getTask() { 
        return task; 
    }
    
    public void setTask(Task task) { 
        this.task = task; 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserTask userTask = (UserTask) o;
        return IDUser.equals(userTask.IDUser) && IDTask.equals(userTask.IDTask);
    }

    @Override
    public int hashCode() {
        return IDUser.hashCode() + IDTask.hashCode();
    }

    @Override
    public String toString() {
        return "UserTask{" +
                "IDUser=" + IDUser +
                ", IDTask=" + IDTask +
                '}';
    }
}
