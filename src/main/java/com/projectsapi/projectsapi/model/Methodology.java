package com.projectsapi.projectsapi.model;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "Methodology")
public class Methodology {


    @Id
    @Column(name = "IDMethodology")
    private Integer IDMethodology;

    @Column(name = "name")
    private String name;


    @OneToMany(mappedBy = "methodology")
    @JsonIgnore 
    private List<Role> roles;


    public Integer getIDMethodology() {
        return IDMethodology;
    }

    public void setIDMethodology(Integer IDMethodology) {
        this.IDMethodology = IDMethodology;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
