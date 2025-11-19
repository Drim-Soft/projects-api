package com.projectsapi.projectsapi.controller;

import com.projectsapi.projectsapi.model.Methodology;
import com.projectsapi.projectsapi.model.Role;
import com.projectsapi.projectsapi.repository.MethodologyRepository;
import com.projectsapi.projectsapi.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MethodologyController.class)
class MethodologyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MethodologyRepository methodologyRepository;

    @MockBean
    private RoleRepository roleRepository;

    private Methodology testMethodology;
    private Role testRole;

    @BeforeEach
    void setUp() {
        testMethodology = new Methodology();
        testMethodology.setIDMethodology(1);
        testMethodology.setName("Ágil");

        testRole = new Role();
        testRole.setIDRole(1);
        testRole.setName("Administrador");
    }

    @Test
    void testGetAll() throws Exception {
        when(methodologyRepository.findAll()).thenReturn(Arrays.asList(testMethodology));

        mockMvc.perform(get("/methodologies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Ágil"));

        verify(methodologyRepository).findAll();
    }

    @Test
    void testGetRolesByMethodology_Success() throws Exception {
        when(methodologyRepository.existsById(1)).thenReturn(true);
        when(roleRepository.findByMethodology_IDMethodology(1)).thenReturn(Arrays.asList(testRole));

        mockMvc.perform(get("/methodologies/1/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Administrador"));

        verify(methodologyRepository).existsById(1);
        verify(roleRepository).findByMethodology_IDMethodology(1);
    }

    @Test
    void testGetRolesByMethodology_NotFound() throws Exception {
        when(methodologyRepository.existsById(999)).thenReturn(false);

        mockMvc.perform(get("/methodologies/999/roles"))
                .andExpect(status().isNotFound());

        verify(methodologyRepository).existsById(999);
        verify(roleRepository, never()).findByMethodology_IDMethodology(anyInt());
    }
}

