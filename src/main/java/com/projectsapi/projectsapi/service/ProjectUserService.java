package com.projectsapi.projectsapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProjectUserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insertRelation(Integer idUser, Integer idRole, Integer idProject) {
        if (idUser == null || idRole == null || idProject == null) {
            throw new IllegalArgumentException(" Los campos IDUser, IDRole e IDProject son obligatorios.");
        }

        String sql = "INSERT INTO UserRoleProject (IDUser, IDRole, IDProject) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, idUser, idRole, idProject);
    }
}
