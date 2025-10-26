package com.projectsapi.projectsapi.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "PublicMessage")
public class PublicMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer IDPublicMessage;

    @ManyToOne
    @JoinColumn(name = "IDTask", referencedColumnName = "IDTask")
    private Task task;

    private Integer IDUser;
    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;

    @Transient
    @JsonProperty("IDTaskRef")
    private Integer IDTaskRef;

    // Constructor por defecto
    public PublicMessage() {}

    // Constructor con parámetros
    public PublicMessage(Integer IDUser, Integer IDTaskRef, String content) {
        this.IDUser = IDUser;
        this.IDTaskRef = IDTaskRef;
        this.content = content;
        this.date = LocalDateTime.now();
    }

    // Getters y setters
    public Integer getIDPublicMessage() { 
        return IDPublicMessage; 
    }
    
    public void setIDPublicMessage(Integer IDPublicMessage) { 
        this.IDPublicMessage = IDPublicMessage; 
    }

    public Task getTask() { 
        return task; 
    }
    
    public void setTask(Task task) { 
        this.task = task; 
    }

    public Integer getIDUser() { 
        return IDUser; 
    }
    
    public void setIDUser(Integer IDUser) { 
        this.IDUser = IDUser; 
    }

    public String getContent() { 
        return content; 
    }
    
    public void setContent(String content) { 
        this.content = content; 
    }

    public LocalDateTime getDate() { 
        return date; 
    }
    
    public void setDate(LocalDateTime date) { 
        this.date = date; 
    }

    public Integer getIDTaskRef() { 
        return IDTaskRef; 
    }
    
    public void setIDTaskRef(Integer IDTaskRef) { 
        this.IDTaskRef = IDTaskRef; 
    }
}
