package com.projectsapi.projectsapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer IDRole;

    @ManyToOne
    @JoinColumn(name = "IDMethodology")
    private Methodology methodology;

    @Column(nullable = false)
    private String name;

    public Integer getIDRole() {
        return IDRole;
    }

    public void setIDRole(Integer IDRole) {
        this.IDRole = IDRole;
    }

    public Methodology getMethodology() {
        return methodology;
    }

    public void setMethodology(Methodology methodology) {
        this.methodology = methodology;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
